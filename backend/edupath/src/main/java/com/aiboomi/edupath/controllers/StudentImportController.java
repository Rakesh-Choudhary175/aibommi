package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.entities.AcademicRecord;
import com.aiboomi.edupath.entities.ExtracurricularActivity;
import com.aiboomi.edupath.services.StudentImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentImportController {

    private final StudentImportService importService;

    public StudentImportController(StudentImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/upload/academics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAcademicExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("file is missing or empty");
        }

        try {
            List<AcademicRecord> saved = importService.importFromExcel(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Imported " + saved.size() + " academic records");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload/extracurriculars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExtracurricularExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("file is missing or empty");
        }

        try {
            List<ExtracurricularActivity> saved = importService.importExtracurricularFromExcel(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Imported " + saved.size() + " extracurricular records");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import: " + e.getMessage());
        }
    }
}
