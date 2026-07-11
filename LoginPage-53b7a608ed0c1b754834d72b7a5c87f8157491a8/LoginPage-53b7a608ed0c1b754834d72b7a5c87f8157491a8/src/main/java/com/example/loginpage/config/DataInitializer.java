//package com.example.loginpage.config;
//
//import com.example.loginpage.model.Product;
//import com.example.loginpage.repository.IProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final IProductRepository productRepository;
//
//    @Autowired
//    public DataInitializer(IProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Only seed data if database is empty to prevent duplicates
//        if (productRepository.count() == 0) {
//
//            // ============================================================
//            // CLOTHING CATEGORY
//            // ============================================================
//
//            // Hoodies Collection
//            productRepository.save(new Product(
//                    "Classic Black Hoodie",
//                    "Warm and comfortable cotton blend hoodie. Perfect for casual wear and outdoor activities.",
//                    new BigDecimal("449.99"), 20, "/images/hoodie.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Premium Cotton Hoodie - Navy",
//                    "Premium quality cotton hoodie with reinforced seams and adjustable drawstrings.",
//                    new BigDecimal("499.99"), 15, "/images/hoodie1.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Cozy Grey Hoodie",
//                    "Ultra-soft grey hoodie made from premium blend fabric. Ideal for all seasons.",
//                    new BigDecimal("459.99"), 18, "/images/hoodie2.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Charcoal Hoodie",
//                    "Sleek charcoal hoodie with kangaroo pocket and modern fit.",
//                    new BigDecimal("475.99"), 22, "/images/hoodie3.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Cream Comfort Hoodie",
//                    "Lightweight cream-colored hoodie perfect for layering.",
//                    new BigDecimal("449.99"), 25, "/images/hoodie5.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Dark Blue Hoodie",
//                    "Deep navy blue hoodie with embroidered logo. Premium comfort fit.",
//                    new BigDecimal("489.99"), 16, "/images/hoodie6.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Olive Green Hoodie",
//                    "Trendy olive green hoodie with contemporary design elements.",
//                    new BigDecimal("479.99"), 14, "/images/hoodie7.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Burgundy Hoodie",
//                    "Rich burgundy hoodie with premium cotton construction.",
//                    new BigDecimal("469.99"), 12, "/images/hoodie8.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Slate Grey Hoodie",
//                    "Modern slate grey hoodie with minimalist aesthetic.",
//                    new BigDecimal("459.99"), 19, "/images/hoodie9.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Forest Green Hoodie",
//                    "Deep forest green hoodie perfect for outdoor enthusiasts.",
//                    new BigDecimal("469.99"), 17, "/images/hoodie10.jpg", "Clothing"));
//
//            // T-Shirts & Tops
//            productRepository.save(new Product(
//                    "Graphic Premium T-Shirt",
//                    "100% premium cotton street-wear t-shirt with bold graphic design.",
//                    new BigDecimal("249.99"), 50, "/images/tshirt.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Casual Button-Up Shirt",
//                    "Versatile denim button-up shirt perfect for casual and semi-formal occasions.",
//                    new BigDecimal("349.99"), 30, "/images/shirt.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Premium Polo Neck Sweater",
//                    "Classic black polo neck sweater with cable knit pattern. Timeless elegance.",
//                    new BigDecimal("399.99"), 24, "/images/poloneck.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Cozy Crew Neck Sweater",
//                    "Warm and soft sweater with ribbed cuffs. Perfect for chilly weather.",
//                    new BigDecimal("429.99"), 28, "/images/sweater.jpg", "Clothing"));
//
//            // Jeans & Bottoms
//            productRepository.save(new Product(
//                    "Slim Fit Ripped Jeans",
//                    "Durable denim jeans with distressed design and comfortable stretch.",
//                    new BigDecimal("599.99"), 15, "/images/jeans.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Classic Denim Jeans",
//                    "Timeless dark wash denim jeans with perfect fit and durability.",
//                    new BigDecimal("579.99"), 20, "/images/denimjean.jpg", "Clothing"));
//
//            // Jackets & Outerwear
//            productRepository.save(new Product(
//                    "Winter Puffer Jacket",
//                    "Windproof and water-resistant puffer jacket for extreme weather protection.",
//                    new BigDecimal("899.99"), 10, "/images/jacket.jpg", "Clothing"));
//
//            productRepository.save(new Product(
//                    "Classic Denim Jacket",
//                    "Timeless denim jacket perfect for layering. Versatile and stylish.",
//                    new BigDecimal("749.99"), 18, "/images/denimjacket.jpg", "Clothing"));
//
//            // Tracksuits & Sets
//            productRepository.save(new Product(
//                    "Adidas Tracksuit Set",
//                    "Authentic Adidas tracksuit with comfort fit and premium materials.",
//                    new BigDecimal("1299.99"), 12, "/images/adidastracksuit.jpg", "Clothing"));
//
//            // ============================================================
//            // ELECTRONICS CATEGORY
//            // ============================================================
//
//            productRepository.save(new Product(
//                    "Canon EOS 60D DSLR Camera",
//                    "Professional-grade 18MP DSLR camera with powerful autofocus and 5fps continuous shooting.",
//                    new BigDecimal("8999.99"), 5, "/images/camera.jpg", "Electronics"));
//
//            productRepository.save(new Product(
//                    "Samsung Galaxy Flagship Smartphone",
//                    "Latest flagship smartphone with stunning AMOLED display and advanced camera system.",
//                    new BigDecimal("12999.99"), 8, "/images/galaxy.jpg", "Electronics"));
//
//            productRepository.save(new Product(
//                    "Apple iPad Pro",
//                    "High-performance tablet with M1 chip, stunning Liquid Retina display, and all-day battery.",
//                    new BigDecimal("7499.99"), 6, "/images/ipad.jpg", "Electronics"));
//
//            productRepository.save(new Product(
//                    "Dell XPS 13 Laptop",
//                    "Ultra-slim laptop with Intel Core i7, 16GB RAM, and blazing-fast SSD storage.",
//                    new BigDecimal("15999.99"), 4, "/images/laptop.jpg", "Electronics"));
//
//            // ============================================================
//            // ACCESSORIES CATEGORY
//            // ============================================================
//
//            productRepository.save(new Product(
//                    "JBL Premium Wireless Headphones",
//                    "High-fidelity wireless headphones with active noise cancellation and 40-hour battery life.",
//                    new BigDecimal("3499.99"), 20, "/images/headset.jpg", "Accessories"));
//
//            System.out.println("✅ Marketplace initialized successfully with 24 products across 3 categories!");
//            System.out.println("   - Clothing: 14 products");
//            System.out.println("   - Electronics: 4 products");
//            System.out.println("   - Accessories: 1 product");
//        }
//    }
//}