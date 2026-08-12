package com.example.loginpage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl, "http://localhost:5173", "http://localhost:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // String absoluteUploadPath = "C:/Users/mbobo/Documents/GitHub/Project3WebProject/uploads/";
        // File uploadDir = new File(absoluteUploadPath);
        
        // Use a relative path (from the project root) for portability
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("📁 Created uploads directory at: " + uploadPath);
        }

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("📁 Serving images from: " + uploadPath);
        System.out.println("Does folder exist? " + uploadDir.exists());
        System.out.println("═══════════════════════════════════════════════════════\n");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600);
    }
}