package com.example.loginpage.repository;

import com.example.loginpage.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {

    boolean existsByName(String name);

    /**
     * Find all products in a specific category
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);

    /**
     * Search products by name (case-insensitive partial match)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContaining(@Param("name") String name);

    /**
     * Find products with available stock
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findAvailableProducts();

    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                   @Param("maxPrice") java.math.BigDecimal maxPrice);
}