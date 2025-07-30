package com.tacticalcommand.tactical.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Role entity representing user roles for authorization in the tactical command system.
 * 
 * This entity defines the available roles that can be assigned to users for
 * role-based access control (RBAC). Each role grants specific permissions
 * and capabilities within the system.
 * 
 * Available Roles:
 * - ROLE_COMMANDER: Full system access, mission command authority
 * - ROLE_OPERATOR: Operational access for mission execution
 * - ROLE_ANALYST: Analysis and intelligence operations access
 * - ROLE_VIEWER: Read-only monitoring and reporting access
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    /**
     * Role name (e.g., ROLE_COMMANDER, ROLE_OPERATOR)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private RoleName name;

    /**
     * Human-readable description of the role
     */
    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    /**
     * Users assigned to this role
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /**
     * Default constructor
     */
    public Role() {
    }

    /**
     * Constructor with role name
     * 
     * @param name the role name
     */
    public Role(RoleName name) {
        this.name = name;
    }

    /**
     * Constructor with role name and description
     * 
     * @param name the role name
     * @param description the role description
     */
    public Role(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     * Enumeration of available role names
     */
    public enum RoleName {
        /**
         * Commander role - Full system access and command authority
         */
        ROLE_COMMANDER("Full system access with mission command authority"),
        
        /**
         * Operator role - Operational access for mission execution
         */
        ROLE_OPERATOR("Operational access for mission planning and execution"),
        
        /**
         * Analyst role - Analysis and intelligence operations
         */
        ROLE_ANALYST("Intelligence analysis and reporting capabilities"),
        
        /**
         * Viewer role - Read-only monitoring access
         */
        ROLE_VIEWER("Read-only access for monitoring and basic reporting");

        private final String description;

        RoleName(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Get role name without the ROLE_ prefix
         * 
         * @return role name without prefix
         */
        public String getShortName() {
            return this.name().substring(5); // Remove "ROLE_" prefix
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name=" + name +
                ", description='" + description + '\'' +
                '}';
    }
}
