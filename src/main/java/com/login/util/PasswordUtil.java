package com.login.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password operations
 */
public class PasswordUtil {
    
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hash a password using BCrypt
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a password against its hash
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate password strength
     * @param password the password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        
        return hasLetter && hasDigit;
    }

    /**
     * Get password requirements message
     * @return string describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 6 characters long and contain at least one letter and one number.";
    }
}
