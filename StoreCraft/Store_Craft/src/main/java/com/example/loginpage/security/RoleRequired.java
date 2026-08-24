package com.example.loginpage.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for role-based access control.
 * Place on controller classes or methods to restrict access to specific roles.
 * 
 * Example: @RoleRequired({"ADMIN"}) — only users with the ADMIN role can
 * access.
 * Example: @RoleRequired({"BUYER", "SELLER"}) — either BUYER or SELLER can
 * access.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleRequired {
    String[] value();
}
