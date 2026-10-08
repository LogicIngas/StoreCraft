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
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(IProductRepository productRepository, IRoleRepository roleRepository,
            IUserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            User admin = userRepository.findByEmail("admin@storecraft.com");
            if (admin == null) {
                admin = new User.Builder()
                        .setEmail("admin@storecraft.com")
                        .setPassword(passwordEncoder.encode("admin123"))
                        .setFirstName("Admin")
                        .setLastName("User")
                        .setRole(adminRole)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin user created: admin@storecraft.com / admin123");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        } else {
            System.out.println("❌ ADMIN role not found – admin user not created!");
        }

        // 3. Seed 3 products if the table is empty to make the store look populated and test images
        if (productRepository.count() == 0) {
            productRepository.save(new Product(
                    "Premium Cotton T-Shirt",
                    "High-quality, breathable 100% cotton t-shirt. Perfect for everyday wear.",
                    new BigDecimal("299.99"), 50, 
                    "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&auto=format&fit=crop&q=80", 
                    "Clothing"));

            productRepository.save(new Product(
                    "Classic Denim Jacket",
                    "A timeless piece for any wardrobe. Features durable denim and a comfortable fit.",
                    new BigDecimal("899.99"), 15, 
                    "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=600&auto=format&fit=crop&q=80", 
                    "Clothing"));

            productRepository.save(new Product(
                    "Leather Crossbody Bag",
                    "Handcrafted genuine leather bag with adjustable strap and multiple compartments.",
                    new BigDecimal("1299.99"), 10, 
                    "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=600&auto=format&fit=crop&q=80", 
                    "Accessories"));
        }

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("✅ DATA INITIALIZATION COMPLETE");
        System.out.println("═══════════════════════════════════════════════════════\n");
    }
}