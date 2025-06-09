package com.example.useradmin.service;

import com.example.useradmin.exception.InvalidOtpException;
import com.example.useradmin.exception.OtpExpiredException;
import com.example.useradmin.exception.UserAlreadyActiveException;
import com.example.useradmin.exception.UserNotFoundException;
import com.example.useradmin.model.User;
import com.example.useradmin.model.UserStatus;
import com.example.useradmin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserActivationService {

    private static final Logger logger = LoggerFactory.getLogger(UserActivationService.class);

    private final UserRepository userRepository;
    private final OtpService otpService; // OtpService handles OTP validation using PasswordEncoder

    public UserActivationService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    @Transactional
    public void activateUser(String email, String otp) {
        logger.info("Attempting to activate user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Activation attempt for non-existent user: {}", email);
                    return new UserNotFoundException("User with email '" + email + "' not found.");
                });

        if (user.getStatus() == UserStatus.ACTIVE) {
            logger.warn("User {} is already active. No action needed.", email);
            throw new UserAlreadyActiveException("User with email '" + email + "' is already active.");
        }

        if (user.getStatus() != UserStatus.PENDING_ACTIVATION) {
            logger.warn("User {} is not in PENDING_ACTIVATION state. Current state: {}", email, user.getStatus());
            // Or throw a more specific exception if activation is only allowed from PENDING_ACTIVATION
            throw new InvalidOtpException("User is not awaiting OTP verification.");
        }

        if (user.getOtpExpiryTime() == null || LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            logger.warn("OTP expired for user {}. Expiry time: {}", email, user.getOtpExpiryTime());
            // Optionally, clear the expired OTP from the user record here or let registration resend
            user.setOtp(null);
            user.setOtpExpiryTime(null);
            userRepository.save(user); // Save changes to clear OTP
            throw new OtpExpiredException("OTP has expired. Please request a new OTP.");
        }

        if (!otpService.validateOtp(otp, user.getOtp())) {
            logger.warn("Invalid OTP provided for user {}.", email);
            // Add OTP attempt limit logic here in a future enhancement
            throw new InvalidOtpException("Invalid OTP provided.");
        }

        // OTP is valid and not expired
        user.setStatus(UserStatus.ACTIVE);
        user.setOtp(null); // Clear OTP after successful activation
        user.setOtpExpiryTime(null); // Clear OTP expiry time

        userRepository.save(user);
        logger.info("User {} successfully activated.", email);
    }
}
