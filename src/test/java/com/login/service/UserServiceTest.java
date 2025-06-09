package com.login.service;

import com.login.model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;

/**
 * Test class for UserService
 */
public class UserServiceTest {
    
    private UserService userService;
    
    @Before
    public void setUp() {
        userService = new UserService();
    }
    
    @Test
    public void testRegisterUser_ValidInput() throws SQLException {
        String username = "testuser" + System.currentTimeMillis();
        String password = "password123";
        String email = "test@example.com";
        
        User user = userService.registerUser(username, password, email);
        
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_EmptyUsername() throws SQLException {
        userService.registerUser("", "password123", "test@example.com");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_ShortUsername() throws SQLException {
        userService.registerUser("ab", "password123", "test@example.com");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_WeakPassword() throws SQLException {
        userService.registerUser("testuser", "123", "test@example.com");
    }
    
    @Test
    public void testAuthenticateUser_ValidCredentials() throws SQLException {
        String username = "authtest" + System.currentTimeMillis();
        String password = "password123";
        
        // Register user first
        userService.registerUser(username, password, "test@example.com");
        
        // Then authenticate
        User authenticatedUser = userService.authenticateUser(username, password);
        
        assertNotNull(authenticatedUser);
        assertEquals(username, authenticatedUser.getUsername());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateUser_InvalidPassword() throws SQLException {
        String username = "authtest2" + System.currentTimeMillis();
        String password = "password123";
        
        // Register user first
        userService.registerUser(username, password, "test@example.com");
        
        // Try to authenticate with wrong password
        userService.authenticateUser(username, "wrongpassword");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateUser_NonexistentUser() throws SQLException {
        userService.authenticateUser("nonexistent", "password123");
    }
    
    @Test
    public void testIsUsernameAvailable() throws SQLException {
        String username = "availtest" + System.currentTimeMillis();
        
        // Should be available initially
        assertTrue(userService.isUsernameAvailable(username));
        
        // Register user
        userService.registerUser(username, "password123", "test@example.com");
        
        // Should not be available now
        assertFalse(userService.isUsernameAvailable(username));
    }
}
