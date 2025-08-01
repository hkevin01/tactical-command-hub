package com.tacticalcommand.tactical.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.MilitaryUnit.UnitStatus;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.service.MilitaryUnitService;
import com.tacticalcommand.tactical.service.MissionService;

/**
 * Web Controller for Tactical Dashboard interface.
 * 
 * Provides server-side rendered tactical command interface with real-time updates
 * for unit status, mission progress, and operational overview.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-08-01
 */
@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('COMMANDER') or hasRole('OPERATOR')")
public class TacticalDashboardController {

    private static final String UNITS_ATTRIBUTE = "units";
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 1000);

    private final MilitaryUnitService militaryUnitService;
    private final MissionService missionService;
    private final SimpMessagingTemplate messagingTemplate;

    public TacticalDashboardController(MilitaryUnitService militaryUnitService,
                                     MissionService missionService,
                                     SimpMessagingTemplate messagingTemplate) {
        this.militaryUnitService = militaryUnitService;
        this.missionService = missionService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Main tactical dashboard view.
     * 
     * @param model Spring MVC model
     * @return dashboard template name
     */
    @GetMapping
    public String dashboard(Model model) {
        // Get all units for status overview
        List<MilitaryUnit> units = militaryUnitService.getAllUnits(DEFAULT_PAGEABLE).getContent();
        List<Mission> activeMissions = missionService.getMissionsByStatus(MissionStatus.ACTIVE, DEFAULT_PAGEABLE).getContent();

        // Calculate unit status distribution
        Map<UnitStatus, Long> unitStatusCounts = units.stream()
            .collect(Collectors.groupingBy(MilitaryUnit::getStatus, Collectors.counting()));

        // Calculate mission status distribution
        Map<MissionStatus, Long> missionStatusCounts = activeMissions.stream()
            .collect(Collectors.groupingBy(Mission::getStatus, Collectors.counting()));

        // Recent tactical events (last 10) - sorted by ID since no lastUpdated field
        List<MilitaryUnit> recentlyUpdatedUnits = units.stream()
            .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
            .limit(10)
            .toList();

        model.addAttribute("totalUnits", units.size());
        model.addAttribute("activeMissions", activeMissions.size());
        model.addAttribute("unitStatusCounts", unitStatusCounts);
        model.addAttribute("missionStatusCounts", missionStatusCounts);
        model.addAttribute("recentUpdates", recentlyUpdatedUnits);
        model.addAttribute(UNITS_ATTRIBUTE, units);
        model.addAttribute("missions", activeMissions);

        return "dashboard/tactical-overview";
    }

    /**
     * Real-time data endpoint for dashboard updates.
     * 
     * @return JSON response with current tactical data
     */
    @GetMapping("/api/realtime-data")
    @ResponseBody
    public Map<String, Object> getRealtimeData() {
        List<MilitaryUnit> units = militaryUnitService.getAllUnits(DEFAULT_PAGEABLE).getContent();
        List<Mission> activeMissions = missionService.getMissionsByStatus(MissionStatus.ACTIVE, DEFAULT_PAGEABLE).getContent();

        Map<UnitStatus, Long> unitStatusCounts = units.stream()
            .collect(Collectors.groupingBy(MilitaryUnit::getStatus, Collectors.counting()));

        Map<MissionStatus, Long> missionStatusCounts = activeMissions.stream()
            .collect(Collectors.groupingBy(Mission::getStatus, Collectors.counting()));

        return Map.of(
            "totalUnits", units.size(),
            "activeMissions", activeMissions.size(),
            "readyUnits", unitStatusCounts.getOrDefault(UnitStatus.ACTIVE, 0L),
            "deployedUnits", unitStatusCounts.getOrDefault(UnitStatus.DEPLOYED, 0L),
            "maintenanceUnits", unitStatusCounts.getOrDefault(UnitStatus.MAINTENANCE, 0L),
            "planningMissions", missionStatusCounts.getOrDefault(MissionStatus.PLANNING, 0L),
            "activeMissionsCount", missionStatusCounts.getOrDefault(MissionStatus.ACTIVE, 0L),
            "completedMissions", missionStatusCounts.getOrDefault(MissionStatus.COMPLETED, 0L)
        );
    }

    /**
     * WebSocket fragment update for unit status cards.
     * 
     * @param model Spring MVC model
     * @return fragment template name
     */
    @GetMapping("/fragments/unit-status")
    public String getUnitStatusFragment(Model model) {
        List<MilitaryUnit> units = militaryUnitService.getAllUnits(DEFAULT_PAGEABLE).getContent();
        model.addAttribute(UNITS_ATTRIBUTE, units);
        return "dashboard/fragments/unit-status-cards :: unit-status-grid";
    }

    /**
     * Scheduled task to broadcast real-time updates via WebSocket.
     * Runs every 5 seconds to update connected dashboard clients.
     */
    @Scheduled(fixedRate = 5000)
    public void broadcastDashboardUpdates() {
        Map<String, Object> realtimeData = getRealtimeData();
        messagingTemplate.convertAndSend("/topic/dashboard/updates", realtimeData);
    }

    /**
     * Tactical map view with unit positions.
     * 
     * @param model Spring MVC model
     * @return map template name
     */
    @GetMapping("/map")
    public String tacticalMap(Model model) {
        List<MilitaryUnit> units = militaryUnitService.getAllUnits(DEFAULT_PAGEABLE).getContent();
        
        // Filter units with valid coordinates
        List<MilitaryUnit> mappableUnits = units.stream()
            .filter(unit -> unit.getLatitude() != null && unit.getLongitude() != null)
            .toList();

        model.addAttribute(UNITS_ATTRIBUTE, mappableUnits);
        return "dashboard/tactical-map";
    }
}
