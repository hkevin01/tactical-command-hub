package com.caribouthunder.tactical.dto.auth;

import java.util.List;

/**
 * Login response DTO containing authentication token and user information.
 * 
 * This DTO represents the successful authentication response sent back to
 * clients after successful login. It contains the JWT token and basic
 * user information needed for the client application.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private String rank;
    private String unitAssignment;
    private List<String> roles;
    private long expiresIn;

    /**
     * Default constructor
     */
    public LoginResponse() {
    }

    /**
     * Constructor with token and user information
     * 
     * @param token the JWT token
     * @param username the username
     * @param fullName the user's full name
     * @param email the user's email
     * @param roles the user's roles
     * @param expiresIn token expiration time in seconds
     */
    public LoginResponse(String token, String username, String fullName, String email, List<String> roles, long expiresIn) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUnitAssignment() {
        return unitAssignment;
    }

    public void setUnitAssignment(String unitAssignment) {
        this.unitAssignment = unitAssignment;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
