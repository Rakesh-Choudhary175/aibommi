package com.aiboomi.edupath.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Lob
    @Column(name = "profile_json", columnDefinition = "LONGTEXT")
    private String profileJson;

    @Lob
    @Column(name = "narrative", columnDefinition = "LONGTEXT")
    private String narrative;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Profile() {}

    public Profile(Student student, String profileJson, String narrative) {
        this.student = student;
        this.profileJson = profileJson;
        this.narrative = narrative;
        this.createdAt = OffsetDateTime.now();
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
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

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}