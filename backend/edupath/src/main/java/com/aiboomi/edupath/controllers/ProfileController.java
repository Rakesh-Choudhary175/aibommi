package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.entities.Profile;
import com.aiboomi.edupath.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Profiles", description = "Profile generation and retrieval endpoints")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Generate profile for a student and persist it")
    @PostMapping("/{id}/profiles")
    public ResponseEntity<com.aiboomi.edupath.dtos.ProfileDTO> generateProfile(@PathVariable Long id) throws Exception {
        com.aiboomi.edupath.entities.Profile p = profileService.generateProfileForStudent(id);
        return ResponseEntity.ok(profileService.toDto(p));
    }

    @Operation(summary = "Generate profiles for all students")
    @PostMapping("/generate-profiles")
    public ResponseEntity<java.util.List<com.aiboomi.edupath.dtos.ProfileDTO>> generateAll() throws Exception {
        java.util.List<com.aiboomi.edupath.entities.Profile> list = profileService.generateProfilesForAll();
        java.util.List<com.aiboomi.edupath.dtos.ProfileDTO> dtoList = new java.util.ArrayList<>();
        for (com.aiboomi.edupath.entities.Profile p : list) dtoList.add(profileService.toDto(p));
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "Get latest profile for a student")
    @GetMapping("/{id}/profiles/latest")
    public ResponseEntity<com.aiboomi.edupath.dtos.ProfileDTO> getLatest(@PathVariable Long id) {
        return profileService.getLatestProfileForStudent(id)
                .map(profileService::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}