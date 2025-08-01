package com.tacticalcommand.tactical.web;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.MilitaryUnit.UnitStatus;
import com.tacticalcommand.tactical.domain.MilitaryUnit.OperationalDomain;
import com.tacticalcommand.tactical.service.MilitaryUnitService;

import jakarta.validation.Valid;

/**
 * Web Controller for Unit Management interface.
 * 
 * Provides server-side rendered interface for military unit management
 * including unit roster, status updates, and position tracking.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-08-01
 */
@Controller
@RequestMapping("/units")
@PreAuthorize("hasRole('COMMANDER') or hasRole('OPERATOR')")
public class UnitManagementController {

    private static final String UNITS_ATTRIBUTE = "units";
    private static final String UNIT_ATTRIBUTE = "unit";
    private static final String REDIRECT_UNITS = "redirect:/units";

    private final MilitaryUnitService militaryUnitService;

    public UnitManagementController(MilitaryUnitService militaryUnitService) {
        this.militaryUnitService = militaryUnitService;
    }

    /**
     * Main unit roster view with filtering and pagination.
     * 
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @param sort sort parameter (default by callsign)
     * @param status filter by unit status
     * @param domain filter by operational domain
     * @param search search by callsign or unit name
     * @param model Spring MVC model
     * @return unit roster template
     */
    @GetMapping
    public String unitRoster(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(defaultValue = "callsign") String sort,
                           @RequestParam(required = false) UnitStatus status,
                           @RequestParam(required = false) OperationalDomain domain,
                           @RequestParam(required = false) String search,
                           Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<MilitaryUnit> unitsPage = militaryUnitService.getAllUnits(pageable);

        // Apply filtering in-memory for now (could be optimized with repository methods)
        List<MilitaryUnit> filteredUnits = unitsPage.getContent().stream()
            .filter(unit -> search == null || search.trim().isEmpty() || 
                    unit.getCallsign().toLowerCase().contains(search.toLowerCase()) ||
                    unit.getUnitName().toLowerCase().contains(search.toLowerCase()))
            .filter(unit -> status == null || unit.getStatus().equals(status))
            .filter(unit -> domain == null || unit.getDomain().equals(domain))
            .toList();

        model.addAttribute(UNITS_ATTRIBUTE, unitsPage);
        model.addAttribute("filteredUnits", filteredUnits);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", unitsPage.getTotalPages());
        model.addAttribute("totalElements", unitsPage.getTotalElements());
        model.addAttribute("statusFilter", status);
        model.addAttribute("domainFilter", domain);
        model.addAttribute("searchFilter", search);
        model.addAttribute("allStatuses", UnitStatus.values());
        model.addAttribute("allDomains", OperationalDomain.values());

        return "units/unit-roster";
    }

    /**
     * Unit detail view showing comprehensive unit information.
     * 
     * @param id unit ID
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for error handling
     * @return unit detail template or redirect to roster
     */
    @GetMapping("/{id}")
    public String unitDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MilitaryUnit> unitOpt = militaryUnitService.getUnitById(id);
        
        if (unitOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Unit not found with ID: " + id);
            return REDIRECT_UNITS;
        }

        model.addAttribute(UNIT_ATTRIBUTE, unitOpt.get());
        return "units/unit-detail";
    }

    /**
     * Show form for creating a new unit.
     * 
     * @param model Spring MVC model
     * @return unit form template
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute(UNIT_ATTRIBUTE, new MilitaryUnit());
        model.addAttribute("isEdit", false);
        addFormAttributes(model);
        return "units/unit-form";
    }

    /**
     * Show form for editing an existing unit.
     * 
     * @param id unit ID
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for error handling
     * @return unit form template or redirect to roster
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MilitaryUnit> unitOpt = militaryUnitService.getUnitById(id);
        
        if (unitOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Unit not found with ID: " + id);
            return REDIRECT_UNITS;
        }

        model.addAttribute(UNIT_ATTRIBUTE, unitOpt.get());
        model.addAttribute("isEdit", true);
        addFormAttributes(model);
        return "units/unit-form";
    }

    /**
     * Handle unit creation form submission.
     * 
     * @param unit the unit to create
     * @param bindingResult form validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for success/error messages
     * @return redirect to unit detail or back to form with errors
     */
    @PostMapping
    public String createUnit(@Valid @ModelAttribute(UNIT_ATTRIBUTE) MilitaryUnit unit,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            addFormAttributes(model);
            return "units/unit-form";
        }

        try {
            MilitaryUnit createdUnit = militaryUnitService.createUnit(unit);
            redirectAttributes.addFlashAttribute("success", 
                "Unit '" + createdUnit.getCallsign() + "' created successfully");
            return "redirect:/units/" + createdUnit.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error creating unit: " + e.getMessage());
            model.addAttribute("isEdit", false);
            addFormAttributes(model);
            return "units/unit-form";
        }
    }

    /**
     * Handle unit update form submission.
     * 
     * @param id unit ID
     * @param unit the updated unit data
     * @param bindingResult form validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect attributes for success/error messages
     * @return redirect to unit detail or back to form with errors
     */
    @PostMapping("/{id}")
    public String updateUnit(@PathVariable Long id,
                           @Valid @ModelAttribute(UNIT_ATTRIBUTE) MilitaryUnit unit,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            unit.setId(id); // Ensure ID is set for edit mode
            model.addAttribute("isEdit", true);
            addFormAttributes(model);
            return "units/unit-form";
        }

        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateUnit(id, unit);
            redirectAttributes.addFlashAttribute("success", 
                "Unit '" + updatedUnit.getCallsign() + "' updated successfully");
            return "redirect:/units/" + updatedUnit.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error updating unit: " + e.getMessage());
            unit.setId(id);
            model.addAttribute("isEdit", true);
            addFormAttributes(model);
            return "units/unit-form";
        }
    }

    /**
     * Handle unit deletion.
     * 
     * @param id unit ID
     * @param redirectAttributes redirect attributes for success/error messages
     * @return redirect to unit roster
     */
    @PostMapping("/{id}/delete")
    public String deleteUnit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<MilitaryUnit> unitOpt = militaryUnitService.getUnitById(id);
            if (unitOpt.isPresent()) {
                militaryUnitService.deleteUnit(id);
                redirectAttributes.addFlashAttribute("success", 
                    "Unit '" + unitOpt.get().getCallsign() + "' deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Unit not found with ID: " + id);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting unit: " + e.getMessage());
        }

        return REDIRECT_UNITS;
    }

    /**
     * Unit position map view.
     * 
     * @param model Spring MVC model
     * @return unit position map template
     */
    @GetMapping("/map")
    public String unitPositionMap(Model model) {
        List<MilitaryUnit> units = militaryUnitService.getAllUnits(PageRequest.of(0, 1000)).getContent();
        
        // Filter units with valid coordinates
        List<MilitaryUnit> mappableUnits = units.stream()
            .filter(unit -> unit.getLatitude() != null && unit.getLongitude() != null)
            .toList();

        model.addAttribute(UNITS_ATTRIBUTE, mappableUnits);
        return "units/unit-position-map";
    }

    /**
     * Add common form attributes for dropdowns and enums.
     * 
     * @param model Spring MVC model
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("allStatuses", UnitStatus.values());
        model.addAttribute("allDomains", OperationalDomain.values());
        model.addAttribute("allUnitTypes", MilitaryUnit.UnitType.values());
        model.addAttribute("allReadinessLevels", MilitaryUnit.ReadinessLevel.values());
        model.addAttribute("allCommunicationStatuses", MilitaryUnit.CommunicationStatus.values());
        model.addAttribute("allThreatLevels", MilitaryUnit.ThreatLevel.values());
    }
}
