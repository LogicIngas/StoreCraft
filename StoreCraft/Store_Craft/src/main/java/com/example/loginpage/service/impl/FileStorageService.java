package com.example.loginpage.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName != null ? cloudName.trim() : "",
                "api_key",    apiKey != null ? apiKey.trim() : "",
                "api_secret", apiSecret != null ? apiSecret.trim() : "",
                "secure",     true
        ));
    }

    /**
     * Upload an image to Cloudinary and return its permanent CDN URL.
     *
     * @param file the multipart image file to upload
     * @return the full HTTPS URL of the uploaded image on Cloudinary
     * @throws IOException if the upload fails
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

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",           "storecraft/products",
                        "resource_type",    "image",
                        "use_filename",     false,
                        "unique_filename",  true
                )
        );

        // "secure_url" is the permanent HTTPS CDN link
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete an image from Cloudinary by its URL.
     * Extracts the public_id from the URL so Cloudinary can locate the asset.
     *
     * @param imageUrl the full Cloudinary CDN URL stored in the database
     * @return true if deletion succeeded, false otherwise
     */
    public boolean deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            System.err.println("Failed to delete Cloudinary image: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check whether a Cloudinary image URL is reachable (non-null/empty).
     * Cloudinary URLs are always persistent, so we just validate the URL exists.
     */
    public boolean fileExists(String imageUrl) {
        return imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("https://");
    }

    /**
     * Extract the Cloudinary public_id from a secure URL.
     * Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/storecraft/products/abc123.jpg
     * Extracted public_id: storecraft/products/abc123
     */
    private String extractPublicId(String imageUrl) {
        // Remove everything up to and including "/upload/"
        int uploadIndex = imageUrl.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new IllegalArgumentException("Not a valid Cloudinary URL: " + imageUrl);
        }
        String afterUpload = imageUrl.substring(uploadIndex + 8); // skip "/upload/"

        // Remove version segment if present (e.g. "v1234567890/")
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            int slashIndex = afterUpload.indexOf("/");
            String potentialVersion = afterUpload.substring(1, slashIndex);
            if (potentialVersion.matches("\\d+")) {
                afterUpload = afterUpload.substring(slashIndex + 1);
            }
        }

        // Remove file extension
        int dotIndex = afterUpload.lastIndexOf(".");
        if (dotIndex != -1) {
            afterUpload = afterUpload.substring(0, dotIndex);
        }

        return afterUpload;
    }
}