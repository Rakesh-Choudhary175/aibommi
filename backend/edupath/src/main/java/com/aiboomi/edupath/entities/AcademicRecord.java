package com.aiboomi.edupath.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "academic_record", indexes = {@Index(columnList = "student_id, year, subject", name = "idx_academic_student_year_subject")})
public class AcademicRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "year")
    private Integer year;

    @Column(name = "subject")
    private String subject;

    @Column(name = "marks")
    private Double marks;

    @Column(name = "max_marks")
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