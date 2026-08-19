package com.example.loginpage.factory;

import com.example.loginpage.model.Role;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleFactoryTest {

    @Test
    public void testCreateRole_Success() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Role role = RoleFactory.createRole("ADMIN", "Admin Role");
        assertNotNull(role);
    }

    @Test
    public void testCreateRole_Fail_EmptyName() {
        String userId = Helper.generateShortUUID();
        Role role = RoleFactory.createRole("", "Empty Role Name");
        assertNull(role);
    }
}
