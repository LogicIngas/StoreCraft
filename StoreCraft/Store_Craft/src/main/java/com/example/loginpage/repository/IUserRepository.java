package com.example.loginpage.repository;

import com.example.loginpage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    /**
     * Find a user by their email address
     * Used during login to verify credentials
     *
     * @param email the user's email
     * @return User if found, null otherwise
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    /**
     * Find a user by their password-reset token.
     */
    @Query("SELECT u FROM User u WHERE u.resetToken = :token")
    User findByResetToken(@Param("token") String token);
}