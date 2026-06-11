package com.codealpha.gradetracker.cli;

import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeService;
import com.codealpha.gradetracker.util.DatabaseHandler;
import java.util.*;

/**
 * Console-based user interface for managing student grades.
 */
public class CommandLineInterface {
    private final GradeService gradeService;
    private final Scanner scanner;

    public CommandLineInterface(GradeService gradeService) {
        this.gradeService = gradeService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the CLI execution loop.
     */
    public void start() {
        System.out.println("==================================================");
        System.out.println("      CODEALPHA STUDENT GRADE TRACKER - CLI       ");
        System.out.println("==================================================");
        
        // Load data from DB
        List<Student> loaded = DatabaseHandler.loadStudents();
        gradeService.setStudents(loaded);
        System.out.println("Loaded " + loaded.size() + " student record(s) from database.");

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readIntInput("Enter choice (1-7): ", 1, 7);
            System.out.println();
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> editStudent();
                case 4 -> deleteStudent();
                case 5 -> searchStudent();
                case 6 -> viewStatisticsSummary();
                case 7 -> {
                    DatabaseHandler.saveStudents(gradeService.getAllStudents());
                    System.out.println("Student records saved. Thank you for using Student Grade Tracker!");
                    exit = true;
                }
            }
            if (!exit) {
                System.out.println("\nPress Enter to return to menu...");
                scanner.nextLine();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add New Student");
        System.out.println("2. View All Students & Grades");
        System.out.println("3. Edit Student Grades");
        System.out.println("4. Delete Student Record");
        System.out.println("5. Search Student by ID/Name");
        System.out.println("6. View Detailed Class Statistics");
        System.out.println("7. Save Data & Exit");
    }

    private void addStudent() {
        System.out.println("--- ADD NEW STUDENT ---");
        String id = readStringInput("Enter Student ID (unique): ", true);
        
        if (gradeService.findStudentById(id) != null) {
            System.out.println("[Error] Student with ID '" + id + "' already exists!");
            return;
        }

        String name = readStringInput("Enter Student Name: ", false);
        Student student = new Student(id, name);

        System.out.println("Enter grades for subjects (0.0 to 100.0):");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            double grade = readDoubleInput("  " + subject + ": ", 0.0, 100.0);
            student.setGrade(subject, grade);
        }

        if (gradeService.addStudent(student)) {
            DatabaseHandler.saveStudents(gradeService.getAllStudents());
            System.out.println("[Success] Student '" + name + "' added successfully!");
        } else {
            System.out.println("[Error] Failed to add student.");
        }
    }

    private void viewAllStudents() {
        System.out.println("--- STUDENT DIRECTORY ---");
        List<Student> list = gradeService.getAllStudents();
        displayStudentTable(list);
    }

    private void editStudent() {
        System.out.println("--- EDIT STUDENT GRADES ---");
        String id = readStringInput("Enter Student ID to edit: ", true);
        Student student = gradeService.findStudentById(id);

        if (student == null) {
            System.out.println("[Error] Student not found with ID '" + id + "'");
            return;
        }

        System.out.println("Editing Student: " + student.getName() + " (ID: " + student.getId() + ")");
        String newName = readStringInput("Enter New Name (press Enter to keep '" + student.getName() + "'): ", false);
        if (newName.trim().isEmpty()) {
            newName = student.getName();
        }

        Map<String, Double> grades = new HashMap<>(student.getGrades());
        System.out.println("Enter new grades (press Enter to keep existing grade):");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            double current = student.getGrade(subject);
            Double newVal = readOptionalDoubleInput("  " + subject + " (Current: " + current + "): ", 0.0, 100.0);
            if (newVal != null) {
                grades.put(subject, newVal);
            }
        }

        if (gradeService.updateStudent(id, newName, grades)) {
            DatabaseHandler.saveStudents(gradeService.getAllStudents());
            System.out.println("[Success] Student records updated!");
        } else {
            System.out.println("[Error] Failed to update student records.");
        }
    }

    private void deleteStudent() {
        System.out.println("--- DELETE STUDENT RECORD ---");
        String id = readStringInput("Enter Student ID to delete: ", true);
        Student student = gradeService.findStudentById(id);

        if (student == null) {
            System.out.println("[Error] Student not found with ID '" + id + "'");
            return;
        }

        System.out.print("Are you sure you want to delete '" + student.getName() + "'? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            if (gradeService.deleteStudent(id)) {
                DatabaseHandler.saveStudents(gradeService.getAllStudents());
                System.out.println("[Success] Student record deleted successfully.");
            } else {
                System.out.println("[Error] Failed to delete student.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchStudent() {
        System.out.println("--- SEARCH STUDENT ---");
        String query = readStringInput("Enter Search Query (Name or ID partial): ", false);
        List<Student> results = gradeService.searchStudents(query);
        System.out.println("Search Results (" + results.size() + " matches):");
        displayStudentTable(results);
    }

    private void viewStatisticsSummary() {
        System.out.println("--- CLASS STATISTICS REPORT ---");
        List<Student> list = gradeService.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No student data available to compute statistics.");
            return;
        }

        double avg = gradeService.getClassAverage();
        Student top = gradeService.getTopStudent();
        Student lowest = gradeService.getLowestStudent();
        double passRate = gradeService.getPassRate();

        System.out.printf("Total Students:    %d\n", list.size());
        System.out.printf("Class Average:     %.2f%%\n", avg);
        System.out.printf("Overall Pass Rate: %.1f%%\n", passRate);
        if (top != null) {
            System.out.printf("Top Student:       %s (ID: %s, Avg: %.2f%%)\n", top.getName(), top.getId(), top.getAverageGrade());
        }
        if (lowest != null) {
            System.out.printf("Lowest Student:    %s (ID: %s, Avg: %.2f%%)\n", lowest.getName(), lowest.getId(), lowest.getAverageGrade());
        }

        System.out.println("\nSubject-wise Analysis:");
        System.out.printf("%-18s | %-12s | %-12s | %-12s\n", "Subject", "Average", "Highest", "Lowest");
        System.out.println("-------------------|--------------|--------------|--------------");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            Map<String, Double> stats = gradeService.getSubjectStatistics(subject);
            System.out.printf("%-18s | %-11.2f%% | %-11.2f%% | %-11.2f%%\n",
                    subject, stats.get("Average"), stats.get("Highest"), stats.get("Lowest"));
        }

        System.out.println("\nGrade Distribution Graph:");
        Map<String, Integer> dist = gradeService.getGradeDistribution();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            System.out.printf("  %-2s: ", entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
                System.out.print("■");
            }
            System.out.printf(" (%d)\n", entry.getValue());
        }
    }

    private void displayStudentTable(List<Student> list) {
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        // Render header
        System.out.print("+------------+--------------------+");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            System.out.print("------+");
        }
        System.out.println("---------+-------+-------+");
        
        System.out.printf("| %-10s | %-18s |", "ID", "Name");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            // Take first 4 letters of subject
            String subAbbr = subject.length() > 4 ? subject.substring(0, 4) : subject;
            System.out.printf(" %-4s |", subAbbr);
        }
        System.out.printf(" %-7s | %-5s | %-5s |\n", "Average", "Grade", "GPA");

        System.out.print("+------------+--------------------+");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            System.out.print("------+");
        }
        System.out.println("---------+-------+-------+");

        // Render rows
        for (Student s : list) {
            System.out.printf("| %-10s | %-18s |", truncate(s.getId(), 10), truncate(s.getName(), 18));
            for (String subject : Student.DEFAULT_SUBJECTS) {
                System.out.printf(" %-4.0f |", s.getGrade(subject));
            }
            System.out.printf(" %-7.2f | %-5s | %-5.1f |\n", s.getAverageGrade(), s.getLetterGrade(), s.getGPA());
        }

        // Render footer line
        System.out.print("+------------+--------------------+");
        for (String subject : Student.DEFAULT_SUBJECTS) {
            System.out.print("------+");
        }
        System.out.println("---------+-------+-------+");
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 2) + "..";
    }

    // Helper input methods with validation
    private String readStringInput(String prompt, boolean uppercase) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return uppercase ? input.toUpperCase() : input;
            }
            System.out.println("[Error] Input cannot be empty. Please try again.");
        }
    }

    private int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[Error] Input must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid integer.");
            }
        }
    }

    private double readDoubleInput(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[Error] Grade must be between %.1f and %.1f.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid decimal number.");
            }
        }
    }

    private Double readOptionalDoubleInput(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                double value = Double.parseDouble(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[Error] Grade must be between %.1f and %.1f.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid decimal number or press Enter.");
            }
        }
    }
}
