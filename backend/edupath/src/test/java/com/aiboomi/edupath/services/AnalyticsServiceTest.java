package com.aiboomi.edupath.services;

import com.aiboomi.edupath.daos.AcademicRecordRepository;
import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {

    StudentRepository studentRepository;
    AcademicRecordRepository academicRepository;
    ActivityRepository activityRepository;
    CareerScoreRepository careerScoreRepository;
    AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        academicRepository = mock(AcademicRecordRepository.class);
        activityRepository = mock(ActivityRepository.class);
        careerScoreRepository = mock(CareerScoreRepository.class);
        analyticsService = new AnalyticsService(studentRepository, academicRepository, activityRepository, careerScoreRepository);
    }

    @Test
    void computeSubjectMetrics_singleSubjectMultipleYears() {
        Student s = new Student("S1","Test",9,null);
        s.setId(1L);

        AcademicRecord r1 = new AcademicRecord(s, 2021, "Mathematics", 80.0, 100.0);
        AcademicRecord r2 = new AcademicRecord(s, 2022, "Mathematics", 85.0, 100.0);
        AcademicRecord r3 = new AcademicRecord(s, 2023, "Mathematics", 90.0, 100.0);

        when(academicRepository.findByStudent_Id(1L)).thenReturn(List.of(r1, r2, r3));

        var metrics = analyticsService.computeSubjectMetrics(s);
        assertThat(metrics).containsKey("Mathematics");
        double strength = metrics.get("Mathematics").subjectStrength;
        assertThat(strength).isBetween(81.0, 82.0);
    }

    @Test
    void computeCareerScores_engineeringScore() {
        Student s = new Student("S2","EngTest",10,null);
        s.setId(2L);

        // Single-year entries so trend=50, consistency=100
        AcademicRecord m = new AcademicRecord(s, 2023, "Mathematics", 85.0, 100.0);
        AcademicRecord p = new AcademicRecord(s, 2023, "Physics", 78.0, 100.0);
        AcademicRecord cs = new AcademicRecord(s, 2023, "Computer Science", 93.0, 100.0);

        when(studentRepository.findAll()).thenReturn(List.of(s));
        when(academicRepository.findByStudent_Id(2L)).thenReturn(List.of(m, p, cs));
        when(activityRepository.findByStudent_Id(2L)).thenReturn(List.of());

        when(careerScoreRepository.save(any(CareerScore.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<CareerScore> results = analyticsService.computeCareerScores();
        assertThat(results).hasSize(5);

        CareerScore eng = results.stream().filter(x -> x.getCareer() == Career.ENGINEERING).findFirst().orElse(null);
        assertThat(eng).isNotNull();
        // Expected approx 77.25
        assertThat(eng.getScore()).isBetween(76.5, 78.5);
        assertThat(eng.getConfidence()).isEqualTo(ConfidenceLevel.MEDIUM);
    }
}