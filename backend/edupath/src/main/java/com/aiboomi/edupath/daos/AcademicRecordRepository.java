package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
    // find all academic records for a given student id
    java.util.List<AcademicRecord> findByStudent_Id(Long studentId);

    // optional: find by student and year
    java.util.List<AcademicRecord> findByStudent_IdAndYear(Long studentId, Integer year);
}
