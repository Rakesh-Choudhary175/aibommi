package com.aiboomi.edupath.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student", indexes = {@Index(columnList = "student_external_id", name = "idx_student_external_id")})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_external_id", unique = true)
    private String studentExternalId; // original Student ID from files

    @Column(name = "name")
    private String name;

    @Column(name = "class_number")
    private Integer classNumber;

    @Column(name = "dob")
    private LocalDate dob;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcademicRecord> academicRecords = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtracurricularActivity> activities = new ArrayList<>();

    public Student() {
    }

    public Student(String studentExternalId, String name, Integer classNumber, LocalDate dob) {
        this.studentExternalId = studentExternalId;
        this.name = name;
        this.classNumber = classNumber;
        this.dob = dob;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentExternalId() {
        return studentExternalId;
    }

    public void setStudentExternalId(String studentExternalId) {
        this.studentExternalId = studentExternalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public List<AcademicRecord> getAcademicRecords() {
        return academicRecords;
    }

    public void setAcademicRecords(List<AcademicRecord> academicRecords) {
        this.academicRecords = academicRecords;
    }

    public List<ExtracurricularActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ExtracurricularActivity> activities) {
        this.activities = activities;
    }
}