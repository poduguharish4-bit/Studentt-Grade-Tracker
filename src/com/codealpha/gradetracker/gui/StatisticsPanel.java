package com.codealpha.gradetracker.gui;

import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeService;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dashboard panel displaying class metrics and graphical charts.
 */
public class StatisticsPanel extends JPanel {
    private final GradeService gradeService;

    // UI Stats Labels
    private JLabel lblClassAverage;
    private JLabel lblPassRate;
    private JLabel lblTopStudent;
    private JLabel lblLowestStudent;
    
    private GradeDistributionChart distChart;
    private SubjectAveragesChart subjectChart;

    // Theme Colors
    private static final Color BG_DARK = new Color(20, 20, 24);
    private static final Color CARD_BG = new Color(32, 32, 38);
    private static final Color FG_LIGHT = new Color(240, 240, 245);
    private static final Color FG_MUTED = new Color(150, 150, 160);
    private static final Color ACCENT_BLUE = new Color(75, 110, 175);
    private static final Color ACCENT_GREEN = new Color(46, 184, 114);

    public StatisticsPanel(GradeService gradeService) {
        this.gradeService = gradeService;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Summary Cards Layout
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(BG_DARK);

        summaryPanel.add(createCard("Class Average", lblClassAverage = new JLabel("0.0%"), ACCENT_BLUE));
        summaryPanel.add(createCard("Pass Rate", lblPassRate = new JLabel("0.0%"), ACCENT_GREEN));
        summaryPanel.add(createCard("Top Student", lblTopStudent = new JLabel("N/A"), new Color(155, 89, 182)));
        summaryPanel.add(createCard("Lowest Student", lblLowestStudent = new JLabel("N/A"), new Color(231, 76, 60)));

        add(summaryPanel, BorderLayout.NORTH);

        // Charts Panel Layout
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsContainer.setBackground(BG_DARK);

        distChart = new GradeDistributionChart();
        subjectChart = new SubjectAveragesChart();

        chartsContainer.add(distChart);
        chartsContainer.add(subjectChart);

        add(chartsContainer, BorderLayout.CENTER);
    }

    /**
     * Refreshes stats panels and charts with updated service data.
     */
    public void refreshStatistics() {
        java.util.List<Student> students = gradeService.getAllStudents();
        if (students.isEmpty()) {
            lblClassAverage.setText("0.0%");
            lblPassRate.setText("0.0%");
            lblTopStudent.setText("N/A");
            lblLowestStudent.setText("N/A");
        } else {
            lblClassAverage.setText(String.format("%.1f%%", gradeService.getClassAverage()));
            lblPassRate.setText(String.format("%.1f%%", gradeService.getPassRate()));
            
            Student top = gradeService.getTopStudent();
            lblTopStudent.setText(top != null ? truncate(top.getName(), 16) + " (" + String.format("%.0f", top.getAverageGrade()) + ")" : "N/A");
            
            Student bottom = gradeService.getLowestStudent();
            lblLowestStudent.setText(bottom != null ? truncate(bottom.getName(), 16) + " (" + String.format("%.0f", bottom.getAverageGrade()) + ")" : "N/A");
        }

        distChart.repaint();
        subjectChart.repaint();
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Draw color bar at top
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 6, 16, 16);
                g2.fillRect(0, 3, getWidth(), 3); // Make flat bottom on accent bar
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setBackground(BG_DARK);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(FG_MUTED);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(FG_LIGHT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Custom component to paint the Grade Distribution Bar Chart.
     */
    private class GradeDistributionChart extends JPanel {
        public GradeDistributionChart() {
            setBackground(CARD_BG);
            setPreferredSize(new Dimension(300, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Card Background
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            // Title
            g2.setColor(FG_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Grade Distribution", 20, 30);

            Map<String, Integer> dist = gradeService.getGradeDistribution();
            int maxCount = 1; // Avoid divide by zero
            for (int count : dist.values()) {
                if (count > maxCount) maxCount = count;
            }

            int chartX = 40;
            int chartY = 60;
            int chartW = getWidth() - 70;
            int chartH = getHeight() - 110;

            // Draw grid lines
            g2.setColor(new Color(60, 60, 70));
            g2.setStroke(new BasicStroke(1.0f));
            for (int i = 0; i <= 4; i++) {
                int y = chartY + chartH - (i * chartH / 4);
                g2.drawLine(chartX, y, chartX + chartW, y);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(FG_MUTED);
                g2.drawString(String.valueOf(Math.round((double) maxCount * i / 4)), chartX - 25, y + 4);
                g2.setColor(new Color(60, 60, 70));
            }

            // Draw Bars
            int barCount = dist.size();
            int spaceBetween = 15;
            int totalSpacing = spaceBetween * (barCount - 1);
            int barWidth = (chartW - totalSpacing) / barCount;

            Color[] colors = {
                new Color(46, 204, 113), // A+ (Emerald)
                new Color(52, 152, 219), // A (Blue)
                new Color(155, 89, 182), // B (Amethyst)
                new Color(241, 196, 15), // C (Yellow)
                new Color(230, 126, 34), // D (Orange)
                new Color(231, 76, 60)   // F (Red)
            };

            int i = 0;
            for (Map.Entry<String, Integer> entry : dist.entrySet()) {
                String letter = entry.getKey();
                int count = entry.getValue();

                int barHeight = (int) (((double) count / maxCount) * chartH);
                int x = chartX + i * (barWidth + spaceBetween);
                int y = chartY + chartH - barHeight;

                // Draw Bar
                g2.setColor(colors[i % colors.length]);
                g2.fillRoundRect(x, y, barWidth, Math.max(barHeight, 4), 6, 6);

                // Draw label at bottom
                g2.setColor(FG_LIGHT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(letter, x + (barWidth / 2) - 6, chartY + chartH + 20);

                // Draw count above bar if count > 0
                if (count > 0) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(FG_LIGHT);
                    g2.drawString(String.valueOf(count), x + (barWidth / 2) - 5, y - 6);
                }

                i++;
            }
        }
    }

    /**
     * Custom component to paint Subject Averages Bar Chart.
     */
    private class SubjectAveragesChart extends JPanel {
        public SubjectAveragesChart() {
            setBackground(CARD_BG);
            setPreferredSize(new Dimension(300, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Card Background
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            // Title
            g2.setColor(FG_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Subject Averages (%)", 20, 30);

            int chartX = 50;
            int chartY = 60;
            int chartW = getWidth() - 80;
            int chartH = getHeight() - 110;

            // Draw grid lines (0%, 25%, 50%, 75%, 100%)
            g2.setColor(new Color(60, 60, 70));
            for (int i = 0; i <= 4; i++) {
                int y = chartY + chartH - (i * chartH / 4);
                g2.drawLine(chartX, y, chartX + chartW, y);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(FG_MUTED);
                g2.drawString((i * 25) + "%", chartX - 35, y + 4);
                g2.setColor(new Color(60, 60, 70));
            }

            // Draw Bars
            int barCount = Student.DEFAULT_SUBJECTS.length;
            int spaceBetween = 20;
            int totalSpacing = spaceBetween * (barCount - 1);
            int barWidth = (chartW - totalSpacing) / barCount;

            Color barColor = ACCENT_BLUE;

            for (int i = 0; i < barCount; i++) {
                String subject = Student.DEFAULT_SUBJECTS[i];
                Map<String, Double> stats = gradeService.getSubjectStatistics(subject);
                double avg = stats.get("Average");

                int barHeight = (int) ((avg / 100.0) * chartH);
                int x = chartX + i * (barWidth + spaceBetween);
                int y = chartY + chartH - barHeight;

                // Draw Bar
                g2.setColor(barColor);
                g2.fillRoundRect(x, y, barWidth, Math.max(barHeight, 4), 6, 6);

                // Draw label at bottom (Abbr/First Letter or 3 letters)
                g2.setColor(FG_LIGHT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                String label = subject.substring(0, Math.min(subject.length(), 4));
                g2.drawString(label, x + (barWidth / 2) - 10, chartY + chartH + 20);

                // Draw score above bar
                if (avg > 0) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(FG_LIGHT);
                    g2.drawString(String.format("%.0f", avg), x + (barWidth / 2) - 7, y - 6);
                }
            }
        }
    }
}
