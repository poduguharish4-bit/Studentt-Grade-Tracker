package com.codealpha.gradetracker.gui;

import com.codealpha.gradetracker.model.Student;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dialog to add or edit student records.
 */
public class StudentDialog extends JDialog {
    private final JTextField txtId;
    private final JTextField txtName;
    private final Map<String, JTextField> txtGrades;
    private boolean saved = false;
    private final boolean isEditMode;

    private static final Color BG_DARK = new Color(30, 30, 35);
    private static final Color FG_LIGHT = new Color(240, 240, 245);
    private static final Color ACCENT_COLOR = new Color(75, 110, 175);
    private static final Color FIELD_BG = new Color(45, 45, 50);

    public StudentDialog(Frame owner, String title, Student existingStudent) {
        super(owner, title, true);
        this.isEditMode = (existingStudent != null);
        this.txtGrades = new HashMap<>();

        // Dialog configuration
        setSize(400, 450);
        setLocationRelativeTo(owner);
        setResizable(false);

        // Layout panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // ID Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createStyledLabel("Student ID:"), gbc);
        
        gbc.gridx = 1;
        txtId = createStyledTextField();
        if (isEditMode) {
            txtId.setText(existingStudent.getId());
            txtId.setEnabled(false); // ID is immutable during edit
        }
        formPanel.add(txtId, gbc);

        // Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createStyledLabel("Student Name:"), gbc);
        
        gbc.gridx = 1;
        txtName = createStyledTextField();
        if (isEditMode) {
            txtName.setText(existingStudent.getName());
        }
        formPanel.add(txtName, gbc);

        // Grade Fields dynamically based on subjects
        int gridY = 2;
        for (String subject : Student.DEFAULT_SUBJECTS) {
            gbc.gridx = 0;
            gbc.gridy = gridY;
            formPanel.add(createStyledLabel(subject + ":"), gbc);

            gbc.gridx = 1;
            JTextField txtGrade = createStyledTextField();
            txtGrade.setText(isEditMode ? String.valueOf((int)existingStudent.getGrade(subject)) : "0");
            formPanel.add(txtGrade, gbc);
            txtGrades.put(subject, txtGrade);
            gridY++;
        }

        // Scrollable content if necessary, though we have fixed dimensions
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(BG_DARK);

        JButton btnSave = new JButton(isEditMode ? "Update" : "Add Student");
        styleButton(btnSave, ACCENT_COLOR, Color.WHITE);
        btnSave.addActionListener(e -> onSave());

        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel, new Color(70, 70, 75), FG_LIGHT);
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FG_LIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setBackground(FIELD_BG);
        field.setForeground(FG_LIGHT);
        field.setCaretColor(FG_LIGHT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65), 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        return field;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void onSave() {
        // Validate ID
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Name
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Grades
        for (Map.Entry<String, JTextField> entry : txtGrades.entrySet()) {
            String subject = entry.getKey();
            String scoreStr = entry.getValue().getText().trim();
            try {
                double score = Double.parseDouble(scoreStr);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, subject + " grade must be between 0 and 100.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for " + subject + ".", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Student getStudent() {
        if (!saved) return null;
        Student s = new Student(txtId.getText().trim(), txtName.getText().trim());
        for (Map.Entry<String, JTextField> entry : txtGrades.entrySet()) {
            s.setGrade(entry.getKey(), Double.parseDouble(entry.getValue().getText().trim()));
        }
        return s;
    }
}
