package com.aiboomi.edupath.services;

import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.daos.ProfileRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    StudentRepository studentRepository;
    CareerScoreRepository careerScoreRepository;
    ActivityRepository activityRepository;
    ProfileRepository profileRepository;
    ProfileService profileService;

    @BeforeEach
    void setup() {
        studentRepository = mock(StudentRepository.class);
        careerScoreRepository = mock(CareerScoreRepository.class);
        activityRepository = mock(ActivityRepository.class);
        profileRepository = mock(ProfileRepository.class);
        profileService = new ProfileService(studentRepository, careerScoreRepository, activityRepository, profileRepository, new ObjectMapper());
    }

    @Test
    void generateProfile_createsJsonAndNarrative() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setName("Test Student");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        CareerScore csEng = new CareerScore(s, Career.ENGINEERING, 85.0, ConfidenceLevel.HIGH);
        CareerScore csDesign = new CareerScore(s, Career.DESIGN, 70.0, ConfidenceLevel.MEDIUM);
        when(careerScoreRepository.findByStudent_Id(1L)).thenReturn(List.of(csEng, csDesign));
        when(activityRepository.findByStudent_Id(1L)).thenReturn(List.of());
        when(profileRepository.save(Mockito.any(Profile.class))).thenAnswer(i -> i.getArgument(0));

        Profile p = profileService.generateProfileForStudent(1L);
        assertThat(p).isNotNull();
        assertThat(p.getStudent().getId()).isEqualTo(1L);
        assertThat(p.getProfileJson()).contains("Engineering");
        assertThat(p.getProfileJson()).contains("Design");
        assertThat(p.getNarrative()).contains("Primary: ENGINEERING");
    }
}
