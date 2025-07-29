package com.caribouthunder.tactical.repository;

import com.caribouthunder.tactical.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 * 
 * This repository provides data access methods for role management including
 * role lookup for authorization and role assignment operations. It extends
 * JpaRepository to provide standard CRUD operations.
 * 
 * Key Features:
 * - Role lookup by name
 * - Role existence checks
 * - Role management operations
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by role name.
     * 
     * @param name the role name to search for
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(Role.RoleName name);

    /**
     * Check if role exists by name.
     * 
     * @param name the role name to check
     * @return true if role exists, false otherwise
     */
    boolean existsByName(Role.RoleName name);
}
