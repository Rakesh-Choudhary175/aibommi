package com.aiboomi.edupath.services;

import com.aiboomi.edupath.daos.AcademicRecordRepository;
import com.aiboomi.edupath.daos.ActivityRepository;
import com.aiboomi.edupath.daos.StudentRepository;
import com.aiboomi.edupath.entities.AcademicRecord;
import com.aiboomi.edupath.entities.ExtracurricularActivity;
import com.aiboomi.edupath.entities.Student;
import com.aiboomi.edupath.entities.ActivityCategory;
import com.aiboomi.edupath.entities.CompetitionLevel;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class StudentImportService {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRepository;
    private final ActivityRepository activityRepository;

    public StudentImportService(StudentRepository studentRepository, AcademicRecordRepository academicRepository,
            ActivityRepository activityRepository) {
        this.studentRepository = studentRepository;
        this.academicRepository = academicRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public List<AcademicRecord> importFromExcel(MultipartFile file) throws IOException {
        final double FALLBACK_MAX_MARKS = 100.0;

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                return new java.util.ArrayList<>(); // empty
            }

            // Read header row and detect columns
            Row header = rows.next();
            int idIdx = -1, nameIdx = -1, classIdx = -1, yearIdx = -1;
            java.util.Map<Integer, String> subjectCols = new java.util.LinkedHashMap<>();

            int lastCell = header.getLastCellNum();
            for (int c = 0; c < lastCell; c++) {
                String h = getCellString(header.getCell(c));
                if (h == null)
                    continue;
                String hl = h.toLowerCase();
                if (idIdx == -1 && hl.contains("student") && hl.contains("id")) {
                    idIdx = c;
                    continue;
                }
                if (idIdx == -1 && hl.equals("id")) {
                    idIdx = c;
                    continue;
                }
                if (nameIdx == -1 && hl.contains("name")) {
                    nameIdx = c;
                    continue;
                }
                if (classIdx == -1 && hl.contains("class")) {
                    classIdx = c;
                    continue;
                }
                if (yearIdx == -1 && hl.contains("year")) {
                    yearIdx = c;
                    continue;
                }

                // treat remaining columns as subjects (clean header text)
                String subj = h.replaceAll("(?i)\\bmarks?\\b|\\bma\\b", "").trim();
                if (subj.isEmpty())
                    subj = h.trim();
                subj = normalizeSubjectName(subj);
                subjectCols.put(c, subj);
            }

            // Fallbacks
            if (idIdx == -1)
                idIdx = 0;
            if (nameIdx == -1)
                nameIdx = 1;
            if (classIdx == -1 && lastCell >= 2)
                classIdx = lastCell - 2;
            if (yearIdx == -1 && lastCell >= 1)
                yearIdx = lastCell - 1;

            // First pass: parse rows into memory and compute per-(year,subject) maxima
            class ParsedRow {
                String studentId;
                String studentName;
                String className;
                Integer year;
                java.util.Map<Integer, Double> marksByCol = new java.util.HashMap<>();
            }

            List<ParsedRow> parsedRows = new ArrayList<>();
            java.util.Map<String, Double> maxPerYearSubject = new java.util.HashMap<>();

            // Keep original header/subjectCols for activity import decision
            // We will reuse subjectCols mapping for academics

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (isRowEmpty(currentRow))
                    continue;

                ParsedRow pr = new ParsedRow();
                pr.studentId = getCellString(currentRow.getCell(idIdx));
                pr.studentName = getCellString(currentRow.getCell(nameIdx));
                pr.className = getCellString(currentRow.getCell(classIdx));
                pr.year = getCellInteger(currentRow.getCell(yearIdx));

                String yearKey = (pr.year == null) ? "ALL" : String.valueOf(pr.year);

                for (java.util.Map.Entry<Integer, String> ent : subjectCols.entrySet()) {
                    Double marks = getCellDouble(currentRow.getCell(ent.getKey()));
                    if (marks == null)
                        continue;
                    pr.marksByCol.put(ent.getKey(), marks);

                    String subj = ent.getValue();
                    String key = subj + "::" + yearKey;
                    Double prev = maxPerYearSubject.get(key);
                    if (prev == null || marks > prev) {
                        maxPerYearSubject.put(key, marks);
                    }
                }

                // only add if at least one mark exists
                if (!pr.marksByCol.isEmpty())
                    parsedRows.add(pr);
            }

            // Second pass: create AcademicRecord instances using per-(year,subject) maxima
            java.util.List<AcademicRecord> academicRecords = new java.util.ArrayList<>();

            for (ParsedRow pr : parsedRows) {
                // ensure Student exists / updated
                Student student = null;
                if (pr.studentId != null) {
                    student = studentRepository.findByStudentExternalId(pr.studentId).orElse(null);
                }
                if (student == null) {
                    student = new Student(pr.studentId, pr.studentName, tryParseInt(pr.className), null);
                    student = studentRepository.save(student);
                } else {
                    // update name/class if missing
                    boolean changed = false;
                    if ((student.getName() == null || student.getName().isEmpty()) && pr.studentName != null) {
                        student.setName(pr.studentName);
                        changed = true;
                    }
                    Integer classNum = tryParseInt(pr.className);
                    if (student.getClassNumber() == null && classNum != null) {
                        student.setClassNumber(classNum);
                        changed = true;
                    }
                    if (changed)
                        studentRepository.save(student);
                }

                String yearKey = (pr.year == null) ? "ALL" : String.valueOf(pr.year);
                for (java.util.Map.Entry<Integer, Double> m : pr.marksByCol.entrySet()) {
                    String subject = subjectCols.get(m.getKey());
                    String key = subject + "::" + yearKey;
                    Double maxMarks = maxPerYearSubject.getOrDefault(key, FALLBACK_MAX_MARKS);
                    AcademicRecord ar = new AcademicRecord(student, pr.year, subject, m.getValue(), maxMarks);
                    academicRecords.add(ar);
                }
            }

            // save all academic records in batch
            return academicRepository.saveAll(academicRecords);
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        int last = row.getLastCellNum();
        if (last <= 0)
            return true;
        for (int i = 0; i < last; i++) {
            Cell cell = row.getCell(i);
            if (cell == null)
                continue;
            if (cell.getCellType() == CellType.BLANK)
                continue;
            String v = getCellString(cell);
            if (v != null && !v.trim().isEmpty())
                return false;
            if (cell.getCellType() == CellType.NUMERIC)
                return false;
        }
        return true;
    }

    private Integer tryParseInt(String s) {
        if (s == null)
            return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Cell getCellIfExists(Row r, Integer idx) {
        if (r == null || idx == null || idx < 0)
            return null;
        try {
            return r.getCell(idx);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Integer firstIndex(java.util.Map<String, Integer> indices, String... keys) {
        for (String k : keys) {
            Integer v = indices.get(k.toLowerCase());
            if (v != null)
                return v;
        }
        return null;
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.STRING)
            return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) {
            double d = cell.getNumericCellValue();
            long l = (long) d;
            if (d == l)
                return String.valueOf(l);
            return String.valueOf(d);
        }
        return null;
    }

    private Integer getCellInteger(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC)
            return (int) cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double getCellDouble(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC)
            return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Extracurricular import (separate Excel with columns like student_id,
    // student_name, year, activity, metric_value, metric_unit, level, remarks)
    @Transactional
    public List<ExtracurricularActivity> importExtracurricularFromExcel(MultipartFile file) throws IOException {
        List<ExtracurricularActivity> out = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext())
                return out;

            Row header = rows.next();

            // map header names to indices
            java.util.Map<String, Integer> indices = new java.util.HashMap<>();
            int last = header.getLastCellNum();
            for (int i = 0; i < last; i++) {
                String h = getCellString(header.getCell(i));
                if (h == null)
                    continue;
                indices.put(h.toLowerCase().trim(), i);
            }

            // validate required columns
            Integer studentIdIdx = firstIndex(indices, "student_id", "studentid");
            Integer studentNameIdx = firstIndex(indices, "student_name", "name");
            Integer activityIdx = firstIndex(indices, "activity", "activity_name");
            if (studentIdIdx == null && studentNameIdx == null) {
                throw new IllegalArgumentException("Missing required column: student_id or student_name");
            }
            if (activityIdx == null) {
                throw new IllegalArgumentException("Missing required column: activity or activity_name");
            }

            while (rows.hasNext()) {
                Row r = rows.next();
                if (isRowEmpty(r))
                    continue;
                String studentId = getCellString(getCellIfExists(r, studentIdIdx));
                String studentName = getCellString(getCellIfExists(r, studentNameIdx));
                Integer year = getCellInteger(getCellIfExists(r, indices.getOrDefault("year", -1)));
                String activityName = getCellString(getCellIfExists(r, activityIdx));
                Double metricValue = getCellDouble(
                        getCellIfExists(r, indices.getOrDefault("metric_value", indices.getOrDefault("metric", -1))));
                String metricUnit = getCellString(getCellIfExists(r,
                        indices.getOrDefault("metric_unit", indices.getOrDefault("metricunit", -1))));
                String levelStr = getCellString(getCellIfExists(r, indices.getOrDefault("level", -1)));
                String remarks = getCellString(getCellIfExists(r, indices.getOrDefault("remarks", -1)));
                String categoryStr = getCellString(
                        getCellIfExists(r, firstIndex(indices, "category", "activity_type")));

                Student student = null;
                if (studentId != null)
                    student = studentRepository.findByStudentExternalId(studentId).orElse(null);
                if (student == null) {
                    student = new Student(studentId, studentName, null, null);
                    student = studentRepository.save(student);
                }

                ActivityCategory category = ActivityCategory.OTHER;
                if (categoryStr != null) {
                    try {
                        category = ActivityCategory.valueOf(categoryStr.trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                CompetitionLevel level = null;
                if (levelStr != null) {
                    try {
                        level = CompetitionLevel.valueOf(levelStr.trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                ExtracurricularActivity act = new ExtracurricularActivity(student, year, category, activityName,
                        metricValue, metricUnit, level, remarks);
                out.add(act);
            }

            return activityRepository.saveAll(out);
        }
    }

    private String normalizeSubjectName(String input) {
        if (input == null)
            return "";
        String lower = input.toLowerCase().trim();

        // Handle specific cases first to avoid partial match overlaps
        if (lower.contains("physical education") || lower.contains("phy ed") || lower.contains("p.e.")
                || lower.equals("pe")) {
            return "Physical Education";
        }

        if (lower.contains("math"))
            return "Mathematics";
        if (lower.contains("eco"))
            return "Economics";
        if (lower.contains("stat"))
            return "Statistics";
        if (lower.contains("hist"))
            return "History";

        // Physics needs to be checked after Physical Education
        if (lower.contains("phys") || lower.contains("phy"))
            return "Physics";

        if (lower.contains("bio"))
            return "Biology";
        if (lower.contains("chem"))
            return "Chemistry";
        if (lower.contains("eng") || lower.contains("lang"))
            return "English";
        if (lower.contains("comp") || lower.contains("cs"))
            return "Computer Science";
        if (lower.contains("art") || lower.contains("draw"))
            return "Art";

        return input;
    }
}
