package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.ExtracurricularActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ExtracurricularActivity, Long> {
    java.util.List<ExtracurricularActivity> findByStudent_Id(Long studentId);
    java.util.List<ExtracurricularActivity> findByStudent_IdAndYear(Long studentId, Integer year);
}
