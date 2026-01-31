package com.aiboomi.edupath.dtos;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Student data transfer object")
public class StudentDTO {
    @Schema(description = "Internal database id", example = "1")
    private Long id;
    @Schema(description = "External student id from source data / school", example = "S12345")
    private String studentExternalId;
    @Schema(description = "Full name of the student", example = "Amit Kumar")
    private String name;
    @Schema(description = "Numeric class (6-10)", example = "9")
    private Integer classNumber;
    @Schema(description = "Date of birth of the student", example = "2010-05-12")
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