package com.example.loginpage.repository;

import com.example.loginpage.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, String> {

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL ORDER BY c.displayOrder")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.categoryId = :parentId ORDER BY c.displayOrder")
    List<Category> findSubCategories(@Param("parentId") String parentId);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByName(@Param("name") String name);
}