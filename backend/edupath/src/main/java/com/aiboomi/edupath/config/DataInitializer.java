package com.aiboomi.edupath.config;

import com.aiboomi.edupath.daos.CareerDomainRepository;
import com.aiboomi.edupath.entities.CareerDomain;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedCareerDomains(CareerDomainRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new CareerDomain(1, "ENGINEERING", "Engineering and Technology"));
                repo.save(new CareerDomain(2, "MEDICINE", "Medical and Health Sciences"));
                repo.save(new CareerDomain(3, "COMMERCE", "Business and Commerce"));
                repo.save(new CareerDomain(4, "DESIGN", "Design and Creative fields"));
                repo.save(new CareerDomain(5, "SPORTS", "Professional sports and physical education"));
                repo.save(new CareerDomain(6, "SINGING", "Music and Performance"));
                repo.save(new CareerDomain(7, "DATA_SCIENCE", "Data Science and AI"));
                repo.save(new CareerDomain(8, "ARCHITECTURE", "Architecture and Planning"));
                repo.save(new CareerDomain(9, "LAW", "Law and Legal Studies"));
                repo.save(new CareerDomain(10, "MEDIA", "Media and Communications"));
            }
        };
    }
}