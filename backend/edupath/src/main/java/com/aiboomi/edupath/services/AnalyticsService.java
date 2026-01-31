package com.aiboomi.edupath.services;

import com.aiboomi.edupath.daos.AcademicRecordRepository;
import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.CareerScoreRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.dtos.CareerScoreDTO;
import com.aiboomi.edupath.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRepository;
    private final ActivityRepository activityRepository;
    private final CareerScoreRepository careerScoreRepository;

    public AnalyticsService(StudentRepository studentRepository, AcademicRecordRepository academicRepository,
            ActivityRepository activityRepository, CareerScoreRepository careerScoreRepository) {
        this.studentRepository = studentRepository;
        this.academicRepository = academicRepository;
        this.activityRepository = activityRepository;
        this.careerScoreRepository = careerScoreRepository;
    }

    // Subject metrics per subject
    public static class SubjectMetrics {
        public double latestPct; // 0-100
        public double trendScore; // 0-100
        public double consistencyScore; // 0-100
        public double subjectStrength; // 0-100
    }

    // Compute subject metrics for a student
    public Map<String, SubjectMetrics> computeSubjectMetrics(Student s) {
        List<AcademicRecord> records = academicRepository.findByStudent_Id(s.getId());
        Map<String, List<AcademicRecord>> bySubject = records.stream()
                .collect(Collectors.groupingBy(AcademicRecord::getSubject));
        Map<String, SubjectMetrics> out = new HashMap<>();

        for (Map.Entry<String, List<AcademicRecord>> e : bySubject.entrySet()) {
            String subject = e.getKey();
            List<AcademicRecord> recs = e.getValue().stream().sorted(Comparator.comparing(AcademicRecord::getYear))
                    .collect(Collectors.toList());
            AcademicRecord latest = recs.get(recs.size() - 1);
            double latestPct = safePct(latest.getMarks(), latest.getMaxMarks());

            List<AcademicRecord> prev = recs.size() > 1 ? recs.subList(0, recs.size() - 1) : Collections.emptyList();
            double prevAvgPct = prev.isEmpty() ? latestPct
                    : prev.stream().mapToDouble(r -> safePct(r.getMarks(), r.getMaxMarks())).average()
                            .orElse(latestPct);

            double trendScore = clamp(50 + (latestPct - prevAvgPct), 0, 100); // baseline 50

            // consistency: std dev of pct across all records
            double[] pcts = recs.stream().mapToDouble(r -> safePct(r.getMarks(), r.getMaxMarks())).toArray();
            double std = stdDev(pcts);
            double consistencyScore = clamp(100 - std, 0, 100);

            double subjectStrength = clamp(0.5 * latestPct + 0.3 * trendScore + 0.2 * consistencyScore, 0, 100);

            SubjectMetrics sm = new SubjectMetrics();
            sm.latestPct = latestPct;
            sm.trendScore = trendScore;
            sm.consistencyScore = consistencyScore;
            sm.subjectStrength = subjectStrength;
            out.put(subject, sm);
        }
        return out;
    }

    // Helper for percent
    private double safePct(Double marks, Double maxMarks) {
        if (marks == null || maxMarks == null || maxMarks <= 0)
            return 0.0;
        return clamp((marks / maxMarks) * 100.0, 0, 100);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double stdDev(double[] arr) {
        if (arr.length == 0)
            return 0.0;
        double mean = Arrays.stream(arr).average().orElse(0.0);
        double var = Arrays.stream(arr).map(d -> (d - mean) * (d - mean)).sum() / arr.length;
        return Math.sqrt(var);
    }

    // Compute subject strengths only (map subject->strength in 0-100)
    public Map<String, Double> computeStrength(Student s) {
        return computeSubjectMetrics(s).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().subjectStrength));
    }

    // Compute career scores for all students and persist them
    @Transactional
    public List<CareerScore> computeCareerScores() {
        List<Student> students = studentRepository.findAll();
        List<CareerScore> saved = new ArrayList<>();
        // clear previous scores (optional: keep history - here we remove existing and
        // replace)
        careerScoreRepository.deleteAllInBatch();

        for (Student s : students) {
            Map<String, Double> strengths = computeStrength(s);
            List<ExtracurricularActivity> activities = activityRepository.findByStudent_Id(s.getId());
            // ENGINEERING
            CareerScore eng = computeEngineering(s, strengths, activities);
            saved.add(careerScoreRepository.save(eng));

            // DESIGN
            CareerScore design = computeDesign(s, strengths, activities);
            saved.add(careerScoreRepository.save(design));

            // MEDICINE
            CareerScore med = computeMedicine(s, strengths, activities);
            saved.add(careerScoreRepository.save(med));

            // SPORTS
            CareerScore sports = computeSports(s, strengths, activities);
            saved.add(careerScoreRepository.save(sports));

            // SINGING
            CareerScore singing = computeSinging(s, strengths, activities);
            saved.add(careerScoreRepository.save(singing));

            // COMMERCE
            CareerScore commerce = computeCommerce(s, strengths);
            saved.add(careerScoreRepository.save(commerce));

            // DATA_SCIENCE
            CareerScore dataScience = computeDataScience(s, strengths);
            saved.add(careerScoreRepository.save(dataScience));

            // ARCHITECTURE
            CareerScore arch = computeArchitecture(s, strengths);
            saved.add(careerScoreRepository.save(arch));

            // LAW
            CareerScore law = computeLaw(s, strengths);
            saved.add(careerScoreRepository.save(law));

            // MEDIA
            CareerScore media = computeMedia(s, strengths);
            saved.add(careerScoreRepository.save(media));
        }

        return saved;
    }

    private CareerScore computeEngineering(Student s, Map<String, Double> strength,
            List<ExtracurricularActivity> activities) {
        // Locked weights
        Map<String, Double> weights = Map.of("Mathematics", 0.5, "Physics", 0.3, "Computer Science", 0.2);
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        double numerator = 0.0;
        int present = 0;
        for (Map.Entry<String, Double> e : weights.entrySet()) {
            Double val = strength.get(e.getKey());
            if (val != null) {
                numerator += val * e.getValue();
                present++;
            }
        }
        Double score = present == 0 ? null : clamp(numerator / totalWeight, 0, 100);

        ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        // If Running or Swimming present -> increase confidence
        boolean hasRunOrSwim = activities.stream().anyMatch(a -> nameContains(a, "run") || nameContains(a, "swim"));
        if (hasRunOrSwim)
            confidence = ConfidenceLevel.HIGH;
        else if (present < weights.size())
            confidence = ConfidenceLevel.LOW;

        return new CareerScore(s, Career.ENGINEERING, score, confidence);
    }

    private CareerScore computeDesign(Student s, Map<String, Double> strength,
            List<ExtracurricularActivity> activities) {
        // Mandatory Art/Drawing and Mathematics
        double art = firstMatchingStrength(strength, List.of("Art", "Drawing"));
        Double math = strength.get("Mathematics");
        Double score = null;
        if (art == -1 && math == null) {
            score = null;
        } else {
            double artVal = art == -1 ? 0.0 : art;
            double matVal = math == null ? 0.0 : math;
            score = clamp(0.6 * artVal + 0.4 * matVal, 0, 100);
        }
        ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        if (art == -1 || math == null)
            confidence = ConfidenceLevel.LOW;
        // Extracurriculars doesn't change numeric score (per rules), may increase
        // confidence if arts activities present
        boolean hasArtActivity = activities.stream()
                .anyMatch(a -> nameContains(a, "art") || nameContains(a, "drawing") || nameContains(a, "design"));
        if (hasArtActivity)
            confidence = ConfidenceLevel.HIGH;
        return new CareerScore(s, Career.DESIGN, score, confidence);
    }

    private CareerScore computeMedicine(Student s, Map<String, Double> strength,
            List<ExtracurricularActivity> activities) {
        Double bio = strength.get("Biology");
        Double chem = strength.get("Chemistry");
        Double phys = strength.get("Physics");
        int present = 0;
        double numerator = 0.0;
        if (bio != null) {
            numerator += bio * 0.4;
            present++;
        }
        if (chem != null) {
            numerator += chem * 0.3;
            present++;
        }
        if (phys != null) {
            numerator += phys * 0.3;
            present++;
        }
        Double score = present == 0 ? null : clamp(numerator / (present == 3 ? 1 : 1), 0, 100); // weights sum to 1,
                                                                                                // missing handled by
                                                                                                // using available
                                                                                                // subset
        ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        if (present < 3)
            confidence = ConfidenceLevel.LOW;
        // Extracurriculars only affect confidence
        boolean anyExtra = activities != null && !activities.isEmpty();
        if (anyExtra)
            confidence = ConfidenceLevel.HIGH;
        return new CareerScore(s, Career.MEDICINE, score, confidence);
    }

    private CareerScore computeSports(Student s, Map<String, Double> strength,
            List<ExtracurricularActivity> activities) {
        // PE academic
        Double pe = firstMatchingStrengthNullable(strength, List.of("Physical Education", "PE"));
        // Extracurricular: Running, Swimming optional. compute their scores
        Double runningScore = computeTimeBasedScore(
                activities.stream().filter(a -> nameContains(a, "run")).findFirst().orElse(null));
        Double swimmingScore = computeTimeBasedScore(
                activities.stream().filter(a -> nameContains(a, "swim")).findFirst().orElse(null));
        Double extracurricularScore = null;
        if (runningScore != null && swimmingScore != null)
            extracurricularScore = Math.max(runningScore, swimmingScore);
        else
            extracurricularScore = runningScore != null ? runningScore : swimmingScore;

        Double finalScore;
        ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        if (extracurricularScore == null) {
            // use only PE
            finalScore = pe == null ? null : clamp(pe, 0, 100);
            confidence = ConfidenceLevel.MEDIUM;
        } else {
            double peVal = pe == null ? 0.0 : pe;
            finalScore = clamp(0.6 * extracurricularScore + 0.4 * peVal, 0, 100);
            confidence = ConfidenceLevel.HIGH;
        }
        return new CareerScore(s, Career.SPORTS, finalScore, confidence);
    }

    private CareerScore computeSinging(Student s, Map<String, Double> strength,
            List<ExtracurricularActivity> activities) {
        Double music = strength.get("Music");
        // find choir singing activity and its level
        ExtracurricularActivity choir = activities.stream()
                .filter(a -> nameContains(a, "choir") || nameContains(a, "sing")).findFirst().orElse(null);
        Double choirScore = null;
        if (choir != null) {
            if (choir.getLevel() == CompetitionLevel.SCHOOL)
                choirScore = 70.0;
            else if (choir.getLevel() == CompetitionLevel.DISTRICT)
                choirScore = 85.0;
            else if (choir.getLevel() == CompetitionLevel.STATE)
                choirScore = 95.0;
        }
        Double finalScore;
        ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        if (choirScore == null) {
            // use only music
            finalScore = music == null ? null : clamp(music, 0, 100);
            confidence = ConfidenceLevel.MEDIUM;
        } else {
            double musicVal = music == null ? 0.0 : music;
            finalScore = clamp(0.7 * choirScore + 0.3 * musicVal, 0, 100);
            confidence = ConfidenceLevel.HIGH;
        }
        return new CareerScore(s, Career.SINGING, finalScore, confidence);
    }

    private CareerScore computeCommerce(Student s, Map<String, Double> strength) {
        Map<String, Double> weights = Map.of("Mathematics", 0.5, "Economics", 0.3, "English", 0.2);
        return computeWeightedCareer(s, Career.COMMERCE, strength, weights);
    }

    private CareerScore computeDataScience(Student s, Map<String, Double> strength) {
        Map<String, Double> weights = Map.of("Mathematics", 0.5, "Computer Science", 0.5);
        return computeWeightedCareer(s, Career.DATA_SCIENCE, strength, weights);
    }

    private CareerScore computeArchitecture(Student s, Map<String, Double> strength) {
        // Architecture: 0.4 Math + 0.4 Art + 0.2 Physics
        // Handle Art separately due to aliasing first
        Double math = strength.get("Mathematics");
        Double phys = strength.get("Physics");
        double art = firstMatchingStrength(strength, List.of("Art", "Drawing"));

        int present = 0;
        double numerator = 0.0;
        if (math != null) {
            numerator += math * 0.4;
            present++;
        }
        if (phys != null) {
            numerator += phys * 0.2;
            present++;
        }
        if (art != -1) {
            numerator += art * 0.4;
            present++;
        }

        Double score = present == 0 ? null : clamp(numerator, 0, 100);
        // Note: weights sum to 1. If missing, score is lower naturally or should we
        // normalize?
        // Existing logic in computeEngineering uses normalized average (numerator /
        // totalWeight).
        // Let's normalize.
        double currentTotalWeight = 0.0;
        if (math != null)
            currentTotalWeight += 0.4;
        if (phys != null)
            currentTotalWeight += 0.2;
        if (art != -1)
            currentTotalWeight += 0.4;

        if (currentTotalWeight > 0) {
            score = clamp(numerator / currentTotalWeight, 0, 100);
        }

        ConfidenceLevel confidence = (present == 3) ? ConfidenceLevel.HIGH
                : ((present >= 2) ? ConfidenceLevel.MEDIUM : ConfidenceLevel.LOW);

        return new CareerScore(s, Career.ARCHITECTURE, score, confidence);
    }

    private CareerScore computeLaw(Student s, Map<String, Double> strength) {
        Map<String, Double> weights = Map.of("English", 0.4, "History", 0.4, "Mathematics", 0.2);
        return computeWeightedCareer(s, Career.LAW, strength, weights);
    }

    private CareerScore computeMedia(Student s, Map<String, Double> strength) {
        // Media: 0.5 English + 0.3 Art/Drawing + 0.2 Music
        Double eng = strength.get("English");
        Double music = strength.get("Music");
        double art = firstMatchingStrength(strength, List.of("Art", "Drawing"));

        double numerator = 0.0;
        double currentTotalWeight = 0.0;
        int present = 0;

        if (eng != null) {
            numerator += eng * 0.5;
            currentTotalWeight += 0.5;
            present++;
        }
        if (music != null) {
            numerator += music * 0.2;
            currentTotalWeight += 0.2;
            present++;
        }
        if (art != -1) {
            numerator += art * 0.3;
            currentTotalWeight += 0.3;
            present++;
        }

        Double score = null;
        if (currentTotalWeight > 0) {
            score = clamp(numerator / currentTotalWeight, 0, 100);
        }

        ConfidenceLevel confidence = (present == 3) ? ConfidenceLevel.HIGH
                : ((present >= 1) ? ConfidenceLevel.MEDIUM : ConfidenceLevel.LOW);
        return new CareerScore(s, Career.MEDIA, score, confidence);
    }

    private CareerScore computeWeightedCareer(Student s, Career c, Map<String, Double> strength,
            Map<String, Double> weights) {
        double numerator = 0.0;
        double totalWeight = 0.0;
        int present = 0;

        for (Map.Entry<String, Double> e : weights.entrySet()) {
            Double val = strength.get(e.getKey());
            if (val != null) {
                numerator += val * e.getValue();
                totalWeight += e.getValue();
                present++;
            }
        }

        Double score = (totalWeight == 0) ? null : clamp(numerator / totalWeight, 0, 100);
        ConfidenceLevel confidence = (present == weights.size()) ? ConfidenceLevel.HIGH
                : ((present > 0) ? ConfidenceLevel.MEDIUM : ConfidenceLevel.LOW);
        return new CareerScore(s, c, score, confidence);
    }

    // helper to check name contains
    private boolean nameContains(ExtracurricularActivity a, String token) {
        if (a == null || a.getName() == null)
            return false;
        return a.getName().toLowerCase().contains(token.toLowerCase());
    }

    // compute time-based score for running/swimming
    private Double computeTimeBasedScore(ExtracurricularActivity a) {
        if (a == null || a.getMetricValue() == null)
            return null;
        double time = a.getMetricValue();
        double base;
        if (time <= 25)
            base = 90;
        else if (time <= 27)
            base = 80;
        else if (time <= 30)
            base = 65;
        else
            base = 50;
        int bonus = 0;
        if (a.getLevel() == CompetitionLevel.DISTRICT)
            bonus = 10;
        else if (a.getLevel() == CompetitionLevel.STATE)
            bonus = 20;
        return clamp(base + bonus, 0, 100);
    }

    // helper: find first matching subject among candidates and return -1 if none
    private double firstMatchingStrength(Map<String, Double> strengths, List<String> candidates) {
        for (String c : candidates) {
            Double v = strengths.get(c);
            if (v != null)
                return v;
        }
        return -1;
    }

    private Double firstMatchingStrengthNullable(Map<String, Double> strengths, List<String> candidates) {
        for (String c : candidates) {
            Double v = strengths.get(c);
            if (v != null)
                return v;
        }
        return null;
    }

    // Public helper to run analysis and return DTOs ready for API response
    @Transactional
    public List<CareerScoreDTO> analyzeAllStudentsAndReturnDTOs() {
        List<CareerScore> all = computeCareerScores();
        return all.stream().map(
                cs -> new CareerScoreDTO(cs.getStudent().getId(), cs.getCareer(), cs.getScore(), cs.getConfidence()))
                .collect(Collectors.toList());
    }
}