package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.entities.Career;
import com.aiboomi.edupath.entities.CareerScore;
import com.aiboomi.edupath.entities.ConfidenceLevel;
import com.aiboomi.edupath.entities.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentControllerCareersTest {

    @Test
    void getCareers_returnsScores() {
        StudentRepository studentRepository = mock(StudentRepository.class);
        CareerScoreRepository careerScoreRepository = mock(CareerScoreRepository.class);

        StudentController controller = new StudentController(studentRepository, null, null, careerScoreRepository);

        Student s = new Student();
        s.setId(5L);
        when(studentRepository.existsById(5L)).thenReturn(true);
        CareerScore cs = new CareerScore(s, Career.ENGINEERING, 82.4, ConfidenceLevel.HIGH);
        when(careerScoreRepository.findByStudent_Id(5L)).thenReturn(List.of(cs));

        var resp = controller.getCareers(5L);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body).hasSize(1);
        assertThat(body.get(0).getCareer()).isEqualTo(Career.ENGINEERING);
        assertThat(body.get(0).getScore()).isEqualTo(82.4);
        assertThat(body.get(0).getConfidence()).isEqualTo(ConfidenceLevel.HIGH);
    }
}
