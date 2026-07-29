package com.example.loginpage.config;

import com.example.loginpage.model.Product;
import com.example.loginpage.model.Role;
import com.example.loginpage.model.User;
import com.example.loginpage.repository.IProductRepository;
import com.example.loginpage.repository.IRoleRepository;
import com.example.loginpage.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IProductRepository productRepository;
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;

    @Autowired
    public DataInitializer(IProductRepository productRepository, IRoleRepository roleRepository, IUserRepository userRepository) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize Roles
        initializeRoles();

        // Initialize Admin User
        initializeAdminUser();

        // Only seed data if database is empty to prevent duplicates
        if (productRepository.count() == 0) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("🌱 SEEDING DATABASE WITH PRODUCTS");
            System.out.println("═══════════════════════════════════════════════════════\n");

            // ============================================================
            // CLOTHING CATEGORY - Hoodies
            // ============================================================

            productRepository.save(new Product(
                    "Classic Black Hoodie",
                    "Warm and comfortable cotton blend hoodie. Perfect for casual wear and outdoor activities.",
                    new BigDecimal("449.99"), 20, "/images/n6.jpg", "Clothing"));

            productRepository.save(new Product(
                    "Premium Cotton Hoodie - Navy",
                    "Premium quality cotton hoodie with reinforced seams and adjustable drawstrings.",
                    new BigDecimal("499.99"), 15, "/images/n7.jpg", "Clothing"));
        
            // ============================================================
            // CLOTHING CATEGORY - Jeans & Bottoms
            // ============================================================

            productRepository.save(new Product(
                    "Slim Fit Ripped Jeans",
                    "Durable denim jeans with distressed design and comfortable stretch.",
                    new BigDecimal("599.99"), 15, "/images/n20.jpg", "Clothing"));

            // ============================================================
            // CLOTHING CATEGORY - Jackets & Outerwear
            // ============================================================

            productRepository.save(new Product(
                    "Winter Puffer Jacket",
                    "Windproof and water-resistant puffer jacket for extreme weather protection.",
                    new BigDecimal("899.99"), 10, "/images/n22.jpg", "Clothing"));

            productRepository.save(new Product(
                    "Classic Denim Jacket",
                    "Timeless denim jacket perfect for layering. Versatile and stylish.",
                    new BigDecimal("749.99"), 18, "/images/n23.jpg", "Clothing"));

            // ============================================================
            // CLOTHING CATEGORY - Tracksuits & Sets
            // ============================================================

            productRepository.save(new Product(
                    "Adidas Tracksuit Set",
                    "Authentic Adidas tracksuit with comfort fit and premium materials.",
                    new BigDecimal("1299.99"), 12, "/images/n24.jpg", "Clothing"));

            // ============================================================
            // ELECTRONICS CATEGORY
            // ============================================================
            productRepository.save(new Product(
                    "Dell XPS 13 Laptop",
                    "Ultra-slim laptop with Intel Core i7, 16GB RAM, and blazing-fast SSD storage.",
                    new BigDecimal("15999.99"), 4, "/images/n28.jpg", "Electronics"));

            // ============================================================
            // ACCESSORIES CATEGORY
            // ============================================================

            productRepository.save(new Product(
                    "Yoga Mat Premium",
                    "Non-slip yoga mat made from eco-friendly materials. Perfect for fitness.",
                    new BigDecimal("149.99"), 40, "/images/n45.jpg", "Accessories"));


            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("✅ MARKETPLACE SEEDED SUCCESSFULLY!");
            System.out.println("═══════════════════════════════════════════════════════");

        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("BUYER", "Regular buyer/customer role"));
            roleRepository.save(new Role("SELLER", "Seller who can upload products"));
            roleRepository.save(new Role("ADMIN", "Administrator role"));
            System.out.println("✅ Roles initialized: BUYER, SELLER, ADMIN");
        }
    }

    private void initializeAdminUser() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            if (adminRole != null) {
                User admin = new User.Builder()
                        .setEmail("admin@africonnect.com")
                        .setPassword("admin123")
                        .setFirstName("Admin")
                        .setLastName("User")
                        .setRole(adminRole)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin user created: admin@africonnect.com / admin123");
            }
        }
    }
}