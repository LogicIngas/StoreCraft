#!/usr/bin/env bash
# =============================================================================
# Container entrypoint.
#
# Starts the Spring Boot API and the nginx reverse proxy, then supervises both.
# The script stays as PID 1 so a crashed JVM brings the whole container down and
# lets the orchestrator restart it, instead of leaving a proxy serving 502s.
# =============================================================================
set -euo pipefail

PORT="${PORT:-8080}"
SERVER_PORT="${SERVER_PORT:-8081}"
FILE_UPLOAD_DIR="${FILE_UPLOAD_DIR:-/var/data/uploads}"
JAVA_OPTS="${JAVA_OPTS:--XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom}"

# Declared up front because the shutdown trap can fire before the processes
# exist, and `set -u` would abort on an unset reference.
JAVA_PID=""
NGINX_PID=""

log() { printf '[start] %s\n' "$*"; }

terminate() {
    log "shutting down"
    [ -n "$NGINX_PID" ] && kill -TERM "$NGINX_PID" 2>/dev/null || true
    [ -n "$JAVA_PID" ] && kill -TERM "$JAVA_PID" 2>/dev/null || true
    wait 2>/dev/null || true
    exit 0
}
trap terminate TERM INT

# The upload directory may be a mounted volume, so it is (re)created at boot.
mkdir -p "$FILE_UPLOAD_DIR" /var/log/nginx /var/lib/nginx /var/run

log "rendering nginx config (public port ${PORT}, api port ${SERVER_PORT})"
sed -e "s|__PORT__|${PORT}|g" \
    -e "s|__BACKEND_PORT__|${SERVER_PORT}|g" \
    /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

# Fail fast and loudly rather than starting a proxy that cannot bind.
nginx -t

log "starting Spring Boot on port ${SERVER_PORT}"
# shellcheck disable=SC2086
java ${JAVA_OPTS} -jar /app/app.jar --server.port="${SERVER_PORT}" &
JAVA_PID=$!

# /actuator/health is permitted without authentication, so it doubles as a
# readiness probe while the JVM warms up.
log "waiting for the API to report healthy"
healthy=0
for _ in $(seq 1 90); do
    if ! kill -0 "$JAVA_PID" 2>/dev/null; then
        log "ERROR: the backend exited during startup - see the log above"
        wait "$JAVA_PID" 2>/dev/null || true
        exit 1
    fi
    if curl -fsS "http://127.0.0.1:${SERVER_PORT}/actuator/health" >/dev/null 2>&1; then
        healthy=1
        break
    fi
    sleep 2
done

if [ "$healthy" -eq 1 ]; then
    log "API is healthy"
else
    # Not fatal: the JVM is still running and may simply be slow to migrate the
    # schema. nginx will answer 502 until it is ready, and Render retries the
    # health check, so the deploy can still succeed.
    log "WARNING: the API did not become healthy within 3 minutes; continuing anyway"
fi

log "starting nginx on port ${PORT}"
nginx -g 'daemon off;' &
NGINX_PID=$!

# Returns as soon as either process exits, so a dead JVM or a dead proxy ends
# the container instead of leaving it half alive. `|| true` keeps `set -e` from
# exiting before the message is logged.
wait -n || true
log "ERROR: a supervised process exited unexpectedly"
terminate
