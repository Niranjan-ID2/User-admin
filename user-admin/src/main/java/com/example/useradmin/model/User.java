package com.example.useradmin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(length = 100)
    private String firstName;

    @Size(max = 100, message = "Middle name cannot exceed 100 characters")
    @Column(length = 100)
    private String middleName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(length = 100)
    private String lastName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(length = 20)
    private String phone;

    @Size(max = 50, message = "Gender cannot exceed 50 characters")
    @Column(length = 50)
    private String gender;

    private Integer age;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Column(length = 100)
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_ACTIVATION;

    @Column(length = 64) // For storing hashed OTP (e.g., SHA-256)
    private String otp;

    private LocalDateTime otpExpiryTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
