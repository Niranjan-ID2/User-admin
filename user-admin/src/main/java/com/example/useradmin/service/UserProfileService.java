package com.example.useradmin.service;

import com.example.useradmin.dto.UserProfileUpdateRequest;
import com.example.useradmin.exception.UserNotActiveException;
import com.example.useradmin.exception.UserNotFoundException;
import com.example.useradmin.model.User;
import com.example.useradmin.model.UserStatus;
import com.example.useradmin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // For checking if string has text

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User updateUserProfile(String email, UserProfileUpdateRequest profileDetails) {
        logger.info("Attempting to update profile for user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Profile update attempt for non-existent user: {}", email);
                    return new UserNotFoundException("User with email '" + email + "' not found.");
                });

        if (user.getStatus() != UserStatus.ACTIVE) {
            logger.warn("Attempt to update profile for non-active user: {}. Status: {}", email, user.getStatus());
            throw new UserNotActiveException("User with email '" + email + "' is not active. Profile cannot be updated.");
        }

        // Update fields only if they are provided in the request (non-null or non-empty for strings)
        // For StringUtils.hasText, ensure you have spring-core on the classpath (it's usually there with Spring Boot)
        if (StringUtils.hasText(profileDetails.getFirstName())) {
            user.setFirstName(profileDetails.getFirstName());
        }
        if (StringUtils.hasText(profileDetails.getMiddleName())) {
            user.setMiddleName(profileDetails.getMiddleName());
        }
        if (StringUtils.hasText(profileDetails.getLastName())) {
            user.setLastName(profileDetails.getLastName());
        }
        if (StringUtils.hasText(profileDetails.getPhone())) {
            user.setPhone(profileDetails.getPhone());
        }
        if (StringUtils.hasText(profileDetails.getGender())) {
            user.setGender(profileDetails.getGender());
        }
        if (profileDetails.getAge() != null) { // For Integer, check for null
            user.setAge(profileDetails.getAge());
        }
        if (StringUtils.hasText(profileDetails.getState())) {
            user.setState(profileDetails.getState());
        }
        if (StringUtils.hasText(profileDetails.getCountry())) {
            user.setCountry(profileDetails.getCountry());
        }

        User updatedUser = userRepository.save(user);
        logger.info("Profile updated successfully for user email: {}", email);
        return updatedUser;
    }
}
