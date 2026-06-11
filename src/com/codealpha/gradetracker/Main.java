package com.codealpha.gradetracker;

import com.codealpha.gradetracker.cli.CommandLineInterface;
import com.codealpha.gradetracker.gui.MainFrame;
import com.codealpha.gradetracker.service.GradeService;
import javax.swing.*;

/**
 * Entry point of the Student Grade Tracker application.
 */
public class Main {
    public static void main(String[] args) {
        GradeService gradeService = new GradeService();

        // Check if user requested CLI mode
        boolean useCli = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--cli")) {
                useCli = true;
                break;
            }
        }

        if (useCli) {
            CommandLineInterface cli = new CommandLineInterface(gradeService);
            cli.start();
        } else {
            // Setup GUI System Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fail silently, fallback to standard Look and Feel
            }

            // Launch the GUI on the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(gradeService);
                frame.setVisible(true);
            });
        }
    }
}
