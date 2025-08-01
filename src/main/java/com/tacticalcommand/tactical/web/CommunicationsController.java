package com.tacticalcommand.tactical.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web Controller for Communications interface.
 * 
 * Provides server-side rendered interface for tactical communications,
 * message center, and alert management.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-08-01
 */
@Controller
@RequestMapping("/communications")
@PreAuthorize("hasRole('COMMANDER') or hasRole('OPERATOR')")
public class CommunicationsController {

    /**
     * Main communications center view.
     * 
     * @param model Spring MVC model
     * @return communications center template
     */
    @GetMapping
    public String communicationsCenter(Model model) {
        // Mock data for communications
        model.addAttribute("activeChannels", 8);
        model.addAttribute("messagesReceived", 142);
        model.addAttribute("messagesSent", 89);
        model.addAttribute("alertsActive", 3);
        
        return "communications/message-center";
    }

    /**
     * Alert management interface.
     * 
     * @param model Spring MVC model
     * @return alert management template
     */
    @GetMapping("/alerts")
    public String alertManagement(Model model) {
        model.addAttribute("criticalAlerts", 1);
        model.addAttribute("warningAlerts", 2);
        model.addAttribute("infoAlerts", 5);
        
        return "communications/alert-management";
    }

    /**
     * Communication status monitoring.
     * 
     * @param model Spring MVC model
     * @return communication status template
     */
    @GetMapping("/status")
    public String communicationStatus(Model model) {
        model.addAttribute("operationalChannels", 12);
        model.addAttribute("degradedChannels", 2);
        model.addAttribute("offlineChannels", 1);
        model.addAttribute("networkReliability", 94);
        
        return "communications/comm-status";
    }

    /**
     * Command net interface for priority communications.
     * 
     * @param model Spring MVC model
     * @return command net template
     */
    @GetMapping("/command-net")
    public String commandNet(Model model) {
        model.addAttribute("commanderOnline", true);
        model.addAttribute("unitsConnected", 15);
        model.addAttribute("priorityMessages", 3);
        
        return "communications/command-net";
    }
}
