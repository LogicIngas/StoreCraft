package com.example.loginpage.security;

import com.example.loginpage.model.User;
import com.example.loginpage.service.impl.UserService;
import com.example.loginpage.util.Helper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * Interceptor that validates the STORECRAFT_SESSION cookie on every protected request.
 * 
 * Flow:
 * 1. Reads the STORECRAFT_SESSION cookie
 * 2. Looks up the user by ID stored in the cookie value
 * 3. Validates the user exists, is active, and has a valid email
 * 4. Checks @RoleRequired annotation for role-based access
 * 5. Stores the authenticated User in request attribute "authenticatedUser"
 */
@Component
public class CookieAuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_COOKIE_NAME = "STORECRAFT_SESSION";
    public static final String AUTHENTICATED_USER_ATTR = "authenticatedUser";

    private final UserService userService;

    public CookieAuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        // Allow preflight CORS requests through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Only intercept controller methods (not static resources)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // Skip auth for login and registration endpoints
        String path = request.getRequestURI();
        // Some deployments may prepend a context path; also allow substring match
        if (path.contains("/user/login") || path.contains("/user/create")) {
            return true;
        }

        // ---- Step 1: Read the session cookie ----
        String userId = getSessionCookieValue(request);
        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Authentication required. Please log in.\"}");
            return false;
        }

        // ---- Step 2: Look up the user ----
        User user = userService.read(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid session. Please log in again.\"}");
            return false;
        }

        // ---- Step 3: Validate user is active ----
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Account is deactivated. Contact support.\"}");
            return false;
        }

        // ---- Step 4: Validate email using Helper ----
        if (!Helper.isValidEmail(user.getEmail())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid email on account. Please update your profile.\"}");
            return false;
        }

        // ---- Step 5: Check @RoleRequired annotation ----
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Check method-level annotation first, then class-level
        RoleRequired roleRequired = handlerMethod.getMethodAnnotation(RoleRequired.class);
        if (roleRequired == null) {
            roleRequired = handlerMethod.getBeanType().getAnnotation(RoleRequired.class);
        }

        if (roleRequired != null) {
            String userRole = user.getRole().getName();
            String[] allowedRoles = roleRequired.value();
            boolean hasRole = Arrays.asList(allowedRoles).contains(userRole);

            if (!hasRole) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Access denied. Insufficient permissions.\"}");
                return false;
            }
        }

        // ---- Step 6: Store authenticated user in request ----
        request.setAttribute(AUTHENTICATED_USER_ATTR, user);
        return true;
    }

    /**
     * Extract the STORECRAFT_SESSION cookie value from the request.
     */
    private String getSessionCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
