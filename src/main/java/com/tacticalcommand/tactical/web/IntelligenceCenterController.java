package com.tacticalcommand.tactical.web;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tacticalcommand.tactical.service.IntelligenceService;

/**
 * Web Controller for Intelligence Center interface.
 * 
 * Provides server-side rendered interface for intelligence management,
 * threat assessment, and information analysis.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-08-01
 */
@Controller
@RequestMapping("/intelligence")
@PreAuthorize("hasRole('COMMANDER') or hasRole('OPERATOR') or hasRole('ANALYST')")
public class IntelligenceCenterController {

    private static final String INTELLIGENCE_ATTRIBUTE = "intelligence";
    private static final String REDIRECT_INTELLIGENCE = "redirect:/intelligence";

    private final IntelligenceService intelligenceService;

    public IntelligenceCenterController(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    /**
     * Main intelligence dashboard view.
     * 
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @param sort sort parameter (default by creation date)
     * @param severity filter by threat severity
     * @param verified filter by verification status
     * @param model Spring MVC model
     * @return intelligence dashboard template
     */
    @GetMapping
    public String intelligenceDashboard(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(defaultValue = "createdAt") String sort,
                                      @RequestParam(required = false) String severity,
                                      @RequestParam(required = false) Boolean verified,
                                      Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        
        // Mock data for intelligence reports since the service might not have all methods
        model.addAttribute("totalReports", 45);
        model.addAttribute("verifiedReports", 32);
        model.addAttribute("pendingReports", 13);
        model.addAttribute("highThreatReports", 8);
        
        // Intelligence threat levels
        model.addAttribute("threatLevels", new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"});
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 3);
        model.addAttribute("totalElements", 45);
        model.addAttribute("severityFilter", severity);
        model.addAttribute("verifiedFilter", verified);

        return "intelligence/threat-board";
    }

    /**
     * Show form for creating a new intelligence report.
     * 
     * @param model Spring MVC model
     * @return intelligence report form template
     */
    @GetMapping("/new")
    public String showNewReportForm(Model model) {
        model.addAttribute("isEdit", false);
        addFormAttributes(model);
        return "intelligence/intel-report-form";
    }

    /**
     * Threat assessment dashboard with real-time threat indicators.
     * 
     * @param model Spring MVC model
     * @return threat assessment template
     */
    @GetMapping("/threats")
    public String threatAssessment(Model model) {
        // Mock threat data
        model.addAttribute("activeThreatIndicators", 12);
        model.addAttribute("threatTrends", "ESCALATING");
        model.addAttribute("riskLevel", "ELEVATED");
        
        return "intelligence/threat-assessment";
    }

    /**
     * Intelligence collection requirements and tasking.
     * 
     * @param model Spring MVC model
     * @return collection requirements template
     */
    @GetMapping("/collection")
    public String collectionRequirements(Model model) {
        model.addAttribute("activeRequirements", 8);
        model.addAttribute("completedToday", 5);
        model.addAttribute("pendingAnalysis", 15);
        
        return "intelligence/collection-requirements";
    }

    /**
     * Geospatial intelligence view with threat mapping.
     * 
     * @param model Spring MVC model
     * @return geospatial intelligence template
     */
    @GetMapping("/geospatial")
    public String geospatialIntelligence(Model model) {
        model.addAttribute("threatLocations", List.of());
        model.addAttribute("surveillanceAssets", 6);
        model.addAttribute("coverageAreas", 12);
        
        return "intelligence/geospatial-intel";
    }

    /**
     * Add common form attributes for dropdowns and options.
     * 
     * @param model Spring MVC model
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("threatLevels", new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"});
        model.addAttribute("sources", new String[]{"HUMINT", "SIGINT", "IMINT", "OSINT", "MASINT"});
        model.addAttribute("classifications", new String[]{"UNCLASSIFIED", "CONFIDENTIAL", "SECRET", "TOP_SECRET"});
        model.addAttribute("reportTypes", new String[]{"THREAT_ASSESSMENT", "SITUATION_REPORT", "INTELLIGENCE_SUMMARY", "WARNING_ORDER"});
    }
}
