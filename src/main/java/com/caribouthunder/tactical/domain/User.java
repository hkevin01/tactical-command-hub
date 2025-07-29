package com.caribouthunder.tactical.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing system users with authentication and authorization data.
 * 
 * This entity stores user account information including credentials, roles, and
 * profile data for the tactical command system. It extends BaseEntity to inherit
 * audit trail functionality and supports role-based access control.
 * 
 * Security Features:
 * - Encrypted password storage
 * - Role-based authorization
 * - Account status management (enabled/disabled)
 * - Email-based unique identification
 * - Audit trail for account changes
 * 
 * Roles:
 * - COMMANDER: Full system access and mission command authority
 * - OPERATOR: Operational access for mission execution
 * - ANALYST: Read/analysis access for intelligence operations
 * - VIEWER: Read-only access for monitoring and reporting
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
public class User extends BaseEntity {

    /**
     * Unique username for authentication
     */
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User's full name for display purposes
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * User's email address for notifications and recovery
     */
    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Encrypted password for authentication
     */
    @NotBlank
    @Size(max = 120)
    @Column(name = "password", nullable = false, length = 120)
    private String password;

    /**
     * Military rank of the user
     */
    @Size(max = 50)
    @Column(name = "rank", length = 50)
    private String rank;

    /**
     * Unit assignment or organization
     */
    @Size(max = 100)
    @Column(name = "unit_assignment", length = 100)
    private String unitAssignment;

    /**
     * Security clearance level
     */
    @Size(max = 50)
    @Column(name = "clearance_level", length = 50)
    private String clearanceLevel;

    /**
     * Account enabled status
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * Account locked status
     */
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    /**
     * Account expired status
     */
    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired = true;

    /**
     * Credentials expired status
     */
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired = true;

    /**
     * User roles for authorization
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with basic user information
     * 
     * @param username unique username
     * @param fullName user's full name
     * @param email user's email address
     * @param password encrypted password
     */
    public User(String username, String fullName, String email, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(String clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Helper method to add a role to the user
     * 
     * @param role role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Helper method to remove a role from the user
     * 
     * @param role role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Check if user has a specific role
     * 
     * @param roleName name of the role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                   .anyMatch(role -> role.getName().equals(roleName));
    }
}
