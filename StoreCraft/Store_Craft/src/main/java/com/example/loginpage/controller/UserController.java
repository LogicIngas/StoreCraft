package com.example.loginpage.controller;

import com.example.loginpage.model.User;
import com.example.loginpage.model.Role;
import com.example.loginpage.repository.IRoleRepository;
import com.example.loginpage.repository.IUserRepository;
import com.example.loginpage.service.impl.EmailService;
import com.example.loginpage.service.impl.JWTService;
import com.example.loginpage.service.impl.UserService;
import com.example.loginpage.util.Helper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;
    private final IRoleRepository roleRepository;
    private final JWTService jwtService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final EmailService emailService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public UserController(UserService service, IRoleRepository roleRepository, JWTService jwtService,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                          IUserRepository userRepository, EmailService emailService) {
        this.service = service;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
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
                    .setPassword(passwordEncoder.encode(request.password))
                    .setFirstName(request.firstName)
                    .setLastName(request.lastName)
                    .setRole(role)
                    .build();

            User saved = service.create(user);

            // Generate JWT token so the user is immediately authenticated
            String token = jwtService.generateToken(saved.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("userId", saved.getUserId());
            response.put("email", saved.getEmail());
            response.put("firstName", saved.getFirstName());
            response.put("lastName", saved.getLastName());
            response.put("roleName", saved.getRole().getName());
            response.put("isActive", saved.getIsActive());
            response.put("profileImageUrl", saved.getProfileImageUrl());
            response.put("token", token); // JWT token for subsequent requests
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

            boolean matches;
            if (user.getPassword().startsWith("$2a$")) {
                matches = passwordEncoder.matches(request.password, user.getPassword());
            } else {
                matches = user.getPassword().equals(request.password);
                if (matches) {
                    // Seamless migration: hash plain text password on successful login
                    user.setPassword(passwordEncoder.encode(request.password));
                    service.update(user);
                }
            }

            if (!matches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid email or password."));
            }

            // Generate JWT token for the authenticated user
            String token = jwtService.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("roleName", user.getRole().getName());
            response.put("isActive", user.getIsActive());
            response.put("profileImageUrl", user.getProfileImageUrl());
            response.put("token", token); // JWT token for subsequent requests
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login error: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse httpResponse) {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully.", "success", true));
    }

    /**
     * Social / Magic-Link login bridge.
     * Called by the frontend after Supabase authenticates the user.
     * Finds or creates a BUYER account in our database, then returns our own JWT.
     */
    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request) {
        try {
            if (request.email == null || request.email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Email is required."));
            }

            User user = userRepository.findByEmail(request.email.trim());

            if (user == null) {
                // First time — create the user as BUYER automatically
                Role buyerRole = roleRepository.findByName("BUYER").orElse(null);
                if (buyerRole == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "BUYER role not found. Contact admin."));
                }
                String firstName = request.firstName != null ? request.firstName : request.email.split("@")[0];
                String lastName  = request.lastName  != null ? request.lastName  : "";

                user = new User.Builder()
                        .setEmail(request.email.trim())
                        .setPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setRole(buyerRole)
                        .build();
                user = service.create(user);
            }

            String token = jwtService.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("userId",          user.getUserId());
            response.put("email",           user.getEmail());
            response.put("firstName",       user.getFirstName());
            response.put("lastName",        user.getLastName());
            response.put("roleName",        user.getRole().getName());
            response.put("isActive",        user.getIsActive());
            response.put("profileImageUrl", user.getProfileImageUrl());
            response.put("token",           token);
            response.put("success",         true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Social login failed: " + e.getMessage()));
        }
    }

    /**
     * Forgot password — generates a reset token and emails a link to the user.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Always return success to prevent email enumeration
        try {
            if (request.email == null || request.email.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "If that email exists, a reset link has been sent.", "success", true));
            }
            User user = userRepository.findByEmail(request.email.trim());
            if (user != null) {
                String token = UUID.randomUUID().toString().replace("-", "");
                user.setResetToken(token);
                user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
                service.update(user);

                // Build the reset link pointing to the frontend
                String baseUrl = frontendUrl.contains(",")
                        ? frontendUrl.split(",")[0].trim()
                        : frontendUrl.trim();
                String resetLink = baseUrl + "?reset_token=" + token;

                emailService.sendPasswordResetEmail(
                        user.getEmail(),
                        user.getFirstName() != null ? user.getFirstName() : user.getEmail(),
                        resetLink
                );
            }
        } catch (Exception e) {
            // Log but never reveal whether the email exists
            System.err.println("Forgot-password error: " + e.getMessage());
        }
        return ResponseEntity.ok(Map.of("message", "If that email exists, a reset link has been sent.", "success", true));
    }

    /**
     * Reset password — validates the token and updates the user's password.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (request.token == null || request.newPassword == null || request.newPassword.length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Token and a password of at least 6 characters are required."));
            }
            User user = userRepository.findByResetToken(request.token.trim());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid or expired reset link."));
            }
            if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "This reset link has expired. Please request a new one."));
            }
            user.setPassword(passwordEncoder.encode(request.newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            service.update(user);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully! You can now log in.", "success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Reset failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Without cookies or interceptor, just check if user ID is in headers (for testing)
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated."));
        }
        User user = service.read(userId);
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
            if (request.password != null && !request.password.isEmpty()) {
                existing.setPassword(passwordEncoder.encode(request.password));
            }

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

            // Save to the configured upload directory (a mounted volume in
            // production, the working directory during local development)
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
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

    // -------- Request DTOs --------
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

    public static class SocialLoginRequest {
        public String email;
        public String firstName;
        public String lastName;
    }

    public static class ForgotPasswordRequest {
        public String email;
    }

    public static class ResetPasswordRequest {
        public String token;
        public String newPassword;
    }
}