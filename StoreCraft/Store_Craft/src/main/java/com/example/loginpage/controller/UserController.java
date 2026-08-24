package com.example.loginpage.controller;

import com.example.loginpage.model.User;
import com.example.loginpage.model.Role;
import com.example.loginpage.repository.IRoleRepository;
import com.example.loginpage.security.CookieAuthInterceptor;
import com.example.loginpage.service.impl.UserService;
import com.example.loginpage.util.Helper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class UserController {

    private final UserService service;
    private final IRoleRepository roleRepository;

    // Cookie settings
    private static final String SESSION_COOKIE_NAME = CookieAuthInterceptor.SESSION_COOKIE_NAME;
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24 hours in seconds

    public UserController(UserService service, IRoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    // ============ HELPER: Create session cookie ============

    private Cookie createSessionCookie(String userId) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, userId);
        cookie.setHttpOnly(true);       // Prevents JavaScript access (XSS protection)
        cookie.setPath("/");            // Available to all paths
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setSecure(false);        // Set to true in production (HTTPS only)
        // SameSite attribute will be added manually when adding the cookie to the response
        return cookie;
    }

    private Cookie createClearSessionCookie() {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);            // Immediately expires the cookie
        cookie.setSecure(false);
        return cookie;
    }

    // ============ ENDPOINTS ============

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserRequest request, HttpServletResponse httpResponse) {
        try {
            // Validate email using Helper
            if (Helper.isNullOrEmpty(request.email) || !Helper.isValidEmail(request.email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Please provide a valid email address."));
            }

            String roleName = request.roleName != null ? request.roleName.toUpperCase() : "BUYER";
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.findByName("BUYER").orElse(null));

            if (role == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid role specified."));
            }

            User user = new User.Builder()
                    .setEmail(request.email)
                    .setPassword(request.password)
                    .setFirstName(request.firstName)
                    .setLastName(request.lastName)
                    .setRole(role)
                    .build();

            User saved = service.create(user);

            // Set session cookie on successful registration
            httpResponse.addCookie(createSessionCookie(saved.getUserId()));

            Map<String, Object> response = new HashMap<>();
            response.put("userId", saved.getUserId());
            response.put("email", saved.getEmail());
            response.put("firstName", saved.getFirstName());
            response.put("lastName", saved.getLastName());
            response.put("roleName", saved.getRole().getName());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse httpResponse) {
        try {
            // Validate email using Helper
            if (Helper.isNullOrEmpty(request.email) || !Helper.isValidEmail(request.email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Please provide a valid email address."));
            }

            User user = service.findByEmail(request.email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found. Please create an account."));
            }

            if (!user.getPassword().equals(request.password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid email or password."));
            }

            // Set session cookie on successful login
            httpResponse.addCookie(createSessionCookie(user.getUserId()));

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("roleName", user.getRole().getName());
            response.put("isActive", user.getIsActive());
            response.put("profileImageUrl", user.getProfileImageUrl());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login error: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse httpResponse) {
        // Clear the session cookie
        httpResponse.addCookie(createClearSessionCookie());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully.", "success", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // The interceptor already validated the cookie and stored the user
        User user = (User) request.getAttribute(CookieAuthInterceptor.AUTHENTICATED_USER_ATTR);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("roleName", user.getRole().getName());
        response.put("isActive", user.getIsActive());
        response.put("profileImageUrl", user.getProfileImageUrl());
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable String id) {
        User user = service.read(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UserRequest request) {
        try {
            User existing = service.read(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }
            if (request.firstName != null) existing.setFirstName(request.firstName);
            if (request.lastName != null) existing.setLastName(request.lastName);
            if (request.email != null) existing.setEmail(request.email);
            if (request.password != null && !request.password.isEmpty()) existing.setPassword(request.password);

            User saved = service.update(existing);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", saved.getUserId());
            response.put("email", saved.getEmail());
            response.put("firstName", saved.getFirstName());
            response.put("lastName", saved.getLastName());
            response.put("roleName", saved.getRole().getName());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable String id) {
        User existing = service.read(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
        existing.setIsActive(false);
        service.update(existing);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable String id) {
        User existing = service.read(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
        existing.setIsActive(true);
        service.update(existing);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    @PostMapping("/upload-profile-image/{id}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            User existing = service.read(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "File is empty"));
            }

            // Save to local file system
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + filename;
            existing.setProfileImageUrl(fileUrl);
            service.update(existing);

            return ResponseEntity.ok(Map.of("profileImageUrl", fileUrl, "message", "Image uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload image: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class UserRequest {
        public String email;
        public String password;
        public String firstName;
        public String lastName;
        public String roleName;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}