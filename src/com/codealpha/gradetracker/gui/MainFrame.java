package com.codealpha.gradetracker.gui;

import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeService;
import com.codealpha.gradetracker.util.DatabaseHandler;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Main application window for the Student Grade Tracker GUI.
 */
public class MainFrame extends JFrame {
    private final GradeService gradeService;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    
    // Components
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblStatus;
    
    private StatisticsPanel statsPanel;
    private JButton btnNavDashboard;
    private JButton btnNavStudents;

    // Theme Colors
    private static final Color BG_DARK = new Color(20, 20, 24);
    private static final Color SIDEBAR_BG = new Color(30, 30, 36);
    private static final Color CARD_BG = new Color(32, 32, 38);
    private static final Color FG_LIGHT = new Color(240, 240, 245);
    private static final Color FG_MUTED = new Color(150, 150, 160);
    private static final Color ACCENT_BLUE = new Color(75, 110, 175);
    private static final Color ACCENT_RED = new Color(231, 76, 60);

    public MainFrame(GradeService gradeService) {
        this.gradeService = gradeService;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        // Frame config
        setTitle("CodeAlpha - Student Grade Tracker");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        // Main Layout
        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(BG_DARK);

        // Add Sidebar
        mainLayout.add(createSidebar(), BorderLayout.WEST);

        // Add Content Panels
        statsPanel = new StatisticsPanel(gradeService);
        contentPanel.add(statsPanel, "Dashboard");
        contentPanel.add(createStudentListPanel(), "StudentList");
        mainLayout.add(contentPanel, BorderLayout.CENTER);

        // Add Status Bar
        mainLayout.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainLayout);

        // Load data on startup
        loadData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Logo Title
        JLabel lblLogo = new JLabel("CODEALPHA");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);

        JLabel lblSub = new JLabel("Grade Management System");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(FG_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblSub);

        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navigation Buttons
        btnNavDashboard = createNavButton("Dashboard", true);
        btnNavStudents = createNavButton("Student Directory", false);

        btnNavDashboard.addActionListener(e -> showPanel("Dashboard"));
        btnNavStudents.addActionListener(e -> showPanel("StudentList"));

        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnNavStudents);

        sidebar.add(Box.createVerticalGlue());

        // Footer in Sidebar
        JLabel lblVersion = new JLabel("Version 1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(FG_MUTED);
        lblVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblVersion);

        return sidebar;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        if (active) {
            btn.setBackground(ACCENT_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(FG_MUTED);
        }
        return btn;
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        if (name.equals("Dashboard")) {
            btnNavDashboard.setBackground(ACCENT_BLUE);
            btnNavDashboard.setForeground(Color.WHITE);
            btnNavStudents.setBackground(SIDEBAR_BG);
            btnNavStudents.setForeground(FG_MUTED);
            statsPanel.refreshStatistics();
        } else {
            btnNavStudents.setBackground(ACCENT_BLUE);
            btnNavStudents.setForeground(Color.WHITE);
            btnNavDashboard.setBackground(SIDEBAR_BG);
            btnNavDashboard.setForeground(FG_MUTED);
            refreshTable(gradeService.getAllStudents());
        }
    }

    private JPanel createStudentListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(15, 15));
        listPanel.setBackground(BG_DARK);
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Action controls (Add, Edit, Delete, Search)
        JPanel actionPanel = new JPanel(new BorderLayout(10, 0));
        actionPanel.setBackground(BG_DARK);

        // Buttons Left
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonGroup.setBackground(BG_DARK);

        JButton btnAdd = createActionButton("Add Student", ACCENT_BLUE, Color.WHITE);
        btnAdd.addActionListener(e -> showAddStudentDialog());
        
        JButton btnEdit = createActionButton("Edit Grades", new Color(52, 152, 219), Color.WHITE);
        btnEdit.addActionListener(e -> showEditStudentDialog());

        JButton btnDelete = createActionButton("Delete Student", ACCENT_RED, Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        buttonGroup.add(btnAdd);
        buttonGroup.add(Box.createRigidArea(new Dimension(8, 0)));
        buttonGroup.add(btnEdit);
        buttonGroup.add(Box.createRigidArea(new Dimension(8, 0)));
        buttonGroup.add(btnDelete);

        actionPanel.add(buttonGroup, BorderLayout.WEST);

        // Search bar Right
        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchGroup.setBackground(BG_DARK);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(FG_LIGHT);

        txtSearch = new JTextField(15);
        txtSearch.setBackground(CARD_BG);
        txtSearch.setForeground(FG_LIGHT);
        txtSearch.setCaretColor(FG_LIGHT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        
        // Dynamic search listener
        txtSearch.addCaretListener(e -> {
            List<Student> filtered = gradeService.searchStudents(txtSearch.getText());
            refreshTable(filtered);
        });

        searchGroup.add(lblSearch);
        searchGroup.add(txtSearch);
        actionPanel.add(searchGroup, BorderLayout.EAST);

        listPanel.add(actionPanel, BorderLayout.NORTH);

        // Setup Table
        String[] columnHeaders = new String[5 + Student.DEFAULT_SUBJECTS.length];
        columnHeaders[0] = "ID";
        columnHeaders[1] = "Student Name";
        for (int i = 0; i < Student.DEFAULT_SUBJECTS.length; i++) {
            columnHeaders[2 + i] = Student.DEFAULT_SUBJECTS[i];
        }
        columnHeaders[2 + Student.DEFAULT_SUBJECTS.length] = "Average";
        columnHeaders[3 + Student.DEFAULT_SUBJECTS.length] = "Grade";
        columnHeaders[4 + Student.DEFAULT_SUBJECTS.length] = "GPA";

        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblStudents = new JTable(tableModel);
        styleTable(tblStudents);

        JScrollPane scrollPane = new JScrollPane(tblStudents);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 55), 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(CARD_BG);
        table.setForeground(FG_LIGHT);
        table.setGridColor(new Color(50, 50, 60));
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(45, 45, 55));
        header.setForeground(FG_LIGHT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));

        // Alignment and Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                c.setBackground(isSelected ? ACCENT_BLUE : CARD_BG);
                c.setForeground(FG_LIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                c.setBackground(isSelected ? ACCENT_BLUE : CARD_BG);
                c.setForeground(FG_LIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SIDEBAR_BG);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 48)),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)
        ));

        lblStatus = new JLabel("Status: Initialized");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(FG_MUTED);

        JLabel lblBranding = new JLabel("CodeAlpha Internship Task 1");
        lblBranding.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBranding.setForeground(FG_MUTED);

        statusPanel.add(lblStatus, BorderLayout.WEST);
        statusPanel.add(lblBranding, BorderLayout.EAST);

        return statusPanel;
    }

    private void loadData() {
        List<Student> loaded = DatabaseHandler.loadStudents();
        gradeService.setStudents(loaded);
        refreshTable(loaded);
        statsPanel.refreshStatistics();
        lblStatus.setText("Database status: Loaded " + loaded.size() + " student record(s) successfully.");
    }

    private void refreshTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            Object[] row = new Object[5 + Student.DEFAULT_SUBJECTS.length];
            row[0] = s.getId();
            row[1] = s.getName();
            
            for (int i = 0; i < Student.DEFAULT_SUBJECTS.length; i++) {
                row[2 + i] = String.format("%.0f", s.getGrade(Student.DEFAULT_SUBJECTS[i]));
            }
            
            row[2 + Student.DEFAULT_SUBJECTS.length] = String.format("%.2f", s.getAverageGrade());
            row[3 + Student.DEFAULT_SUBJECTS.length] = s.getLetterGrade();
            row[4 + Student.DEFAULT_SUBJECTS.length] = String.format("%.1f", s.getGPA());
            tableModel.addRow(row);
        }
    }

    private void showAddStudentDialog() {
        StudentDialog dialog = new StudentDialog(this, "Add New Student", null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Student s = dialog.getStudent();
            if (gradeService.addStudent(s)) {
                DatabaseHandler.saveStudents(gradeService.getAllStudents());
                loadData();
                lblStatus.setText("Status: Added student " + s.getName() + " (" + s.getId() + ")");
            } else {
                JOptionPane.showMessageDialog(this, "A student with ID " + s.getId() + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditStudentDialog() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the list to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblStudents.getValueAt(selectedRow, 0);
        Student student = gradeService.findStudentById(id);
        if (student == null) return;

        StudentDialog dialog = new StudentDialog(this, "Edit Student Grades", student);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Student s = dialog.getStudent();
            if (gradeService.updateStudent(id, s.getName(), s.getGrades())) {
                DatabaseHandler.saveStudents(gradeService.getAllStudents());
                loadData();
                lblStatus.setText("Status: Updated student grades for " + s.getName() + " (" + s.getId() + ")");
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the list to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblStudents.getValueAt(selectedRow, 0);
        Student student = gradeService.findStudentById(id);
        if (student == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the student record for " + student.getName() + " (" + student.getId() + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (gradeService.deleteStudent(id)) {
                DatabaseHandler.saveStudents(gradeService.getAllStudents());
                loadData();
                lblStatus.setText("Status: Deleted student record " + student.getName() + " (" + student.getId() + ")");
            }
        }
    }
}
