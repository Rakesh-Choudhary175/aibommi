package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.daos.AcademicRecordRepository;
import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.dtos.StudentDTO;
import com.aiboomi.edupath.entities.AcademicRecord;
import com.aiboomi.edupath.entities.ExtracurricularActivity;
import com.aiboomi.edupath.entities.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRepository;
    private final ActivityRepository activityRepository;

    public StudentController(StudentRepository studentRepository, AcademicRecordRepository academicRepository, ActivityRepository activityRepository) {
        this.studentRepository = studentRepository;
        this.academicRepository = academicRepository;
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> listStudents() {
        List<StudentDTO> all = studentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable("id") Long id) {
        Optional<Student> s = studentRepository.findById(id);
        return s.map(student -> ResponseEntity.ok(toDto(student))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/academics")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AcademicRecord>> getAcademics(@PathVariable("id") Long id) {
        if (!studentRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<AcademicRecord> list = academicRepository.findByStudent_Id(id);
        return ResponseEntity.ok(list);
    }

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
}
