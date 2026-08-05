package com.example.loginpage.controller;

import com.example.loginpage.dto.ProductDTO;
import com.example.loginpage.service.impl.FileStorageService;
import com.example.loginpage.service.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Get all products
     * GET /product/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get product by ID
     * GET /product/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        ProductDTO product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Search products by name
     * GET /product/search?name=xyz
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Search name is required");
        }
        List<ProductDTO> products = productService.searchProducts(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category
     * GET /product/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Get available products (with stock)
     * GET /product/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableProducts() {
        List<ProductDTO> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by price range
     * GET /product/price-range?min=10&max=100
     */
    @GetMapping("/price-range")
    public ResponseEntity<?> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        if (min == null || max == null || min.compareTo(max) > 0) {
            return ResponseEntity.badRequest().body("Invalid price range");
        }
        List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    /**
     * Upload product with image file
     * POST /product/upload (multipart/form-data)
     *
     * Form fields:
     * - file: MultipartFile (required)
     * - name: String (required)
     * - description: String (optional)
     * - price: BigDecimal (required)
     * - stockQuantity: Integer (required)
     * - category: String (required)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("category") String category) {
        try {
            // Validate inputs
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("File is required"));
            }
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Product name is required"));
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Product price must be greater than 0"));
            }
            if (stockQuantity == null || stockQuantity < 0) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Stock quantity must be 0 or greater"));
            }
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Category is required"));
            }

            // Save file
            String imageUrl = fileStorageService.saveFile(file);

            // Create product in database
            ProductDTO product = productService.createProduct(
                    name,
                    description != null ? description : "",
                    price,
                    stockQuantity,
                    imageUrl,
                    category
            );

            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("File upload failed: " + e.getMessage()));
        }
    }

    /**
     * Create new product with image URL (legacy endpoint)
     * POST /product/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name is required");
        }
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Product price must be greater than 0");
        }

        ProductDTO product = productService.createProduct(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity() != null ? request.stockQuantity() : 0,
                request.imageUrl(),
                request.category()
        );
        return ResponseEntity.ok(product);
    }

    /**
     * Update product
     * PUT /product/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequest request) {
        ProductDTO product = productService.updateProduct(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.imageUrl(),
                request.category()
        );
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Delete product
     * DELETE /product/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Product deleted successfully");
    }

    // ============================================================
    // DTOs
    // ============================================================

    public record CreateProductRequest(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            String imageUrl,
            String category
    ) {}

    public record UpdateProductRequest(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            String imageUrl,
            String category
    ) {}

    public record ErrorResponse(String message) {}
}