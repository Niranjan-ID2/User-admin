package com.example.useradmin.controller;

import com.example.useradmin.dto.UserRegistrationRequest;
import com.example.useradmin.dto.SimpleMessageResponse;
import com.example.useradmin.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleMessageResponse> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        userRegistrationService.registerUser(registrationRequest.getEmail());
        // 202 Accepted is suitable as OTP sending is async and user is not yet fully created/active
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                                     .body(new SimpleMessageResponse("Registration request received. Please check your email for OTP."));
    }
}
