package com.tacticalcommand.tactical.web;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.domain.Mission.Priority;
import com.tacticalcommand.tactical.service.MissionService;
import com.tacticalcommand.tactical.service.MilitaryUnitService;

import jakarta.validation.Valid;

/**
 * Web Controller for Mission Command interface.
 * 
 * Provides server-side rendered interface for mission planning,
 * execution tracking, and resource allocation.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-08-01
 */
@Controller
@RequestMapping("/missions")
@PreAuthorize("hasRole('COMMANDER') or hasRole('OPERATOR')")
public class MissionCommandController {

    private static final String MISSIONS_ATTRIBUTE = "missions";
    private static final String MISSION_ATTRIBUTE = "mission";
    private static final String REDIRECT_MISSIONS = "redirect:/missions";

    private final MissionService missionService;
    private final MilitaryUnitService militaryUnitService;

    public MissionCommandController(MissionService missionService, MilitaryUnitService militaryUnitService) {
        this.missionService = missionService;
        this.militaryUnitService = militaryUnitService;
    }

    /**
     * Main mission board view with filtering and pagination.
     * 
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @param sort sort parameter (default by mission code)
     * @param status filter by mission status
     * @param priority filter by mission priority
     * @param model Spring MVC model
     * @return mission board template
     */
    @GetMapping
    public String missionBoard(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "missionCode") String sort,
                             @RequestParam(required = false) MissionStatus status,
                             @RequestParam(required = false) Priority priority,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Mission> missionsPage;

        if (status != null) {
            missionsPage = missionService.getMissionsByStatus(status, pageable);
        } else {
            // Get all missions using the available method
            List<Mission> allMissions = missionService.getAllMissions();
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allMissions.size());
            List<Mission> pageContent = allMissions.subList(start, end);
            missionsPage = new PageImpl<>(pageContent, pageable, allMissions.size());
        }

        // Apply priority filtering in-memory if needed
        List<Mission> filteredMissions = missionsPage.getContent();
        if (priority != null) {
            filteredMissions = filteredMissions.stream()
                .filter(mission -> mission.getPriority().equals(priority))
                .toList();
        }

        model.addAttribute(MISSIONS_ATTRIBUTE, missionsPage);
        model.addAttribute("filteredMissions", filteredMissions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", missionsPage.getTotalPages());
        model.addAttribute("totalElements", missionsPage.getTotalElements());
        model.addAttribute("statusFilter", status);
        model.addAttribute("priorityFilter", priority);
        model.addAttribute("allStatuses", MissionStatus.values());
        model.addAttribute("allPriorities", Priority.values());

        return "missions/mission-board";
    }

    /**
     * Mission detail view showing comprehensive mission information.
     * 
     * @param id mission ID
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for error handling
     * @return mission detail template or redirect to mission board
     */
    @GetMapping("/{id}")
    public String missionDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Mission> missionOpt = missionService.getMissionById(id);
        
        if (missionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mission not found with ID: " + id);
            return REDIRECT_MISSIONS;
        }

        Mission mission = missionOpt.get();
        model.addAttribute(MISSION_ATTRIBUTE, mission);
        
        // Note: Unit assignment feature will be implemented when backend supports it
        model.addAttribute("assignedUnits", List.of());

        return "missions/mission-detail";
    }

    /**
     * Show form for creating a new mission.
     * 
     * @param model Spring MVC model
     * @return mission planning form template
     */
    @GetMapping("/new")
    public String showMissionPlanningForm(Model model) {
        model.addAttribute(MISSION_ATTRIBUTE, new Mission());
        model.addAttribute("isEdit", false);
        addFormAttributes(model);
        return "missions/mission-planning";
    }

    /**
     * Show form for editing an existing mission.
     * 
     * @param id mission ID
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for error handling
     * @return mission planning form template or redirect to mission board
     */
    @GetMapping("/{id}/edit")
    public String showEditMissionForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Mission> missionOpt = missionService.getMissionById(id);
        
        if (missionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mission not found with ID: " + id);
            return REDIRECT_MISSIONS;
        }

        model.addAttribute(MISSION_ATTRIBUTE, missionOpt.get());
        model.addAttribute("isEdit", true);
        addFormAttributes(model);
        return "missions/mission-planning";
    }

    /**
     * Handle mission creation form submission.
     * 
     * @param mission the mission to create
     * @param bindingResult form validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for success/error messages
     * @return redirect to mission detail or back to form with errors
     */
    @PostMapping
    public String createMission(@Valid @ModelAttribute(MISSION_ATTRIBUTE) Mission mission,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            addFormAttributes(model);
            return "missions/mission-planning";
        }

        try {
            Mission createdMission = missionService.createMission(mission);
            redirectAttributes.addFlashAttribute("success", 
                "Mission '" + createdMission.getMissionCode() + "' created successfully");
            return "redirect:/missions/" + createdMission.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error creating mission: " + e.getMessage());
            model.addAttribute("isEdit", false);
            addFormAttributes(model);
            return "missions/mission-planning";
        }
    }

    /**
     * Handle mission update form submission.
     * 
     * @param id mission ID
     * @param mission the updated mission data
     * @param bindingResult form validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for success/error messages
     * @return redirect to mission detail or back to form with errors
     */
    @PostMapping("/{id}")
    public String updateMission(@PathVariable Long id,
                              @Valid @ModelAttribute(MISSION_ATTRIBUTE) Mission mission,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            mission.setId(id);
            model.addAttribute("isEdit", true);
            addFormAttributes(model);
            return "missions/mission-planning";
        }

        try {
            Mission updatedMission = missionService.updateMission(id, mission);
            redirectAttributes.addFlashAttribute("success", 
                "Mission '" + updatedMission.getMissionCode() + "' updated successfully");
            return "redirect:/missions/" + updatedMission.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error updating mission: " + e.getMessage());
            mission.setId(id);
            model.addAttribute("isEdit", true);
            addFormAttributes(model);
            return "missions/mission-planning";
        }
    }

    /**
     * Mission execution dashboard for active missions.
     * 
     * @param model Spring MVC model
     * @return mission execution template
     */
    @GetMapping("/execution")
    public String missionExecution(Model model) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Mission> activeMissions = missionService.getMissionsByStatus(MissionStatus.ACTIVE, pageable);
        
        model.addAttribute("activeMissions", activeMissions.getContent());
        return "missions/mission-execution";
    }

    /**
     * Add common form attributes for dropdowns and available units.
     * 
     * @param model Spring MVC model
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("allStatuses", MissionStatus.values());
        model.addAttribute("allPriorities", Priority.values());
        model.addAttribute("allMissionTypes", Mission.MissionType.values());
        
        // Get available units for assignment
        List<MilitaryUnit> availableUnits = militaryUnitService.getAllUnits(PageRequest.of(0, 1000)).getContent();
        model.addAttribute("availableUnits", availableUnits);
    }
}
