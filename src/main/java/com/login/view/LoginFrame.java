package com.login.view;

import com.login.model.User;
import com.login.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Main login GUI frame
 */
public class LoginFrame extends JFrame {
    
    private final UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton switchModeButton;
    private JLabel statusLabel;
    private boolean isLoginMode = true;
    
    public LoginFrame() {
        this.userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        switchModeButton = new JButton("Switch to Register");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("User Login System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Email (initially hidden)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setVisible(false);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailField.setVisible(false);
        formPanel.add(emailField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        registerButton.setVisible(false);
        
        // Switch mode panel
        JPanel switchPanel = new JPanel();
        switchPanel.add(switchModeButton);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(switchPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoginMode) {
                    performLogin();
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isLoginMode) {
                    performRegistration();
                }
            }
        });
        
        switchModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchMode();
            }
        });
        
        // Enter key support
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoginMode) {
                    performLogin();
                } else {
                    performRegistration();
                }
            }
        };
        
        usernameField.addActionListener(enterAction);
        passwordField.addActionListener(enterAction);
        emailField.addActionListener(enterAction);
    }
    
    private void switchMode() {
        isLoginMode = !isLoginMode;
        
        if (isLoginMode) {
            // Switch to login mode
            setTitle("Login System - Login");
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            switchModeButton.setText("Switch to Register");
            hideEmailField();
        } else {
            // Switch to register mode
            setTitle("Login System - Register");
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            switchModeButton.setText("Switch to Login");
            showEmailField();
        }
        
        clearFields();
        clearStatus();
    }
    
    private void showEmailField() {
        Component[] components = ((JPanel) getContentPane().getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().equals("Email:")) {
                comp.setVisible(true);
            }
        }
        emailField.setVisible(true);
        pack();
    }
    
    private void hideEmailField() {
        Component[] components = ((JPanel) getContentPane().getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().equals("Email:")) {
                comp.setVisible(false);
            }
        }
        emailField.setVisible(false);
        pack();
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields", Color.RED);
            return;
        }
        
        try {
            User user = userService.authenticateUser(username, password);
            showStatus("Login successful! Welcome, " + user.getUsername(), Color.GREEN);
            
            // Here you could open the main application window
            showMainWindow(user);
            
        } catch (IllegalArgumentException e) {
            showStatus(e.getMessage(), Color.RED);
        } catch (SQLException e) {
            showStatus("Database error: " + e.getMessage(), Color.RED);
        }
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username and password are required", Color.RED);
            return;
        }
        
        try {
            User user = userService.registerUser(username, password, email.isEmpty() ? null : email);
            showStatus("Registration successful! You can now login.", Color.GREEN);
            
            // Switch to login mode after successful registration
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(2000);
                    switchMode();
                    usernameField.setText(username);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            
        } catch (IllegalArgumentException e) {
            showStatus(e.getMessage(), Color.RED);
        } catch (SQLException e) {
            showStatus("Database error: " + e.getMessage(), Color.RED);
        }
    }
    
    private void showMainWindow(User user) {
        // Create a simple welcome window
        JFrame mainFrame = new JFrame("Welcome");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 200);
        mainFrame.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("<html><center>Welcome, " + user.getUsername() + "!<br>" +
                "Login successful.<br><br>" +
                "Last login: " + (user.getLastLogin() != null ? user.getLastLogin() : "First time") +
                "</center></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            mainFrame.dispose();
            this.setVisible(true);
            clearFields();
            clearStatus();
        });
        
        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        mainFrame.add(panel);
        mainFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private void clearStatus() {
        statusLabel.setText(" ");
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
    }
}
