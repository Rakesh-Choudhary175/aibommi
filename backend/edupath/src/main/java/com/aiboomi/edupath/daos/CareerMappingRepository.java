package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.CareerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerMappingRepository extends JpaRepository<CareerMapping, Long> {
}