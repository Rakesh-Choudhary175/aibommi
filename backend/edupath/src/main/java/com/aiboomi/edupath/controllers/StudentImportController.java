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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Imports", description = "Endpoints to import students, academics and extracurriculars from Excel files")
public class StudentImportController {

    private final StudentImportService importService;

    public StudentImportController(StudentImportService importService) {
        this.importService = importService;
    }

    @Operation(summary = "Import academics from Excel", description = "Accepts a multipart file containing academic records (row-per-student or subject-columns). Computes per-(year,subject) max marks and persists normalized AcademicRecord rows.")
    @RequestBody(description = "Excel file to upload (multipart/form-data)", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Import successful",
                    content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Imported 123 academic records"))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request (file missing or invalid)") ,
            @ApiResponse(responseCode = "500", description = "Server error")
    })
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

    @Operation(summary = "Import extracurricular activities from Excel", description = "Accepts a multipart file containing extracurricular activities and persists them. Validates required columns and returns detailed errors for invalid input.")
    @RequestBody(description = "Excel file to upload (multipart/form-data)", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Import successful",
                    content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Imported 12 extracurricular records"))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request (file missing or invalid)") ,
            @ApiResponse(responseCode = "500", description = "Server error")
    })
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
