package com.example.loginpage.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Save uploaded file and return the saved filename
     *
     * @param file the multipart file to upload
     * @return the relative path to the uploaded file (e.g., /images/uuid-filename.jpg)
     * @throws IOException if file storage fails
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Get original filename and extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        // Generate unique filename to avoid conflicts
        String savedFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        Path filePath = uploadPath.resolve(savedFilename);
        Files.copy(file.getInputStream(), filePath);

        // Return the URL path (relative to the web root)
        return "/images/" + savedFilename;
    }

    /**
     * Delete a file by its path
     *
     * @param filePath the relative path to the file (e.g., /images/filename.jpg)
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            // Extract filename from path and construct full path
            String filename = filePath.replace("/images/", "");
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().resolve(filename);

            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filePath) {
        try {
            String filename = filePath.replace("/images/", "");
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().resolve(filename);
            return Files.exists(fullPath);
        } catch (Exception e) {
            return false;
        }
    }
}