package com.example.loginpage.util;

import org.apache.commons.validator.EmailValidator;
import java.util.UUID;

public class Helper {

    // ============ UUID GENERATION ============

    public static String generateShortUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // ============ STRING VALIDATIONS ============

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // ============ EMAIL VALIDATION ============
    public static boolean isValidEmail(String emailAddress) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(emailAddress);
    }

    // ============ NUMERIC VALIDATIONS ============

    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    public static boolean isValidNumber(int number) {
        return number > 0;
    }

}