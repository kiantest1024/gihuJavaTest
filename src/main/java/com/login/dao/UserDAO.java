package com.login.dao;

import com.login.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User operations
 */
public interface UserDAO {
    
    /**
     * Create a new user
     * @param user the user to create
     * @return the created user with generated ID
     * @throws SQLException if database operation fails
     */
    User createUser(User user) throws SQLException;
    
    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     * @throws SQLException if database operation fails
     */
    Optional<User> findByUsername(String username) throws SQLException;
    
    /**
     * Find a user by ID
     * @param id the user ID to search for
     * @return Optional containing the user if found, empty otherwise
     * @throws SQLException if database operation fails
     */
    Optional<User> findById(Long id) throws SQLException;
    
    /**
     * Update user information
     * @param user the user to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if database operation fails
     */
    boolean updateUser(User user) throws SQLException;
    
    /**
     * Delete a user by ID
     * @param id the ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database operation fails
     */
    boolean deleteUser(Long id) throws SQLException;
    
    /**
     * Get all users
     * @return list of all users
     * @throws SQLException if database operation fails
     */
    List<User> getAllUsers() throws SQLException;
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     * @throws SQLException if database operation fails
     */
    boolean usernameExists(String username) throws SQLException;
    
    /**
     * Update user's last login time
     * @param username the username
     * @throws SQLException if database operation fails
     */
    void updateLastLogin(String username) throws SQLException;
}
