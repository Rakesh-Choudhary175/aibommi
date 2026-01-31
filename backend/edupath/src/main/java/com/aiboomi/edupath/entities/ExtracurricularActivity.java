package com.aiboomi.edupath.entities;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "activity")
@Schema(description = "Extracurricular activity record for a student")
public class ExtracurricularActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Internal id of the activity record", example = "5")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Student student;

    @Column(name = "year")
    @Schema(description = "Year when the activity was performed", example = "2023")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @Schema(description = "Activity category", example = "SPORTS")
    private ActivityCategory category;

    @Column(name = "name")
    @Schema(description = "Activity name", example = "Inter-school Football")
    private String name;

    @Column(name = "metric_value")
    @Schema(description = "Numeric metric (e.g., goals, score)", example = "5.0")
    private Double metricValue;

    @Column(name = "metric_unit")
    @Schema(description = "Metric unit", example = "goals")
    private String metricUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    @Schema(description = "Competition level", example = "REGIONAL")
    private CompetitionLevel level;

    @Column(name = "remarks")
    @Schema(description = "Optional remarks", example = "Captain")
    private String remarks;

    public ExtracurricularActivity() {
    }

    public ExtracurricularActivity(Student student, Integer year, ActivityCategory category, String name, Double metricValue, String metricUnit, CompetitionLevel level, String remarks) {
        this.student = student;
        this.year = year;
        this.category = category;
        this.name = name;
        this.metricValue = metricValue;
        this.metricUnit = metricUnit;
        this.level = level;
        this.remarks = remarks;
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

    public ActivityCategory getCategory() {
        return category;
    }

    public void setCategory(ActivityCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    public CompetitionLevel getLevel() {
        return level;
    }

    public void setLevel(CompetitionLevel level) {
        this.level = level;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}