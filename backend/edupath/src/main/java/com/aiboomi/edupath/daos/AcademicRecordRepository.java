package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
}
