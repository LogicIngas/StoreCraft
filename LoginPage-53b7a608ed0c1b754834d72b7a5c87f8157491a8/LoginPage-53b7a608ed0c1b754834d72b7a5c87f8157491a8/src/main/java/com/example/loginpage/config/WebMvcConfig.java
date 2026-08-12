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
        // YOUR EXACT UPLOADS FOLDER PATH later we can change it to a relative path or use a property file to make it more flexible
        String absoluteUploadPath = "C:/Users/mbobo/Documents/GitHub/Project3WebProject/uploads/";

        File uploadDir = new File(absoluteUploadPath);

        // Print to console to confirm it finds the folder
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("📁 IMAGE SERVING DIRECTORY CHECK");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Looking for images at: " + absoluteUploadPath);
        System.out.println("Does this folder exist on disk? " + uploadDir.exists());
        System.out.println("═══════════════════════════════════════════════════════\n");

        // This maps your physical C:/drive folder to the URL
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absoluteUploadPath)
                .setCachePeriod(3600);
    }
}