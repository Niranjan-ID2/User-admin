package com.example.useradmin.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class OtpService {

    private final PasswordEncoder passwordEncoder;
    private final Random random = new SecureRandom();

    // PasswordEncoder will be injected. It should be defined as a Bean elsewhere (e.g., SecurityConfig)
    public OtpService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a 6-digit numeric OTP.
     * @return A string representing the 6-digit OTP.
     */
    public String generateOtp() {
        // Generate a 6-digit number
        int otpNumber = 100000 + random.nextInt(900000);
        return String.valueOf(otpNumber);
    }

    /**
     * Hashes the given OTP using the configured PasswordEncoder.
     * @param otp The raw OTP string.
     * @return The hashed OTP string.
     */
    public String hashOtp(String otp) {
        if (otp == null || otp.isEmpty()) {
            throw new IllegalArgumentException("OTP cannot be null or empty for hashing.");
        }
        return passwordEncoder.encode(otp);
    }

    /**
     * Validates a raw OTP against a stored hashed OTP.
     * @param rawOtp The raw OTP entered by the user.
     * @param hashedOtp The stored hashed OTP from the database.
     * @return True if the raw OTP matches the hashed OTP, false otherwise.
     */
    public boolean validateOtp(String rawOtp, String hashedOtp) {
        if (rawOtp == null || rawOtp.isEmpty()) {
            return false; // Or throw IllegalArgumentException
        }
        if (hashedOtp == null || hashedOtp.isEmpty()) {
            return false; // Or throw IllegalArgumentException, indicates an issue with stored OTP
        }
        return passwordEncoder.matches(rawOtp, hashedOtp);
    }
}
