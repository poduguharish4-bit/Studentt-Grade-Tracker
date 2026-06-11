package com.codealpha.gradetracker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a student with a unique ID, name, and grades for various subjects.
 */
public class Student {
    private String id;
    private String name;
    private Map<String, Double> grades;

    public static final String[] DEFAULT_SUBJECTS = {
        "Mathematics", "Science", "English", "History", "Computer Science"
    };

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.grades = new HashMap<>();
        // Initialize with default subjects with a grade of 0.0
        for (String subject : DEFAULT_SUBJECTS) {
            this.grades.put(subject, 0.0);
        }
    }

    public Student(String id, String name, Map<String, Double> grades) {
        this.id = id;
        this.name = name;
        this.grades = new HashMap<>(grades);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, Double> grades) {
        this.grades = new HashMap<>(grades);
    }

    public void setGrade(String subject, double grade) {
        if (grade >= 0.0 && grade <= 100.0) {
            this.grades.put(subject, grade);
        } else {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
    }

    public double getGrade(String subject) {
        return this.grades.getOrDefault(subject, 0.0);
    }

    /**
     * Calculates the average grade across all graded subjects.
     */
    public double getAverageGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (double val : grades.values()) {
            sum += val;
        }
        return sum / grades.size();
    }

    /**
     * Determines the Letter Grade based on the average score.
     */
    public String getLetterGrade() {
        double avg = getAverageGrade();
        if (avg >= 90.0) return "A+";
        if (avg >= 80.0) return "A";
        if (avg >= 70.0) return "B";
        if (avg >= 60.0) return "C";
        if (avg >= 50.0) return "D";
        return "F";
    }

    /**
     * Maps average grade to a standard 4.0 GPA scale.
     */
    public double getGPA() {
        double avg = getAverageGrade();
        if (avg >= 90.0) return 4.0;
        if (avg >= 80.0) return 3.5;
        if (avg >= 70.0) return 3.0;
        if (avg >= 60.0) return 2.5;
        if (avg >= 50.0) return 2.0;
        return 0.0;
    }

    /**
     * Determines if the student passed (Average >= 50.0).
     */
    public boolean isPassed() {
        return getAverageGrade() >= 50.0;
    }
}
