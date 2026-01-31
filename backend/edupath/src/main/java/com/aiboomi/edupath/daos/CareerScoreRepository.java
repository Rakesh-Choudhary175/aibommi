package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.CareerScore;
import com.aiboomi.edupath.entities.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerScoreRepository extends JpaRepository<CareerScore, Long> {
    List<CareerScore> findByStudent_Id(Long studentId);
    List<CareerScore> findByCareer(Career career);
}