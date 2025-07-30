package com.caribouthunder.tactical.security;

import com.caribouthunder.tactical.domain.User;
import com.caribouthunder.tactical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * 
 * This service loads user details from the database for authentication and
 * authorization purposes. It converts the application's User entity into
 * Spring Security's UserDetails object, including roles and account status.
 * 
 * Security Features:
 * - Database-backed user authentication
 * - Role-based authorization mapping
 * - Account status validation (enabled, locked, expired)
 * - Transactional user loading for consistency
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details by username for authentication.
     * 
     * This method is called by Spring Security during authentication to
     * retrieve user information from the database. It eagerly loads user
     * roles to prevent lazy loading issues during authentication.
     * 
     * @param username the username to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user with roles eagerly to avoid lazy loading issues
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with username: " + username));

        return createUserPrincipal(user);
    }

    /**
     * Creates a UserDetails object from the User entity.
     * 
     * @param user the User entity
     * @return UserDetails object for Spring Security
     */
    private UserDetails createUserPrincipal(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(!user.getAccountNonExpired())
                .accountLocked(!user.getAccountNonLocked())
                .credentialsExpired(!user.getCredentialsNonExpired())
                .disabled(!user.getEnabled())
                .build();
    }

    /**
     * Loads user details by user ID (for internal use).
     * 
     * @param userId the user ID to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with id: " + userId));

        return createUserPrincipal(user);
    }
}
