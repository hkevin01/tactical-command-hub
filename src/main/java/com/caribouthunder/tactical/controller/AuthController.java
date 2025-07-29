package com.caribouthunder.tactical.controller;

import com.caribouthunder.tactical.domain.User;
import com.caribouthunder.tactical.dto.auth.LoginRequest;
import com.caribouthunder.tactical.dto.auth.LoginResponse;
import com.caribouthunder.tactical.repository.UserRepository;
import com.caribouthunder.tactical.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authentication Controller for handling user login and token management.
 * 
 * This controller provides endpoints for user authentication, token generation,
 * and user session management. It implements JWT-based authentication for
 * the tactical command system with role-based authorization.
 * 
 * Security Features:
 * - JWT token generation and validation
 * - Role-based authentication
 * - Secure credential handling
 * - Token refresh capabilities
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and token management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticates user and generates JWT token.
     * 
     * @param loginRequest login credentials
     * @return JWT token and user information
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByUsernameWithRoles(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Extract role names
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().getShortName())
                .collect(Collectors.toList());

        // Create response
        LoginResponse response = new LoginResponse(
            jwt,
            user.getUsername(),
            user.getFullName(),
            user.getEmail(),
            roles,
            jwtTokenProvider.getExpirationTime()
        );
        
        response.setRank(user.getRank());
        response.setUnitAssignment(user.getUnitAssignment());

        return ResponseEntity.ok(response);
    }

    /**
     * Validates current JWT token and returns user information.
     * 
     * @return current user information
     */
    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get current authenticated user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<LoginResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().getShortName())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRank(user.getRank());
        response.setUnitAssignment(user.getUnitAssignment());
        response.setRoles(roles);

        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint (client-side token invalidation).
     * 
     * @return logout confirmation
     */
    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout user (client should discard token)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<String> logout() {
        // Since JWT is stateless, logout is handled client-side by discarding the token
        // In production, you might want to implement a token blacklist
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful. Please discard your token.");
    }

    /**
     * Health check endpoint for authentication service.
     * 
     * @return service status
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication Health Check", description = "Check authentication service health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is operational");
    }
}
