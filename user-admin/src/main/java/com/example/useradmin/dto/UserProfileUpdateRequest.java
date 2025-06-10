package com.example.useradmin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Add other relevant JSR 303 annotations as needed (e.g., @Pattern for phone)
@Data
@NoArgsConstructor
@Builder // Added AllArgsConstructor for builder to work, or if it's needed explicitly
@lombok.AllArgsConstructor // Explicitly adding for @Builder, can be removed if builder not used or if default constructor + setters are enough
public class UserProfileUpdateRequest {

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name cannot exceed 100 characters")
    private String middleName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Size(max = 50, message = "Gender cannot exceed 50 characters")
    private String gender;

    @Min(value = 0, message = "Age must be a positive value")
    private Integer age;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;
}
