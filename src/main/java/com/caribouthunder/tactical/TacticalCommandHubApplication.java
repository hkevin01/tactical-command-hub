package com.caribouthunder.tactical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Tactical Command Hub.
 * 
 * This application provides a Java-based Command and Control System Simulator
 * that mimics the functionality of military joint operations platforms like GCCS-J.
 * 
 * Features:
 * - Tactical unit positioning and status management
 * - Real-time mission data processing
 * - Multi-domain operations coordination (land, air, sea, cyber)
 * - Operational reports and analytics
 * - Comprehensive audit trails
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableTransactionManagement
public class TacticalCommandHubApplication {

    /**
     * Main method to start the Tactical Command Hub application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TacticalCommandHubApplication.class, args);
    }
}
