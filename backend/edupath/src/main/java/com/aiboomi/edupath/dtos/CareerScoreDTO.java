package com.aiboomi.edupath.dtos;

import com.aiboomi.edupath.entities.Career;
import com.aiboomi.edupath.entities.ConfidenceLevel;

public class CareerScoreDTO {
    private Long studentId;
    private Career career;
    private Double score;
    private ConfidenceLevel confidence;

    public CareerScoreDTO() {}

    public CareerScoreDTO(Long studentId, Career career, Double score, ConfidenceLevel confidence) {
        this.studentId = studentId;
        this.career = career;
        this.score = score;
        this.confidence = confidence;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public ConfidenceLevel getConfidence() {
        return confidence;
    }

    public void setConfidence(ConfidenceLevel confidence) {
        this.confidence = confidence;
    }
}