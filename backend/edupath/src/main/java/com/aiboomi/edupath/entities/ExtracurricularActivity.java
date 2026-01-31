package com.aiboomi.edupath.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "activity")
public class ExtracurricularActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ActivityCategory category;

    @Column(name = "name")
    private String name;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "metric_unit")
    private String metricUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private CompetitionLevel level;

    @Column(name = "remarks")
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