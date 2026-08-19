package com.example.loginpage.repository;

import com.example.loginpage.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(@Param("name") String name);

    @Query("SELECT r FROM Role r WHERE r.name = 'BUYER'")
    Optional<Role> findBuyerRole();

    @Query("SELECT r FROM Role r WHERE r.name = 'SELLER'")
    Optional<Role> findSellerRole();
}