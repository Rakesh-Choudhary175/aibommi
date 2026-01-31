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
            }
        };
    }
}