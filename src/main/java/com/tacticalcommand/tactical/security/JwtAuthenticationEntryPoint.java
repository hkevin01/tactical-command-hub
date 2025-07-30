package com.caribouthunder.tactical.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Authentication Entry Point for handling unauthorized access attempts.
 * 
 * This class handles authentication failures by sending an appropriate HTTP response
 * when users attempt to access protected resources without valid authentication.
 * It implements the AuthenticationEntryPoint interface to provide custom error
 * handling for JWT-based authentication.
 * 
 * Security Features:
 * - Returns HTTP 401 Unauthorized for invalid authentication
 * - Provides detailed error messages for debugging
 * - Prevents authentication bypass attempts
 * - Logs security violations for audit purposes
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles authentication failures by sending HTTP 401 Unauthorized response.
     * 
     * This method is called when an unauthenticated user attempts to access
     * a protected resource. It provides a standardized error response with
     * appropriate HTTP status code and error message.
     * 
     * @param request HTTP request that resulted in authentication failure
     * @param response HTTP response to be sent to the client
     * @param authException exception that caused the authentication failure
     * @throws IOException if an I/O error occurs while writing the response
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        // Set response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Set content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Create error response body
        String errorMessage = "{\n" +
                "  \"timestamp\": \"" + java.time.Instant.now().toString() + "\",\n" +
                "  \"status\": 401,\n" +
                "  \"error\": \"Unauthorized\",\n" +
                "  \"message\": \"Access denied. Valid authentication token required.\",\n" +
                "  \"path\": \"" + request.getRequestURI() + "\"\n" +
                "}";
        
        // Write error response
        response.getWriter().write(errorMessage);
        response.getWriter().flush();
        
        // Log the authentication failure (for security monitoring)
        System.err.println("Authentication failed for request: " + 
                          request.getMethod() + " " + request.getRequestURI() + 
                          " - " + authException.getMessage());
    }
}
