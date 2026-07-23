package com.example.loginpage.controller;

import com.example.loginpage.model.User;
import com.example.loginpage.model.Role;
import com.example.loginpage.repository.IRoleRepository;
import com.example.loginpage.service.impl.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService service;
    private final IRoleRepository roleRepository;

    public UserController(UserService service, IRoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/create")
    public UserResponse create(@RequestBody UserRequest request) {
        String roleName = request.roleName != null ? request.roleName.toUpperCase() : "BUYER";
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("BUYER").orElse(null));

        if (role == null) {
            throw new RuntimeException("Invalid role specified.");
        }

        User user = new User.Builder()
                .setEmail(request.email)
                .setPassword(request.password)
                .setFirstName(request.firstName)
                .setLastName(request.lastName)
                .setRole(role)
                .build();

        User saved = service.create(user);
        return new UserResponse(saved);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        User user = service.findByEmail(request.email);
        if (user == null) {
            throw new RuntimeException("User not found. Please create an account.");
        }
        if (!user.getPassword().equals(request.password)) {
            throw new RuntimeException("Invalid email or password.");
        }
        return new UserResponse(user);
    }

    @GetMapping("/read/{id}")
    public User read(@PathVariable String id) {
        return service.read(id);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable String id) {
        return service.delete(id);
    }

    // ========== Inner DTOs ==========

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

    public static class UserResponse {
        public String userId;
        public String email;
        public String firstName;
        public String lastName;
        public String roleName;

        public UserResponse(User user) {
            this.userId = user.getUserId();
            this.email = user.getEmail();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.roleName = user.getRole().getName();
        }
    }
}