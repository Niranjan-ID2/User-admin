package com.example.useradmin.controller;

import com.example.useradmin.dto.UserProfileUpdateRequest;
import com.example.useradmin.dto.UserResponse;
import com.example.useradmin.model.User; // Import User model
import com.example.useradmin.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PutMapping("/{email}/profile") // Using email from path as per plan
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable String email,
                                                               @Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        User updatedUser = userProfileService.updateUserProfile(email, profileRequest);
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    }
}
