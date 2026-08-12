package com.example.loginpage.controller;

import com.example.loginpage.model.User;
import com.example.loginpage.model.Role;
import com.example.loginpage.repository.IRoleRepository;
import com.example.loginpage.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserController {

    private final UserService service;
    private final IRoleRepository roleRepository;

    public UserController(UserService service, IRoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserRequest request) {
        try {
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = service.findByEmail(request.email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found. Please create an account."));
            }

            if (!user.getPassword().equals(request.password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid email or password."));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("roleName", user.getRole().getName());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login error: " + e.getMessage()));
        }
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