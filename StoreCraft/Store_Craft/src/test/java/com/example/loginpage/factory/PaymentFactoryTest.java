package com.example.loginpage.factory;

import com.example.loginpage.model.Payment;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentFactoryTest {

    @Test
    public void testCreatePayment_Success() {
        String userId = Helper.generateShortUUID();
        Payment payment = PaymentFactory.createPayment("ORD123", userId, new BigDecimal("100.00"), "USD", "CREDIT_CARD", "1234", "John Doe");
        assertNotNull(payment);
    }

    @Test
    public void testCreatePayment_Fail_InvalidAmount() {
        String userId = Helper.generateShortUUID();
        Payment payment = PaymentFactory.createPayment("ORD123", userId, new BigDecimal("-50.00"), "USD", "CREDIT_CARD", "1234", "John Doe");
        assertNull(payment);
    }
}
