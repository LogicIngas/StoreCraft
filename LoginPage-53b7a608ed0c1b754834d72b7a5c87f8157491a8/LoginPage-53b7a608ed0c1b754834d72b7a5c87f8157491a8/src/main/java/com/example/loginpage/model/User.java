package com.example.loginpage.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "users")
public class User {

    @Id
    @UuidGenerator // Explicitly tells Hibernate 7 to generate a UUID string for fresh inserts
    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    // MANDATORY: Hibernate requires a completely open public/protected no-arg constructor
    public User() {}

    private User(Builder builder) {
        this.userId = builder.userId; // Will be null for new signups
        this.email = builder.email;
        this.password = builder.password;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public static class Builder {
        private String userId;
        private String email;
        private String password;
        private String firstName;
        private String lastName;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
