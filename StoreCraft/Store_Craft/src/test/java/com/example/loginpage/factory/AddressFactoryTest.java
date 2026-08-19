package com.example.loginpage.factory;

import com.example.loginpage.model.Address;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddressFactoryTest {

    @Test
    public void testCreateAddress_Success() {
        String userId = Helper.generateShortUUID();
        Address address = AddressFactory.createAddress(userId, "SHIPPING", "John Doe", "1234567890", "123 Main St", "City", "State", "12345", "Country", true);
        assertNotNull(address);
    }

    @Test
    public void testCreateAddress_Fail_MissingRequired() {
        String userId = Helper.generateShortUUID();
        Address address = AddressFactory.createAddress(userId, "SHIPPING", "", "1234567890", "123 Main St", "City", "State", "12345", "Country", true);
        assertNull(address);
    }
}