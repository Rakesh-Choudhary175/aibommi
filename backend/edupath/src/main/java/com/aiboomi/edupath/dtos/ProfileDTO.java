package com.aiboomi.edupath.dtos;

import java.time.OffsetDateTime;

public class ProfileDTO {
    private Long id;
    private Long studentId;
    private Object profile; // parsed JSON object
    private String narrative;
    private OffsetDateTime createdAt;

    public ProfileDTO() {}

    public ProfileDTO(Long id, Long studentId, Object profile, String narrative, OffsetDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.profile = profile;
        this.narrative = narrative;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Object getProfile() {
        return profile;
    }

    public void setProfile(Object profile) {
        this.profile = profile;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
