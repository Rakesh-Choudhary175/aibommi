package com.aiboomi.edupath.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "academic_record", indexes = {@Index(columnList = "student_id, year, subject", name = "idx_academic_student_year_subject")})
@Schema(description = "An academic record representing a student's marks in a subject for a given year")
public class AcademicRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Internal id of the academic record")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @Column(name = "year")
    @Schema(description = "Academic year (e.g., 2021)", example = "2023")
    private Integer year;

    @Column(name = "subject")
    @Schema(description = "Subject name (e.g., Mathematics)", example = "Mathematics")
    private String subject;

    @Column(name = "marks")
    @Schema(description = "Marks obtained by student", example = "78.0")
    private Double marks;

    @Column(name = "max_marks")
    @Schema(description = "Maximum marks for this subject in that year (used to calculate percentiles)", example = "100.0")
    private Double maxMarks;

    public AcademicRecord() {
    }

    public AcademicRecord(Student student, Integer year, String subject, Double marks, Double maxMarks) {
        this.student = student;
        this.year = year;
        this.subject = subject;
        this.marks = marks;
        this.maxMarks = maxMarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }
}