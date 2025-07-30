package com.tacticalcommand.tactical.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Token Provider for generating and validating JSON Web Tokens.
 * 
 * This component handles all JWT operations including token generation,
 * validation, and extraction of user information. It uses HMAC-SHA256
 * algorithm for token signing and provides secure token management
 * for the tactical command system.
 * 
 * Security Features:
 * - Secure token generation with configurable expiration
 * - HMAC-SHA256 signature validation
 * - Username and role extraction from tokens
 * - Token expiration and format validation
 * - Secure secret key management
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Component
public class JwtTokenProvider {

    @Value("${tactical.jwt.secret:TacticalCommandHubSecretKeyForJWTTokenGeneration2024!@#$%^&*()}")
    private String jwtSecret;

    @Value("${tactical.jwt.expiration:86400}")
    private int jwtExpirationInSeconds;

    /**
     * Generates a JWT token for the authenticated user.
     * 
     * @param authentication Spring Security authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for a specific username.
     * 
     * @param username the username to generate token for
     * @return JWT token string
     */
    public String generateTokenFromUsername(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts username from JWT token.
     * 
     * @param token JWT token string
     * @return username from token subject
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }

    /**
     * Validates the JWT token.
     * 
     * @param authToken JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gets the token expiration time in seconds.
     * 
     * @return expiration time in seconds
     */
    public int getExpirationTime() {
        return jwtExpirationInSeconds;
    }

    /**
     * Extracts expiration date from JWT token.
     * 
     * @param token JWT token string
     * @return expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getExpiration();
    }

    /**
     * Checks if the token is expired.
     * 
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Creates a secure signing key from the JWT secret.
     * 
     * @return SecretKey for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
