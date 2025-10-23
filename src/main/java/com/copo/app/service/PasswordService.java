package com.copo.app.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling password operations using BCrypt
 */
@Service
public class PasswordService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword the plain text password to hash
     * @return the hashed password
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Verify a plain text password against a hashed password
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
    
    /**
     * Check if a password needs to be re-encoded (for future use)
     * @param encodedPassword the encoded password to check
     * @return true if the password needs to be re-encoded
     */
    public boolean needsReencoding(String encodedPassword) {
        return passwordEncoder.upgradeEncoding(encodedPassword);
    }
}

