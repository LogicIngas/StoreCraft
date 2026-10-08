package com.example.loginpage.service.impl;

import com.example.loginpage.model.User;
import com.example.loginpage.repository.IUserRepository;
import com.example.loginpage.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final IUserRepository repository;

    @Autowired
    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    // ---- Spring Security: load user by email (email = username) ----
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        // Map DB role name (e.g. "ADMIN") → Spring Security authority "ROLE_ADMIN"
        String authority = "ROLE_" + user.getRole().getName().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // BCryptPasswordEncoder is set in SecurityConfig
                List.of(new SimpleGrantedAuthority(authority))
        );
    }

    // ---- Standard CRUD ----

    @Override
    @Transactional
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public User update(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}