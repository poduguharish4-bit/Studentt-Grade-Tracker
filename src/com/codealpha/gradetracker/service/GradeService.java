package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.Student;
import java.util.*;

/**
 * Handles operations related to managing students and calculating statistics.
 */
public class GradeService {
    private List<Student> students;

    public GradeService() {
        this.students = new ArrayList<>();
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    public void setStudents(List<Student> newStudents) {
        this.students = new ArrayList<>(newStudents);
    }

    /**
     * Adds a student to the tracker.
     * @return true if added, false if ID already exists.
     */
    public boolean addStudent(Student student) {
        if (findStudentById(student.getId()) != null) {
            return false;
        }
        students.add(student);
        return true;
    }

    /**
     * Updates an existing student's name and grades.
     * @return true if updated, false if student not found.
     */
    public boolean updateStudent(String id, String newName, Map<String, Double> newGrades) {
        Student student = findStudentById(id);
        if (student == null) {
            return false;
        }
        student.setName(newName);
        student.setGrades(newGrades);
        return true;
    }

    /**
     * Deletes a student by ID.
     * @return true if deleted, false if not found.
     */
    public boolean deleteStudent(String id) {
        Student student = findStudentById(id);
        if (student != null) {
            students.remove(student);
            return true;
        }
        return false;
    }

    /**
     * Finds a student by ID.
     */
    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Search students by name or ID (partial match).
     */
    public List<Student> searchStudents(String query) {
        List<Student> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(students);
        }
        String q = query.toLowerCase().trim();
        for (Student s : students) {
            if (s.getId().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Calculates the overall average grade of all students.
     */
    public double getClassAverage() {
        if (students.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Student s : students) {
            sum += s.getAverageGrade();
        }
        return sum / students.size();
    }

    /**
     * Gets the student with the highest average score.
     */
    public Student getTopStudent() {
        if (students.isEmpty()) {
            return null;
        }
        Student top = students.get(0);
        for (Student s : students) {
            if (s.getAverageGrade() > top.getAverageGrade()) {
                top = s;
            }
        }
        return top;
    }

    /**
     * Gets the student with the lowest average score.
     */
    public Student getLowestStudent() {
        if (students.isEmpty()) {
            return null;
        }
        Student lowest = students.get(0);
        for (Student s : students) {
            if (s.getAverageGrade() < lowest.getAverageGrade()) {
                lowest = s;
            }
        }
        return lowest;
    }

    /**
     * Returns the distribution of letter grades (e.g., {"A+": 2, "A": 5, ...}).
     */
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A+", 0);
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);

        for (Student s : students) {
            String letter = s.getLetterGrade();
            distribution.put(letter, distribution.getOrDefault(letter, 0) + 1);
        }
        return distribution;
    }

    /**
     * Calculates statistics (average, highest, lowest) for a specific subject.
     * Returns a map with keys: "Average", "Highest", "Lowest"
     */
    public Map<String, Double> getSubjectStatistics(String subject) {
        Map<String, Double> stats = new HashMap<>();
        if (students.isEmpty()) {
            stats.put("Average", 0.0);
            stats.put("Highest", 0.0);
            stats.put("Lowest", 0.0);
            return stats;
        }

        double sum = 0.0;
        double highest = -1.0;
        double lowest = 101.0;

        for (Student s : students) {
            double grade = s.getGrade(subject);
            sum += grade;
            if (grade > highest) {
                highest = grade;
            }
            if (grade < lowest) {
                lowest = grade;
            }
        }

        stats.put("Average", sum / students.size());
        stats.put("Highest", highest);
        stats.put("Lowest", lowest);
        return stats;
    }

    /**
     * Calculates the overall pass rate (percentage of students with average >= 50.0).
     */
    public double getPassRate() {
        if (students.isEmpty()) {
            return 0.0;
        }
        int passCount = 0;
        for (Student s : students) {
            if (s.isPassed()) {
                passCount++;
            }
        }
        return (double) passCount / students.size() * 100.0;
    }
}
