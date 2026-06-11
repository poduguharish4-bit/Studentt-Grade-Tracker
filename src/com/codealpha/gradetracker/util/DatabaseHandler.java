package com.codealpha.gradetracker.util;

import com.codealpha.gradetracker.model.Student;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Handles CSV file operations for saving and loading student records.
 */
public class DatabaseHandler {
    private static final String DEFAULT_FILE_PATH = "data/students.csv";

    /**
     * Saves the list of students to the default CSV file.
     */
    public static void saveStudents(List<Student> students) {
        saveStudents(students, DEFAULT_FILE_PATH);
    }

    /**
     * Saves the list of students to the specified CSV file.
     */
    public static void saveStudents(List<Student> students, String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            // Write CSV Header
            StringBuilder header = new StringBuilder("ID,Name");
            for (String subject : Student.DEFAULT_SUBJECTS) {
                header.append(",").append(subject);
            }
            writer.write(header.toString());
            writer.newLine();

            // Write Student Data
            for (Student s : students) {
                StringBuilder row = new StringBuilder();
                row.append(escapeCsvField(s.getId())).append(",");
                row.append(escapeCsvField(s.getName()));
                
                for (String subject : Student.DEFAULT_SUBJECTS) {
                    row.append(",").append(s.getGrade(subject));
                }
                writer.write(row.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving student data: " + e.getMessage());
        }
    }

    /**
     * Loads the list of students from the default CSV file.
     */
    public static List<Student> loadStudents() {
        return loadStudents(DEFAULT_FILE_PATH);
    }

    /**
     * Loads the list of students from the specified CSV file.
     */
    public static List<Student> loadStudents(String filePath) {
        List<Student> students = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return students; // Empty file
            }

            // Parse header to map subjects to indexes
            String[] headers = parseCsvRow(headerLine);
            Map<String, Integer> subjectIndexes = new HashMap<>();
            for (int i = 2; i < headers.length; i++) {
                subjectIndexes.put(headers[i], i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCsvRow(line);
                if (fields.length < 2) continue;

                String id = fields[0];
                String name = fields[1];
                Student student = new Student(id, name);

                // Set grades for subjects based on headers
                for (String subject : Student.DEFAULT_SUBJECTS) {
                    Integer idx = subjectIndexes.get(subject);
                    if (idx != null && idx < fields.length) {
                        try {
                            double grade = Double.parseDouble(fields[idx]);
                            student.setGrade(subject, grade);
                        } catch (NumberFormatException e) {
                            student.setGrade(subject, 0.0);
                        }
                    }
                }
                students.add(student);
            }
        } catch (IOException e) {
            System.err.println("Error loading student data: " + e.getMessage());
        }
        return students;
    }

    /**
     * Escapes a CSV field value (wrapping in quotes if it contains commas or quotes).
     */
    private static String escapeCsvField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Simple robust CSV parser that respects double-quoted values.
     */
    private static String[] parseCsvRow(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote: "" -> "
                    field.append('"');
                    i++;
                } else {
                    // Toggle quote mode
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
}
