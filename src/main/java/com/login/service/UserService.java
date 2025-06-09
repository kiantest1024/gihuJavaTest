package com.login.service;

import com.login.dao.UserDAO;
import com.login.dao.impl.UserDAOImpl;
import com.login.model.User;
import com.login.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service class for user-related business logic
 */
public class UserService {
    
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAOImpl();
    }
    
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Register a new user
     * @param username the username
     * @param password the plain text password
     * @param email the email address
     * @return the created user
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database operation fails
     */
    public User registerUser(String username, String password, String email) 
            throws IllegalArgumentException, SQLException {
        
        // Validate input
        validateUserInput(username, password, email);
        
        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Hash the password
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // Create user object
        User user = new User(username, hashedPassword, email);
        
        // Save to database
        return userDAO.createUser(user);
    }

    /**
     * Authenticate a user
     * @param username the username
     * @param password the plain text password
     * @return the authenticated user if successful
     * @throws IllegalArgumentException if authentication fails
     * @throws SQLException if database operation fails
     */
    public User authenticateUser(String username, String password) 
            throws IllegalArgumentException, SQLException {
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Find user by username
        Optional<User> userOptional = userDAO.findByUsername(username.trim());
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        
        // Update last login time
        userDAO.updateLastLogin(username);
        
        return user;
    }

    /**
     * Change user password
     * @param username the username
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return true if password was changed successfully
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database operation fails
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) 
            throws IllegalArgumentException, SQLException {
        
        // Authenticate with old password first
        User user = authenticateUser(username, oldPassword);
        
        // Validate new password
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordRequirements());
        }
        
        // Hash new password
        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        user.setPassword(hashedNewPassword);
        
        // Update in database
        return userDAO.updateUser(user);
    }

    /**
     * Get user by username
     * @param username the username
     * @return the user if found
     * @throws SQLException if database operation fails
     */
    public Optional<User> getUserByUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userDAO.findByUsername(username.trim());
    }

    /**
     * Check if username is available
     * @param username the username to check
     * @return true if username is available, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean isUsernameAvailable(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !userDAO.usernameExists(username.trim());
    }

    /**
     * Validate user input
     * @param username the username
     * @param password the password
     * @param email the email
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUserInput(String username, String password, String email) 
            throws IllegalArgumentException {
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        
        if (username.trim().length() > 50) {
            throw new IllegalArgumentException("Username cannot be longer than 50 characters");
        }
        
        if (!PasswordUtil.isValidPassword(password)) {
            throw new IllegalArgumentException(PasswordUtil.getPasswordRequirements());
        }
        
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Simple email validation
     * @param email the email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
