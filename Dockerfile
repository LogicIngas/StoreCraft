# =============================================================================
# StoreCraft / AfriConnect - single image serving the React SPA and the
# Spring Boot API.
#
# Stage 1  builds the Vite bundle
# Stage 2  builds the Spring Boot jar
# Stage 3  runs nginx (static files + reverse proxy) alongside the JVM
#
# Serving both from one origin means the browser never makes a cross-origin
# request, so CORS is not involved and the app works on any Render hostname.
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1 - frontend build
# -----------------------------------------------------------------------------
FROM node:22-alpine AS frontend-build

WORKDIR /build/frontend

# Install dependencies first so the layer is reused while only sources change.
COPY StoreCraft/Store_Craft/frontend/package.json StoreCraft/Store_Craft/frontend/package-lock.json ./
RUN npm ci

COPY StoreCraft/Store_Craft/frontend/ ./

# VITE_API_URL is intentionally left empty: the SPA talks to its own origin
# and nginx proxies those requests to the API in stage 3.
ENV VITE_API_URL=""
RUN npm run build

# -----------------------------------------------------------------------------
# Stage 2 - backend build
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /build

# Copying the POM alone first keeps the dependency layer cached across source edits.
COPY StoreCraft/Store_Craft/pom.xml ./

# Resolve dependencies in their own layer for better caching. Failure is not
# fatal because the package step below fetches anything still missing.
RUN mvn -B -q dependency:go-offline || true

COPY StoreCraft/Store_Craft/src ./src

# Normalised to target/app.jar so the runtime COPY does not depend on the
# project version in pom.xml.
RUN mvn -B -q clean package -DskipTests \
    && mv "$(find target -maxdepth 1 -name '*.jar' ! -name '*.original' | head -n 1)" target/app.jar

# -----------------------------------------------------------------------------
# Stage 3 - runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

ENV DEBIAN_FRONTEND=noninteractive \
    TZ=UTC \
    PORT=8080 \
    SERVER_PORT=8081 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

# PORT          - port nginx binds to. Render injects PORT at runtime; the
#                 default keeps `docker run -p 8080:8080` working locally.
# SERVER_PORT   - internal port the JVM listens on, never exposed publicly.
# PROFILE       - application-prod.properties (quiet logging, env-driven DB).

RUN apt-get update \
    && apt-get install -y --no-install-recommends nginx curl \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /var/data/uploads

WORKDIR /app

COPY --from=backend-build /build/target/app.jar /app/app.jar
COPY --from=frontend-build /build/frontend/dist /usr/share/nginx/html
COPY docker/nginx.conf.template /etc/nginx/nginx.conf.template
COPY docker/start.sh /usr/local/bin/start.sh

RUN chmod +x /usr/local/bin/start.sh \
    && rm -f /etc/nginx/sites-enabled/default

EXPOSE 8080

# Reports healthy as soon as the reverse proxy is up. The JVM is validated
# separately through /actuator/health, which Render polls via healthCheckPath.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD curl -fsS "http://127.0.0.1:${PORT}/nginx-health" || exit 1

ENTRYPOINT ["/usr/local/bin/start.sh"]
