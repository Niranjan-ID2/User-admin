package com.example.useradmin.dto;

import com.example.useradmin.model.UserStatus;
import java.time.LocalDateTime;

// This DTO is for sending user data back to the client, omitting sensitive fields like OTP.
public class UserResponse {
    private Long id;
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

    // Constructor, Getters, and Setters
    public UserResponse(Long id, String email, String firstName, String middleName, String lastName, String phone, String gender, Integer age, String state, String country, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.state = state;
        this.country = country;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
