package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.dtos.CareerScoreDTO;
import com.aiboomi.edupath.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Analytics", description = "Analyze students and compute career scores")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "Analyze students and compute career scores", description = "Runs analysis for all students, computes subject metrics and career scores and persists the results. Returns all computed scores.")
    @PostMapping("/analyze")
    public ResponseEntity<List<CareerScoreDTO>> analyzeAll() {
        List<CareerScoreDTO> results = analyticsService.analyzeAllStudentsAndReturnDTOs();
        return ResponseEntity.ok(results);
    }
}