package com.tacticalcommand.tactical.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request DTO for user authentication.
 * 
 * This DTO represents the login credentials sent by clients to authenticate
 * with the tactical command system. It contains username and password
 * validation to ensure proper authentication data is provided.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor
     */
    public LoginRequest() {
    }

    /**
     * Constructor with username and password
     * 
     * @param username the username
     * @param password the password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
