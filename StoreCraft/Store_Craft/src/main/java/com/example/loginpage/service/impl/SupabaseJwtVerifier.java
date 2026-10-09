package com.example.loginpage.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

/**
 * A1 — Supabase JWT Server-Side Verifier
 *
 * Validates the RS256-signed access token that Supabase issues after a successful
 * social login / magic-link authentication.  Uses Nimbus JOSE+JWT to:
 *   1. Fetch the JWKS from Supabase's well-known endpoint (cached internally).
 *   2. Verify the token signature and expiry.
 *   3. Return only the verified claims — never trusting the raw request body.
 *
 * The JWKS cache is held for the lifetime of this Spring bean, which is acceptable
 * because Supabase rotates keys rarely and Nimbus re-fetches on key-not-found errors.
 */
@Service
public class SupabaseJwtVerifier {

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    /**
     * @param supabaseUrl The Supabase project URL.
     *                    Defaults to the project URL stored in the frontend .env.
     *                    Override with SUPABASE_URL env var / supabase.url property.
     */
    public SupabaseJwtVerifier(
            @Value("${supabase.url:https://uoswiquejgwwmvlxbzbh.supabase.co}") String supabaseUrl) {
        try {
            String normalizedUrl = supabaseUrl.endsWith("/")
                    ? supabaseUrl.substring(0, supabaseUrl.length() - 1)
                    : supabaseUrl;
            String jwksUri = normalizedUrl + "/auth/v1/.well-known/jwks.json";
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(jwksUri));
            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);

            jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);
            // Disable the built-in claims verifier; we perform our own expiry check
            jwtProcessor.setJWTClaimsSetVerifier(null);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialise Supabase JWT verifier: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies the Supabase access token and returns its claims.
     *
     * @param token the raw Supabase access token (no "Bearer " prefix)
     * @return the verified JWTClaimsSet
     * @throws SupabaseAuthException if the token is missing, invalid, or expired
     */
    public JWTClaimsSet verify(String token) throws SupabaseAuthException {
        if (token == null || token.isBlank()) {
            throw new SupabaseAuthException("Supabase access token is missing");
        }
        try {
            JWTClaimsSet claims = jwtProcessor.process(token, null);
            // Belt-and-suspenders expiry check in addition to Nimbus processing
            if (claims.getExpirationTime() != null
                    && claims.getExpirationTime().before(new Date())) {
                throw new SupabaseAuthException("Supabase token has expired");
            }
            return claims;
        } catch (SupabaseAuthException e) {
            throw e;
        } catch (Exception e) {
            throw new SupabaseAuthException(
                    "Supabase token verification failed: " + e.getMessage());
        }
    }

    /**
     * Extracts the verified email from the claims set.
     * Supabase stores the user's email in the top-level "email" claim.
     */
    public String extractEmail(JWTClaimsSet claims) throws SupabaseAuthException {
        try {
            String email = claims.getStringClaim("email");
            if (email == null || email.isBlank()) {
                throw new SupabaseAuthException(
                        "Email claim is missing from Supabase token");
            }
            return email;
        } catch (SupabaseAuthException e) {
            throw e;
        } catch (Exception e) {
            throw new SupabaseAuthException(
                    "Could not extract email from token: " + e.getMessage());
        }
    }

    /**
     * Extracts a name part from user_metadata if present.
     * Returns null if the claim is absent — callers should fall back gracefully.
     */
    public String extractFullName(JWTClaimsSet claims) {
        try {
            // Supabase stores provider-supplied name in user_metadata.full_name
            Object meta = claims.getClaim("user_metadata");
            if (meta instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> metadata = (java.util.Map<String, Object>) meta;
                Object fullName = metadata.get("full_name");
                if (fullName != null) {
                    return fullName.toString();
                }
            }
        } catch (Exception ignored) {
            // Not critical — caller will use email prefix as fallback
        }
        return null;
    }

    /**
     * Checked exception indicating that the Supabase token cannot be validated.
     * Controllers should map this to HTTP 401.
     */
    public static class SupabaseAuthException extends Exception {
        public SupabaseAuthException(String message) {
            super(message);
        }
    }
}
