package com.tacticalcommand.tactical.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.User;
import com.tacticalcommand.tactical.repository.UserRepository;

/**
 * Service for managing user operations including profile management and clearance verification.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Find user by username.
     *
     * @param username Username to search for
     * @return User entity if found
     * @throws RuntimeException if user not found
     */
    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        return userOpt.get();
    }

    /**
     * Find user by ID.
     *
     * @param id User ID to search for
     * @return User entity if found
     * @throws RuntimeException if user not found
     */
    public User findById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        return userOpt.get();
    }

    /**
     * Find users with minimum clearance level for secure messaging.
     *
     * @param minimumClearance Minimum required clearance level
     * @return List of users with adequate clearance
     */
    public List<User> findUsersByMinimumClearance(String minimumClearance) {
        // This would need to be implemented based on clearance hierarchy
        // For now, return all active users - this should be enhanced
        return userRepository.findByEnabledTrue();
    }

    /**
     * Check if user has required clearance level.
     *
     * @param user User to check
     * @param requiredClearance Required clearance level
     * @return true if user has adequate clearance
     */
    public boolean hasRequiredClearance(User user, String requiredClearance) {
        String userClearance = user.getClearanceLevel();
        
        // Simple clearance hierarchy check
        if ("UNCLASSIFIED".equals(requiredClearance)) return true;
        if ("CONFIDENTIAL".equals(requiredClearance)) return !"UNCLASSIFIED".equals(userClearance);
        if ("SECRET".equals(requiredClearance)) return "SECRET".equals(userClearance) || "TOP_SECRET".equals(userClearance);
        if ("TOP_SECRET".equals(requiredClearance)) return "TOP_SECRET".equals(userClearance);
        
        return false;
    }

    /**
     * Get all active users.
     *
     * @return List of active users
     */
    public List<User> findAllActiveUsers() {
        return userRepository.findByEnabledTrue();
    }

    /**
     * Save or update user.
     *
     * @param user User entity to save
     * @return Saved user entity
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
