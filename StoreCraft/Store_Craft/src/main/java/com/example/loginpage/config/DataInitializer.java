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
    public DataInitializer(IProductRepository productRepository, IRoleRepository roleRepository,
            IUserRepository userRepository) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("🌱 DATA INITIALIZER STARTED");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // 1. Initialize Roles (if missing)
        Role buyerRole = roleRepository.findByName("BUYER").orElse(null);
        if (buyerRole == null) {
            buyerRole = new Role("BUYER", "Regular buyer/customer role");
            roleRepository.save(buyerRole);
            System.out.println("✅ Created BUYER role");
        }

        Role sellerRole = roleRepository.findByName("SELLER").orElse(null);
        if (sellerRole == null) {
            sellerRole = new Role("SELLER", "Seller who can upload products");
            roleRepository.save(sellerRole);
            System.out.println("✅ Created SELLER role");
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = new Role("ADMIN", "Administrator role");
            roleRepository.save(adminRole);
            System.out.println("✅ Created ADMIN role");
        }

        // 2. Initialize Admin User (if missing)
        if (adminRole != null) {
            User admin = userRepository.findByEmail("admin@africonnect.com");
            if (admin == null) {
                admin = new User.Builder()
                        .setEmail("admin@africonnect.com")
                        .setPassword("admin123")
                        .setFirstName("Admin")
                        .setLastName("User")
                        .setRole(adminRole)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin user created: admin@africonnect.com / admin123");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        } else {
            System.out.println("❌ ADMIN role not found – admin user not created!");
        }

        // 3. Seed only 3 products if the table is empty
        if (productRepository.count() == 0) {
            // productRepository.save(new Product(
            // "Classic Black Hoodie",
            // "Warm and comfortable cotton blend hoodie.",
            // new BigDecimal("449.99"), 20, "/images/n6.jpg", "Clothing"));
            productRepository.save(new Product(
                    "Slim Fit Ripped Jeans",
                    "Durable denim jeans with distressed design.",
                    new BigDecimal("599.99"), 15, "/images/n20.jpg", "Clothing"));
            // productRepository.save(new Product(
            // "Winter Puffer Jacket",
            // "Windproof and water-resistant puffer jacket.",
            // new BigDecimal("899.99"), 10, "/images/n22.jpg", "Clothing"));
            // System.out.println("✅ Seeded 3 sample products");
        }

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("✅ DATA INITIALIZATION COMPLETE");
        System.out.println("═══════════════════════════════════════════════════════\n");
    }
}