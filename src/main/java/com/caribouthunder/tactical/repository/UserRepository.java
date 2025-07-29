package com.caribouthunder.tactical.repository;

import com.caribouthunder.tactical.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * This repository provides data access methods for user management including
 * authentication, authorization, and user profile operations. It extends
 * JpaRepository to provide standard CRUD operations and includes custom
 * queries for user-specific operations.
 * 
 * Key Features:
 * - User authentication queries
 * - Role-based user retrieval
 * - Account status management
 * - User profile operations
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username for authentication.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username already exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email already exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find user by username with roles eagerly loaded.
     * 
     * @param username the username to search for
     * @return Optional containing the user with roles if found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Find all users with a specific role.
     * 
     * @param roleName the role name to search for
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    java.util.List<User> findByRoleName(@Param("roleName") com.caribouthunder.tactical.domain.Role.RoleName roleName);

    /**
     * Find all enabled users.
     * 
     * @return list of enabled users
     */
    java.util.List<User> findByEnabledTrue();

    /**
     * Find all disabled users.
     * 
     * @return list of disabled users
     */
    java.util.List<User> findByEnabledFalse();

    /**
     * Find users by unit assignment.
     * 
     * @param unitAssignment the unit assignment to search for
     * @return list of users in the specified unit
     */
    java.util.List<User> findByUnitAssignment(String unitAssignment);

    /**
     * Find users by clearance level.
     * 
     * @param clearanceLevel the clearance level to search for
     * @return list of users with the specified clearance level
     */
    java.util.List<User> findByClearanceLevel(String clearanceLevel);

    /**
     * Find users by rank.
     * 
     * @param rank the rank to search for
     * @return list of users with the specified rank
     */
    java.util.List<User> findByRank(String rank);

    /**
     * Count total number of users.
     * 
     * @return total count of users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    /**
     * Count active users (enabled and not locked).
     * 
     * @return count of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true AND u.accountNonLocked = true")
    long countActiveUsers();

    /**
     * Find users by partial username match (case-insensitive).
     * 
     * @param username partial username to search for
     * @return list of users with matching usernames
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    java.util.List<User> findByUsernameContainingIgnoreCase(@Param("username") String username);

    /**
     * Find users by partial full name match (case-insensitive).
     * 
     * @param fullName partial full name to search for
     * @return list of users with matching full names
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    java.util.List<User> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName);
}
