package com.example.loginpage.factory;

import com.example.loginpage.model.Role;
import com.example.loginpage.model.User;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserFactoryTest {

    @Test
    public void testCreateUser_Success() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Role role = RoleFactory.createRole("BUYER", "Buyer Role");
        User user = UserFactory.createUser("test@example.com", "password123", "John", "Doe", role);
        assertNotNull(user);
    }

    @Test
    public void testCreateUser_Fail_EmptyEmail() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Role role = RoleFactory.createRole("BUYER", "Buyer Role");
        User user = UserFactory.createUser("", "password123", "John", "Doe", role);
        assertNull(user);
    }
}
