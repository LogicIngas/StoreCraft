package com.example.loginpage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
        try {
            // ✅ Get absolute path to uploads directory
            String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();

            // ✅ Create proper file:// URL
            String fileUri;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows: file:///C:/path/to/uploads
                fileUri = "file:///" + absolutePath.replace("\\", "/");
            } else {
                // Linux/Mac: file:///path/to/uploads
                fileUri = "file://" + absolutePath;
            }

            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("📁 IMAGE SERVING CONFIGURATION");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("📂 Upload directory (from config): " + uploadDir);
            System.out.println("📂 Absolute path: " + absolutePath);
            System.out.println("📂 File URI: " + fileUri);
            System.out.println("🌐 Web endpoint: http://localhost:8080/images/**");
            System.out.println("🌐 Frontend CORS: " + frontendUrl);
            System.out.println("═══════════════════════════════════════════════════════\n");

            registry.addResourceHandler("/images/**")
                    .addResourceLocations(fileUri)
                    .setCachePeriod(0)  // No caching for development
                    .resourceChain(true);

            System.out.println("✅ Image handler registered successfully!\n");

        } catch (Exception e) {
            System.err.println("❌ ERROR configuring image handler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}