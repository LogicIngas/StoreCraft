// package com.example.loginpage.config;

// import com.example.loginpage.model.Product;
// import com.example.loginpage.model.Role;
// import com.example.loginpage.model.User;
// import com.example.loginpage.repository.IProductRepository;
// import com.example.loginpage.repository.IRoleRepository;
// import com.example.loginpage.repository.IUserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;

// @Component
// public class DataInitializer implements CommandLineRunner {

//     private final IProductRepository productRepository;
//     private final IRoleRepository roleRepository;
//     private final IUserRepository userRepository;

//     @Autowired
//     public DataInitializer(IProductRepository productRepository, IRoleRepository roleRepository, IUserRepository userRepository) {
//         this.productRepository = productRepository;
//         this.roleRepository = roleRepository;
//         this.userRepository = userRepository;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) throws Exception {
//         // Initialize Roles
//         initializeRoles();

//         // Initialize Admin User
//         initializeAdminUser();

//         // Only seed data if database is empty to prevent duplicates
//         if (productRepository.count() == 0) {
//             System.out.println("\n═══════════════════════════════════════════════════════");
//             System.out.println("🌱 SEEDING DATABASE WITH PRODUCTS");
//             System.out.println("═══════════════════════════════════════════════════════\n");

//             // ============================================================
//             // CLOTHING CATEGORY - Hoodies
//             // ============================================================

//             productRepository.save(new Product(
//                     "Classic Black Hoodie",
//                     "Warm and comfortable cotton blend hoodie. Perfect for casual wear and outdoor activities.",
//                     new BigDecimal("449.99"), 20, "/images/n6.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Premium Cotton Hoodie - Navy",
//                     "Premium quality cotton hoodie with reinforced seams and adjustable drawstrings.",
//                     new BigDecimal("499.99"), 15, "/images/n7.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Cozy Grey Hoodie",
//                     "Ultra-soft grey hoodie made from premium blend fabric. Ideal for all seasons.",
//                     new BigDecimal("459.99"), 18, "/images/n8.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Charcoal Hoodie",
//                     "Sleek charcoal hoodie with kangaroo pocket and modern fit.",
//                     new BigDecimal("475.99"), 22, "/images/n9.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Cream Comfort Hoodie",
//                     "Lightweight cream-colored hoodie perfect for layering.",
//                     new BigDecimal("449.99"), 25, "/images/n10.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Dark Blue Hoodie",
//                     "Deep navy blue hoodie with embroidered logo. Premium comfort fit.",
//                     new BigDecimal("489.99"), 16, "/images/n11.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Olive Green Hoodie",
//                     "Trendy olive green hoodie with contemporary design elements.",
//                     new BigDecimal("479.99"), 14, "/images/n12.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Burgundy Hoodie",
//                     "Rich burgundy hoodie with premium cotton construction.",
//                     new BigDecimal("469.99"), 12, "/images/n13.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Slate Grey Hoodie",
//                     "Modern slate grey hoodie with minimalist aesthetic.",
//                     new BigDecimal("459.99"), 19, "/images/n14.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Forest Green Hoodie",
//                     "Deep forest green hoodie perfect for outdoor enthusiasts.",
//                     new BigDecimal("469.99"), 17, "/images/n15.jpg", "Clothing"));

//             // ============================================================
//             // CLOTHING CATEGORY - T-Shirts & Tops
//             // ============================================================

//             productRepository.save(new Product(
//                     "Graphic Premium T-Shirt",
//                     "100% premium cotton street-wear t-shirt with bold graphic design.",
//                     new BigDecimal("249.99"), 50, "/images/n16.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Casual Button-Up Shirt",
//                     "Versatile denim button-up shirt perfect for casual and semi-formal occasions.",
//                     new BigDecimal("349.99"), 30, "/images/n17.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Premium Polo Neck Sweater",
//                     "Classic black polo neck sweater with cable knit pattern. Timeless elegance.",
//                     new BigDecimal("399.99"), 24, "/images/n18.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Cozy Crew Neck Sweater",
//                     "Warm and soft sweater with ribbed cuffs. Perfect for chilly weather.",
//                     new BigDecimal("429.99"), 28, "/images/n19.jpg", "Clothing"));

//             // ============================================================
//             // CLOTHING CATEGORY - Jeans & Bottoms
//             // ============================================================

//             productRepository.save(new Product(
//                     "Slim Fit Ripped Jeans",
//                     "Durable denim jeans with distressed design and comfortable stretch.",
//                     new BigDecimal("599.99"), 15, "/images/n20.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Classic Denim Jeans",
//                     "Timeless dark wash denim jeans with perfect fit and durability.",
//                     new BigDecimal("579.99"), 20, "/images/n21.jpg", "Clothing"));

//             // ============================================================
//             // CLOTHING CATEGORY - Jackets & Outerwear
//             // ============================================================

//             productRepository.save(new Product(
//                     "Winter Puffer Jacket",
//                     "Windproof and water-resistant puffer jacket for extreme weather protection.",
//                     new BigDecimal("899.99"), 10, "/images/n22.jpg", "Clothing"));

//             productRepository.save(new Product(
//                     "Classic Denim Jacket",
//                     "Timeless denim jacket perfect for layering. Versatile and stylish.",
//                     new BigDecimal("749.99"), 18, "/images/n23.jpg", "Clothing"));

//             // ============================================================
//             // CLOTHING CATEGORY - Tracksuits & Sets
//             // ============================================================

//             productRepository.save(new Product(
//                     "Adidas Tracksuit Set",
//                     "Authentic Adidas tracksuit with comfort fit and premium materials.",
//                     new BigDecimal("1299.99"), 12, "/images/n24.jpg", "Clothing"));

//             // ============================================================
//             // ELECTRONICS CATEGORY
//             // ============================================================

//             productRepository.save(new Product(
//                     "Canon EOS 60D DSLR Camera",
//                     "Professional-grade 18MP DSLR camera with powerful autofocus and 5fps continuous shooting.",
//                     new BigDecimal("8999.99"), 5, "/images/n25.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "Samsung Galaxy Flagship Smartphone",
//                     "Latest flagship smartphone with stunning AMOLED display and advanced camera system.",
//                     new BigDecimal("12999.99"), 8, "/images/n26.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "Apple iPad Pro",
//                     "High-performance tablet with M1 chip, stunning Liquid Retina display, and all-day battery.",
//                     new BigDecimal("7499.99"), 6, "/images/n27.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "Dell XPS 13 Laptop",
//                     "Ultra-slim laptop with Intel Core i7, 16GB RAM, and blazing-fast SSD storage.",
//                     new BigDecimal("15999.99"), 4, "/images/n28.jpg", "Electronics"));

//             // ============================================================
//             // ACCESSORIES CATEGORY
//             // ============================================================

//             productRepository.save(new Product(
//                     "JBL Premium Wireless Headphones",
//                     "High-fidelity wireless headphones with active noise cancellation and 40-hour battery life.",
//                     new BigDecimal("3499.99"), 20, "/images/n29.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Premium Leather Belt",
//                     "Genuine leather belt with classic buckle design. Perfect complement to any outfit.",
//                     new BigDecimal("199.99"), 30, "/images/n30.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Designer Sunglasses",
//                     "UV protection sunglasses with stylish frame and polarized lenses.",
//                     new BigDecimal("499.99"), 25, "/images/n40.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Smart Watch Pro",
//                     "Advanced fitness tracking with heart rate monitor and 7-day battery life.",
//                     new BigDecimal("2499.99"), 12, "/images/n41.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "Wireless Bluetooth Speaker",
//                     "Portable speaker with 360-degree sound and waterproof design.",
//                     new BigDecimal("1299.99"), 18, "/images/n42.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "High-Speed USB-C Cable",
//                     "Fast charging cable compatible with all major devices.",
//                     new BigDecimal("49.99"), 100, "/images/n43.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "Stainless Steel Water Bottle",
//                     "Keeps drinks cold for 24 hours or hot for 12 hours. Eco-friendly design.",
//                     new BigDecimal("79.99"), 60, "/images/n44.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Yoga Mat Premium",
//                     "Non-slip yoga mat made from eco-friendly materials. Perfect for fitness.",
//                     new BigDecimal("149.99"), 40, "/images/n45.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Portable Phone Stand",
//                     "Adjustable phone stand for desk or travel. Aluminum construction.",
//                     new BigDecimal("39.99"), 80, "/images/n46.jpg", "Accessories"));

//             productRepository.save(new Product(
//                     "Mechanical Gaming Keyboard",
//                     "RGB backlit keyboard with mechanical switches for gaming.",
//                     new BigDecimal("899.99"), 15, "/images/n47.jpg", "Electronics"));

//             productRepository.save(new Product(
//                     "4K Webcam",
//                     "Ultra HD webcam perfect for streaming and video conferencing.",
//                     new BigDecimal("599.99"), 20, "/images/n48.jpg", "Electronics"));

//             System.out.println("\n═══════════════════════════════════════════════════════");
//             System.out.println("✅ MARKETPLACE SEEDED SUCCESSFULLY!");
//             System.out.println("═══════════════════════════════════════════════════════");
//             System.out.println("📊 Total Products Created: 30");
//             System.out.println("   - Clothing: 14 products");
//             System.out.println("   - Electronics: 8 products");
//             System.out.println("   - Accessories: 8 products");
//             System.out.println("═══════════════════════════════════════════════════════\n");
//         }
//     }

//     private void initializeRoles() {
//         if (roleRepository.count() == 0) {
//             roleRepository.save(new Role("BUYER", "Regular buyer/customer role"));
//             roleRepository.save(new Role("SELLER", "Seller who can upload products"));
//             roleRepository.save(new Role("ADMIN", "Administrator role"));
//             System.out.println("✅ Roles initialized: BUYER, SELLER, ADMIN");
//         }
//     }

//     private void initializeAdminUser() {
//         if (userRepository.count() == 0) {
//             Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
//             if (adminRole != null) {
//                 User admin = new User.Builder()
//                         .setEmail("admin@africonnect.com")
//                         .setPassword("admin123")
//                         .setFirstName("Admin")
//                         .setLastName("User")
//                         .setRole(adminRole)
//                         .build();
//                 userRepository.save(admin);
//                 System.out.println("✅ Admin user created: admin@africonnect.com / admin123");
//             }
//         }
//     }
// }