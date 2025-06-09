package com.example.useradmin.controller;

import com.example.useradmin.dto.UserActivationRequest;
import com.example.useradmin.dto.SimpleMessageResponse;
import com.example.useradmin.service.UserActivationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserActivationController {

    private final UserActivationService userActivationService;

    public UserActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @PostMapping("/activate")
    public ResponseEntity<SimpleMessageResponse> activateUser(@Valid @RequestBody UserActivationRequest activationRequest) {
        userActivationService.activateUser(activationRequest.getEmail(), activationRequest.getOtp());
        return ResponseEntity.ok(new SimpleMessageResponse("User activated successfully."));
    }
}
