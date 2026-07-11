package com.example.loginpage.repository;

import com.example.loginpage.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAddressRepository extends JpaRepository<Address, String> {

    /**
     * Find all addresses for a user
     */
    @Query("SELECT a FROM Address a WHERE a.userId = :userId ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findByUserId(@Param("userId") String userId);

    /**
     * Find default address for a user
     */
    @Query("SELECT a FROM Address a WHERE a.userId = :userId AND a.isDefault = true")
    Optional<Address> findDefaultAddressByUserId(@Param("userId") String userId);

    /**
     * Find addresses by type (SHIPPING, BILLING, etc.)
     */
    @Query("SELECT a FROM Address a WHERE a.userId = :userId AND a.type = :type")
    List<Address> findByUserIdAndType(@Param("userId") String userId, @Param("type") String type);

    /**
     * Check if user has any addresses
     */
    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);

    /**
     * Count addresses for a user
     */
    @Query("SELECT COUNT(a) FROM Address a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") String userId);
}