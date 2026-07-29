package com.example.loginpage.controller;

import com.example.loginpage.model.Product;
import com.example.loginpage.service.impl.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return service.getProductById(id);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String name) {
        return service.searchProducts(name);
    }

    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return service.getProductsByCategory(category);
    }

    @GetMapping("/available")
    public List<Product> getAvailable() {
        return service.getAvailableProducts();
    }

    @GetMapping("/price-range")
    public List<Product> getByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return service.getProductsByPriceRange(min, max);
    }

    @PostMapping("/upload")
    public Product uploadProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("category") String category) {

        return service.uploadProduct(file, name, description, price, stockQuantity, category);
    }

    @PostMapping("/create")
    public Product create(@RequestBody Product product) {
        return service.createProduct(product);
    }

    @PutMapping("/update/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        return service.updateProduct(id, product);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable String id) {
        return service.deleteProduct(id);
    }
}