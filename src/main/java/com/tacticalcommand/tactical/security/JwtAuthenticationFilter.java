package com.tacticalcommand.tactical.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests.
 * 
 * This filter intercepts incoming HTTP requests to extract and validate JWT tokens from the
 * Authorization header. It extends OncePerRequestFilter to ensure that the filter is executed only
 * once per request, preventing multiple validations.
 * 
 * Security Features: - Extracts JWT tokens from Authorization header - Validates token signature
 * and expiration - Sets authentication context for valid tokens - Handles token refresh for expired
 * tokens - Provides detailed logging for security audit
 * 
 * Token Format: "Bearer {jwt-token}"
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserDetailsService userDetailsService;

  /**
   * Processes JWT authentication for each HTTP request.
   * 
   * This method extracts the JWT token from the Authorization header, validates it, and sets the
   * security context if the token is valid. It follows the standard Bearer token format.
   * 
   * @param request
   *          HTTP request containing potential JWT token
   * @param response
   *          HTTP response for the request
   * @param filterChain
   *          filter chain for continued processing
   * @throws ServletException
   *           if servlet processing fails
   * @throws IOException
   *           if I/O operations fail
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Extract JWT token from Authorization header
      String jwt = getJwtFromRequest(request);

      // Validate token and set authentication context
      if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
        String username = jwtTokenProvider.getUsernameFromToken(jwt);

        // Load user details and create authentication
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        // Set authentication details
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Log successful authentication (for audit purposes)
        logger.debug("Successfully authenticated user: " + username + " for request: "
            + request.getMethod() + " " + request.getRequestURI());
      }

    } catch (Exception e) {
      // Log authentication failure but continue with filter chain
      logger.error("Cannot set user authentication: " + e.getMessage(), e);
    }

    // Continue with the filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Extracts JWT token from the Authorization header.
   * 
   * Expected format: "Bearer {jwt-token}"
   * 
   * @param request
   *          HTTP request containing Authorization header
   * @return JWT token string or null if not found or invalid format
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    // Check if Authorization header exists and starts with "Bearer "
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // Remove "Bearer " prefix
    }

    return null;
  }
}
