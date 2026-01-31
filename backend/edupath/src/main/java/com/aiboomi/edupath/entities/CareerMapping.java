package com.aiboomi.edupath.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "career_mapping", indexes = {@Index(columnList = "career_id, subject", name = "idx_career_subject")})
public class CareerMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private CareerDomain careerDomain;

    @Column(name = "subject")
    private String subject;

    @Column(name = "weight")
    private Double weight;

    public CareerMapping() {
    }

    public CareerMapping(CareerDomain careerDomain, String subject, Double weight) {
        this.careerDomain = careerDomain;
        this.subject = subject;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerDomain getCareerDomain() {
        return careerDomain;
    }

    public void setCareerDomain(CareerDomain careerDomain) {
        this.careerDomain = careerDomain;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}