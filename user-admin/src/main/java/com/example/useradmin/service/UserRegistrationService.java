package com.example.useradmin.service;

import com.example.useradmin.client.NotificationServiceClient;
import com.example.useradmin.exception.NotificationServiceException;
import com.example.useradmin.exception.UserAlreadyExistsException;
import com.example.useradmin.model.User;
import com.example.useradmin.model.UserStatus;
import com.example.useradmin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder; // Will be injected
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final NotificationServiceClient notificationServiceClient;
    private final PasswordEncoder passwordEncoder; // Will be provided by SecurityConfig

    @Value("${otp.validity.duration.minutes:10}") // Default OTP validity to 10 minutes
    private int otpValidityDurationMinutes;

    public UserRegistrationService(UserRepository userRepository,
                                   OtpService otpService,
                                   NotificationServiceClient notificationServiceClient,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.notificationServiceClient = notificationServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String email) {
        // Email format validation should ideally be handled at the controller level with @Valid
        // or here if preferred. For now, assume basic check or rely on DB constraint for format.

        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            if (existingUser.getStatus() == UserStatus.ACTIVE) {
                logger.warn("Attempt to register with already active email: {}", email);
                throw new UserAlreadyExistsException("User with email '" + email + "' already exists and is active.");
            } else if (existingUser.getStatus() == UserStatus.PENDING_ACTIVATION) {
                logger.info("User with email '{}' exists and is pending activation. Re-sending OTP.", email);
                // Update OTP for existing PENDING_ACTIVATION user
                generateAndSendOtp(existingUser);
                return; // Exit after resending OTP
            }
            // Handle other statuses like SUSPENDED if necessary, for now, treat as new registration or error
        }

        // New user registration
        User newUser = new User(email);
        newUser.setStatus(UserStatus.PENDING_ACTIVATION);
        generateAndSendOtp(newUser);
    }

    private void generateAndSendOtp(User user) {
        String rawOtp = otpService.generateOtp();
        String hashedOtp = otpService.hashOtp(rawOtp); // OtpService uses PasswordEncoder

        user.setOtp(hashedOtp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(otpValidityDurationMinutes));

        // If it's a new user, status would have been set to PENDING_ACTIVATION before calling this.
        // If it's an existing PENDING_ACTIVATION user, status remains the same.
        // Ensure status is PENDING_ACTIVATION if not already set
        if (user.getStatus() != UserStatus.PENDING_ACTIVATION) {
             user.setStatus(UserStatus.PENDING_ACTIVATION);
        }


        try {
            userRepository.save(user); // Save user with new/updated OTP details
            logger.info("User record saved/updated for email: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving user during OTP generation for email: {}", user.getEmail(), e);
            // This could be a DataIntegrityViolationException if email uniqueness is violated
            // by a concurrent request. Transactional behavior should handle rollback.
            throw new RuntimeException("Failed to save user details. Please try again.", e);
        }

        try {
            notificationServiceClient.sendOtpEmail(user.getEmail(), rawOtp);
            logger.info("OTP sent successfully to email: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send OTP notification for email {}: {}", user.getEmail(), e.getMessage(), e);
            // Rollback is managed by @Transactional if this is a RuntimeException.
            // If NotificationServiceClient throws a specific checked exception, it needs to be handled.
            // For now, wrapping it in a runtime exception to ensure rollback.
            throw new NotificationServiceException("Failed to send OTP email. Please try again later.", e);
        }
    }
}
