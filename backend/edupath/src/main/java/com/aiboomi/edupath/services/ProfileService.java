package com.aiboomi.edupath.services;

import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.daos.ProfileRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final StudentRepository studentRepository;
    private final CareerScoreRepository careerScoreRepository;
    private final ActivityRepository activityRepository;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    public ProfileService(StudentRepository studentRepository, CareerScoreRepository careerScoreRepository,
            ActivityRepository activityRepository, ProfileRepository profileRepository, ObjectMapper objectMapper) {
        this.studentRepository = studentRepository;
        this.careerScoreRepository = careerScoreRepository;
        this.activityRepository = activityRepository;
        this.profileRepository = profileRepository;
        this.objectMapper = objectMapper;
    }

    // Mapping of canonical subjects per career
    private static final Map<Career, List<String>> CAREER_SUBJECTS = Map.of(
            Career.ENGINEERING, List.of("Mathematics", "Physics", "Computer Science"),
            Career.DESIGN, List.of("Art / Drawing", "Mathematics"),
            Career.MEDICINE, List.of("Biology", "Chemistry", "Physics"),
            Career.SPORTS, List.of("Physical Education"),
            Career.SINGING, List.of("Music"),
            Career.COMMERCE, List.of("Mathematics", "Economics", "English"),
            Career.DATA_SCIENCE, List.of("Mathematics", "Computer Science", "Statistics"),
            Career.ARCHITECTURE, List.of("Mathematics", "Art / Drawing", "Physics"),
            Career.LAW, List.of("English", "History", "Mathematics"),
            Career.MEDIA, List.of("English", "Art / Drawing", "Music"));

    @Transactional
    public Profile generateProfileForStudent(Long studentId) throws Exception {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        List<CareerScore> scores = careerScoreRepository.findByStudent_Id(studentId);
        Map<Career, CareerScore> scoreMap = scores.stream().collect(Collectors.toMap(CareerScore::getCareer, cs -> cs));

        Map<String, Object> output = new LinkedHashMap<>();

        for (Career c : Career.values()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            CareerScore cs = scoreMap.get(c);
            entry.put("score", cs == null ? null : cs.getScore());
            entry.put("confidence", cs == null ? null : cs.getConfidence());

            // Build basedOn list
            List<String> basedOn = new ArrayList<>();
            // Add canonical subjects
            List<String> canonical = CAREER_SUBJECTS.getOrDefault(c, Collections.emptyList());
            basedOn.addAll(canonical);

            // For Sports and Singing, add extracurriculars if present
            if (c == Career.SPORTS) {
                // check for running/swimming activities
                boolean hasRunning = activityRepository.findByStudent_Id(studentId).stream()
                        .anyMatch(a -> a.getName() != null && a.getName().toLowerCase().contains("run"));
                boolean hasSwimming = activityRepository.findByStudent_Id(studentId).stream()
                        .anyMatch(a -> a.getName() != null && a.getName().toLowerCase().contains("swim"));
                if (hasRunning)
                    basedOn.add("Running");
                if (hasSwimming)
                    basedOn.add("Swimming");
            }
            if (c == Career.SINGING) {
                boolean hasChoir = activityRepository.findByStudent_Id(studentId).stream()
                        .anyMatch(a -> a.getName() != null && a.getName().toLowerCase().contains("choir"));
                if (hasChoir)
                    basedOn.add("Choir Singing");
            }

            entry.put("basedOn", basedOn);
            output.put(c.name().substring(0, 1).toUpperCase() + c.name().substring(1).toLowerCase(), entry);
        }

        // pick top 2 careers by score
        List<CareerScore> sorted = scores
                .stream().sorted(Comparator
                        .comparingDouble((CareerScore cs) -> cs.getScore() == null ? -1 : cs.getScore()).reversed())
                .collect(Collectors.toList());
        String narrative = buildNarrative(sorted);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);

        Profile profile = new Profile(s, json, narrative);
        profile.setCreatedAt(OffsetDateTime.now());
        profileRepository.save(profile);
        return profile;
    }

    private String buildNarrative(List<CareerScore> sorted) {
        if (sorted.isEmpty())
            return "No career scores available.";
        CareerScore primary = sorted.get(0);
        CareerScore secondary = sorted.size() > 1 ? sorted.get(1) : null;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Primary: %s (score=%.1f, confidence=%s).", primary.getCareer().name(),
                primary.getScore() == null ? 0.0 : primary.getScore(),
                primary.getConfidence() == null ? "UNKNOWN" : primary.getConfidence().name()));
        if (secondary != null) {
            sb.append(" ");
            sb.append(String.format("Secondary: %s (score=%.1f, confidence=%s).", secondary.getCareer().name(),
                    secondary.getScore() == null ? 0.0 : secondary.getScore(),
                    secondary.getConfidence() == null ? "UNKNOWN" : secondary.getConfidence().name()));
        }
        // Others - one line max
        List<String> others = sorted.stream().skip(2).map(cs -> cs.getCareer().name()).collect(Collectors.toList());
        if (!others.isEmpty()) {
            sb.append(" Others: ");
            sb.append(String.join(", ", others));
            sb.append(" rank lower based on subject strengths or missing extracurricular evidence.");
        }
        return sb.toString();
    }

    public Optional<Profile> getLatestProfileForStudent(Long studentId) {
        return profileRepository.findFirstByStudent_IdOrderByCreatedAtDesc(studentId);
    }

    public com.aiboomi.edupath.dtos.ProfileDTO toDto(Profile p) {
        Object parsed = null;
        try {
            parsed = objectMapper.readValue(p.getProfileJson(), Object.class);
        } catch (Exception e) {
            parsed = p.getProfileJson();
        }
        return new com.aiboomi.edupath.dtos.ProfileDTO(p.getId(), p.getStudent().getId(), parsed, p.getNarrative(),
                p.getCreatedAt());
    }

    @Transactional
    public List<Profile> generateProfilesForAll() throws Exception {
        List<Student> students = studentRepository.findAll();
        List<Profile> saved = new ArrayList<>();
        for (Student s : students) {
            saved.add(generateProfileForStudent(s.getId()));
        }
        return saved;
    }
}