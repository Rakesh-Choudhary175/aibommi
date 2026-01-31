package com.aiboomi.edupath.dtos;

import java.time.LocalDate;

public class StudentDTO {
    private Long id;
    private String studentExternalId;
    private String name;
    private Integer classNumber;
    private LocalDate dob;

    public StudentDTO() {}

    public StudentDTO(Long id, String studentExternalId, String name, Integer classNumber, LocalDate dob) {
        this.id = id;
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
}