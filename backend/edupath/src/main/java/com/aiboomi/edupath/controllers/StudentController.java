package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.daos.AcademicRecordRepository;
import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.dtos.StudentDTO;
import com.aiboomi.edupath.entities.AcademicRecord;
import com.aiboomi.edupath.entities.ExtracurricularActivity;
import com.aiboomi.edupath.entities.Student;
import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.dtos.CareerScoreDTO;
import com.aiboomi.edupath.entities.CareerScore;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Operations to manage students, and to fetch academic records and extracurricular activities")
public class StudentController {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRepository;
    private final ActivityRepository activityRepository;
    private final CareerScoreRepository careerScoreRepository;

    public StudentController(StudentRepository studentRepository, AcademicRecordRepository academicRepository, ActivityRepository activityRepository, CareerScoreRepository careerScoreRepository) {
        this.studentRepository = studentRepository;
        this.academicRepository = academicRepository;
        this.activityRepository = activityRepository;
        this.careerScoreRepository = careerScoreRepository;
    }

    @Operation(summary = "List students", description = "Returns all students",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of students",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.aiboomi.edupath.dtos.StudentDTO.class)),
                                    examples = @ExampleObject(value = "[{\"id\":1,\"studentExternalId\":\"S123\",\"name\":\"Amit Kumar\",\"classNumber\":9,\"dob\":\"2010-05-12\"}]")
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<StudentDTO>> listStudents() {
        List<StudentDTO> all = studentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Get a student", description = "Returns detailed information for a student by internal id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Student details",
                            content = @Content(mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.aiboomi.edupath.dtos.StudentDTO.class),
                                    examples = @ExampleObject(value = "{\"id\":1,\"studentExternalId\":\"S123\",\"name\":\"Amit Kumar\",\"classNumber\":9,\"dob\":\"2010-05-12\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable("id") Long id) {
        Optional<Student> s = studentRepository.findById(id);
        return s.map(student -> ResponseEntity.ok(toDto(student))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "List academic records", description = "Returns academic records for a student. Returns 404 if student not found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Academic records",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.aiboomi.edupath.entities.AcademicRecord.class)),
                                    examples = @ExampleObject(value = "[{\"id\":10,\"year\":2023,\"subject\":\"Mathematics\",\"marks\":78.0,\"maxMarks\":100.0}]")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    @GetMapping("/{id}/academics")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AcademicRecord>> getAcademics(@PathVariable("id") Long id) {
        if (!studentRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<AcademicRecord> list = academicRepository.findByStudent_Id(id);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "List extracurricular activities", description = "Returns extracurricular activities for a student. Returns 404 if student not found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Extracurricular activities",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.aiboomi.edupath.entities.ExtracurricularActivity.class)),
                                    examples = @ExampleObject(value = "[{\"id\":5,\"year\":2023,\"category\":\"SPORTS\",\"name\":\"Inter-school Football\",\"metricValue\":5.0,\"metricUnit\":\"goals\",\"level\":\"REGIONAL\",\"remarks\":\"Captain\"}]")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    @GetMapping("/{id}/activities")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ExtracurricularActivity>> getActivities(@PathVariable("id") Long id) {
        if (!studentRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<ExtracurricularActivity> list = activityRepository.findByStudent_Id(id);
        return ResponseEntity.ok(list);
    }

    private StudentDTO toDto(Student s) {
        if (s == null) return null;
        return new StudentDTO(s.getId(), s.getStudentExternalId(), s.getName(), s.getClassNumber(), s.getDob());
    }

    @Operation(summary = "List computed career scores", description = "Returns computed career scores (score + confidence) for a student. Returns 404 if student not found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Career scores",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.aiboomi.edupath.dtos.CareerScoreDTO.class)),
                                    examples = @ExampleObject(value = "[{\"studentId\":1,\"career\":\"ENGINEERING\",\"score\":82.4,\"confidence\":\"HIGH\"}]")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Student not found")
            }
    )
    @GetMapping("/{id}/careers")
    @Transactional(readOnly = true)
    public ResponseEntity<java.util.List<CareerScoreDTO>> getCareers(@PathVariable("id") Long id) {
        if (!studentRepository.existsById(id)) return ResponseEntity.notFound().build();
        java.util.List<CareerScore> list = careerScoreRepository.findByStudent_Id(id);
        java.util.List<CareerScoreDTO> dto = list.stream().map(cs -> new CareerScoreDTO(cs.getStudent().getId(), cs.getCareer(), cs.getScore(), cs.getConfidence())).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }
}

