package com.login;

import com.login.util.DatabaseUtil;
import com.login.view.LoginFrame;

import javax.swing.*;

/**
 * Main class to start the login application
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (DatabaseUtil.testConnection()) {
            System.out.println("Database connection successful!");
        } else {
            System.err.println("Database connection failed!");
            JOptionPane.showMessageDialog(null, 
                "Database connection failed. Please check your configuration.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    System.out.println("Login application started successfully!");
                } catch (Exception e) {
                    System.err.println("Error starting application: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error starting application: " + e.getMessage(), 
                        "Application Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
