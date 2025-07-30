package com.tacticalcommand.tactical.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tacticalcommand.tactical.security.JwtAuthenticationEntryPoint;
import com.tacticalcommand.tactical.security.JwtAuthenticationFilter;

/**
 * Security configuration for the Tactical Command Hub application.
 * 
 * This configuration implements JWT-based authentication with role-based authorization following
 * military security standards. It provides secure access control for command and control
 * operations.
 * 
 * Security Features: - JWT token authentication - Role-based access control (COMMANDER, OPERATOR,
 * ANALYST, VIEWER) - Stateless session management - CORS configuration for frontend integration -
 * Method-level security annotations
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  private UserDetailsService userDetailsService;

  /**
   * Configures the main security filter chain with JWT authentication and authorization rules.
   * 
   * @param http
   *          HttpSecurity configuration object
   * @return SecurityFilterChain configured security filter chain
   * @throws Exception
   *           if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        // Disable CSRF for stateless API
        .csrf(AbstractHttpConfigurer::disable)

        // Configure CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // Configure session management
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Configure authentication entry point
        .exceptionHandling(
            exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))

        // Configure authorization rules
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/api/v1/auth/**").permitAll().requestMatchers("/api/v1/health/**")
            .permitAll().requestMatchers("/actuator/health").permitAll()

            // API documentation endpoints
            .requestMatchers("/v3/api-docs/**").permitAll().requestMatchers("/swagger-ui/**")
            .permitAll().requestMatchers("/swagger-ui.html").permitAll()

            // Military units endpoints - role-based access
            .requestMatchers("/api/v1/units/**")
            .hasAnyRole("COMMANDER", "OPERATOR", "ANALYST", "VIEWER")
            .requestMatchers("/api/v1/missions/**").hasAnyRole("COMMANDER", "OPERATOR", "ANALYST")
            .requestMatchers("/api/v1/reports/**").hasAnyRole("COMMANDER", "OPERATOR", "ANALYST")

            // Admin endpoints - commanders only
            .requestMatchers("/api/v1/admin/**").hasRole("COMMANDER")

            // All other requests require authentication
            .anyRequest().authenticated())

        // Add JWT filter before username/password authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        .build();
  }

  /**
   * Configures CORS (Cross-Origin Resource Sharing) settings for frontend integration.
   * 
   * @return CorsConfigurationSource CORS configuration
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow specific origins (configure based on your frontend URLs)
    configuration.setAllowedOriginPatterns(List.of("*"));

    // Allow specific HTTP methods
    configuration
        .setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    // Allow specific headers
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Allow credentials
    configuration.setAllowCredentials(true);

    // Expose specific headers to the client
    configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);

    return source;
  }

  /**
   * Configures password encoder using BCrypt with strong hashing.
   * 
   * @return PasswordEncoder BCrypt password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strong hashing with 12 rounds
  }

  /**
   * Configures the authentication provider with custom user details service.
   * 
   * @return DaoAuthenticationProvider configured authentication provider
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Provides the authentication manager bean.
   * 
   * @param config
   *          AuthenticationConfiguration
   * @return AuthenticationManager authentication manager
   * @throws Exception
   *           if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
