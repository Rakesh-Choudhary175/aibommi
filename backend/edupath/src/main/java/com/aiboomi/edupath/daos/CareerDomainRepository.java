package com.aiboomi.edupath.daos;

import com.aiboomi.edupath.entities.CareerDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerDomainRepository extends JpaRepository<CareerDomain, Integer> {
}
