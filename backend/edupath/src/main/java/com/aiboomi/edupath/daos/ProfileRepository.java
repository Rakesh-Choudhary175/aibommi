package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findFirstByStudent_IdOrderByCreatedAtDesc(Long studentId);
}