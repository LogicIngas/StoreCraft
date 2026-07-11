package com.example.loginpage.service.impl;

import com.example.loginpage.dto.ProductDTO;
import com.example.loginpage.model.Product;
import com.example.loginpage.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final IProductRepository productRepository;

    @Autowired
    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(String productId) {
        return productRepository.findById(productId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContaining(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available products (with stock > 0)
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAvailableProducts() {
        return productRepository.findAvailableProducts()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get products by price range
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new product (admin only)
     */
    @Transactional
    public ProductDTO createProduct(String name, String description, BigDecimal price,
                                    Integer stockQuantity, String imageUrl, String category) {
        Product product = new Product(name, description, price, stockQuantity, imageUrl, category);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    /**
     * Update product
     */
    @Transactional
    public ProductDTO updateProduct(String productId, String name, String description,
                                    BigDecimal price, Integer stockQuantity, String imageUrl, String category) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (name != null) product.setName(name);
                    if (description != null) product.setDescription(description);
                    if (price != null) product.setPrice(price);
                    if (stockQuantity != null) product.setStockQuantity(stockQuantity);
                    if (imageUrl != null) product.setImageUrl(imageUrl);
                    if (category != null) product.setCategory(category);
                    Product updated = productRepository.save(product);
                    return convertToDTO(updated);
                })
                .orElse(null);
    }

    /**
     * Delete product
     */
    @Transactional
    public boolean deleteProduct(String productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    /**
     * Check if product has sufficient stock
     */
    @Transactional(readOnly = true)
    public boolean hasStock(String productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= quantity)
                .orElse(false);
    }

    /**
     * Get actual product entity (for internal use)
     */
    public Product getProductEntity(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * Convert Product entity to DTO
     */
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}