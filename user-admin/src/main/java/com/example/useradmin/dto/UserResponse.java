package com.example.useradmin.dto;

import com.example.useradmin.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// This DTO is for sending user data back to the client, omitting sensitive fields like OTP.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String gender;
    private Integer age;
    private String state;
    private String country;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method for conversion from User entity
    public static UserResponse fromUser(com.example.useradmin.model.User user) {
        if (user == null) return null;
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getMiddleName(),
            user.getLastName(),
            user.getPhone(),
            user.getGender(),
            user.getAge(),
            user.getState(),
            user.getCountry(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    // Getters (setters might not be strictly necessary if only used for response)
}
