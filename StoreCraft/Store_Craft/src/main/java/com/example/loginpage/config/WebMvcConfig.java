package com.example.loginpage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // `frontend.url` may hold a comma separated list so several allowed
        // origins (e.g. a Render host plus a preview deployment) can be
        // configured from a single environment variable.
        List<String> origins = new ArrayList<>();
        for (String candidate : Arrays.asList(frontendUrl.split(","))) {
            String trimmed = candidate.trim();
            if (!trimmed.isEmpty() && !trimmed.equals("*")) {
                origins.add(trimmed);
            }
        }
        if (origins.isEmpty()) {
            // No explicit origin configured: same-origin deployments do not need
            // CORS at all, so allow any origin via patterns.
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .exposedHeaders("Set-Cookie")
                    .allowCredentials(true)
                    .maxAge(3600);
            return;
        }

        registry.addMapping("/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
