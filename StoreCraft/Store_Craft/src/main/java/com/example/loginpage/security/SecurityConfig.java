package com.example.loginpage.security;

import com.example.loginpage.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration using JWT.
 * Follows Mr Naidoo's coding style.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on controllers
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF — we're stateless (JWT, no sessions)
        http.csrf(AbstractHttpConfigurer::disable);

        // Disable CORS restriction for our React frontend
        http.cors(Customizer.withDefaults());

        // Stateless session — no HTTP session created
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // URL access rules
        http.authorizeHttpRequests(auth -> auth
                // Public: login, register, static assets, all product browsing, websockets
                .requestMatchers(
                        "/user/login", "/user/create",
                        "/uploads/**", "/images/**",
                        "/product/**",          // all product reading (write ops secured at method level)
                        "/ws/**", "/chat/**",   // WebSocket handshake endpoints
                        "/error"
                ).permitAll()
                // Everything else must carry a valid JWT
                .anyRequest().authenticated()
        );

        // Plug in our DB-backed auth provider
        http.authenticationProvider(authenticationProvider());

        // Run JwtFilter before Spring's default username/password filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // {noop} = no encoding — keeps plain-text passwords working for existing data
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
