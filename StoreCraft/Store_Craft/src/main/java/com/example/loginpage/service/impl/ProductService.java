package com.example.loginpage.service.impl;

import com.example.loginpage.model.Product;
import com.example.loginpage.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final IProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProductService(IProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product uploadProduct(MultipartFile file, String name, String description,
                                 BigDecimal price, Integer stockQuantity, String category) {
        try {
            String imageUrl = fileStorageService.saveFile(file);
            Product product = new Product(name, description, price, stockQuantity, imageUrl, category);
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload product: " + e.getMessage());
        }
    }

    @Transactional
    public Product updateProduct(String productId, Product product) {
        Product existing = productRepository.findById(productId).orElse(null);
        if (existing == null) {
            throw new RuntimeException("Product not found: " + productId);
        }

        if (product.getName() != null) existing.setName(product.getName());
        if (product.getDescription() != null) existing.setDescription(product.getDescription());
        if (product.getPrice() != null) existing.setPrice(product.getPrice());
        if (product.getStockQuantity() != null) existing.setStockQuantity(product.getStockQuantity());
        if (product.getImageUrl() != null) existing.setImageUrl(product.getImageUrl());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());

        return productRepository.save(existing);
    }

    @Transactional
    public boolean deleteProduct(String productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean hasStock(String productId, Integer quantity) {
        Product product = getProductById(productId);
        return product != null && product.getStockQuantity() >= quantity;
    }

    @Transactional(readOnly = true)
    public Product getProductEntity(String productId) {
        return productRepository.findById(productId).orElse(null);
    }
}