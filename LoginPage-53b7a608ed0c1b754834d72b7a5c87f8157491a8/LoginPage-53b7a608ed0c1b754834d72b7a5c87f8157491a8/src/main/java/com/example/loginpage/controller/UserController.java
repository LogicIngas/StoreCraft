package com.example.loginpage.controller;

import com.example.loginpage.dto.UserRequestDTO;
import com.example.loginpage.dto.UserResponseDTO;
import com.example.loginpage.factory.UserFactory;
import com.example.loginpage.model.User;
import com.example.loginpage.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Register a new user
     * POST /user/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserRequestDTO requestDTO) {
        // Validate input
        if (requestDTO.email() == null || requestDTO.email().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        if (requestDTO.password() == null || requestDTO.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        // Check if user already exists
        User existingUser = service.findByEmail(requestDTO.email());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Email already registered. Please sign in.");
        }

        // Build and save user
        User userEntity = UserFactory.createUser(
                requestDTO.email(),
                requestDTO.password(),
                requestDTO.firstName(),
                requestDTO.lastName()
        );

        if (userEntity == null) {
            return ResponseEntity.badRequest().body("Required validation strings are empty.");
        }

        User savedUser = service.create(userEntity);

        UserResponseDTO response = new UserResponseDTO(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Sign in (login) existing user
     * POST /user/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        // Validate input
        if (loginDTO.email() == null || loginDTO.email().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        if (loginDTO.password() == null || loginDTO.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        // Find user by email
        User user = service.findByEmail(loginDTO.email());
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found. Please create an account.");
        }

        // Verify password (simple check - in production use BCrypt)
        if (!user.getPassword().equals(loginDTO.password())) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }

        // Return user data (without password)
        UserResponseDTO response = new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID
     * GET /user/read/{id}
     */
    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable String id) {
        User user = service.read(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserResponseDTO response = new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by ID
     * DELETE /user/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully.");
        }
        return ResponseEntity.badRequest().body("User not found.");
    }

    /**
     * DTO for login request
     */
    public record LoginRequestDTO(
            String email,
            String password
    ) {}
}