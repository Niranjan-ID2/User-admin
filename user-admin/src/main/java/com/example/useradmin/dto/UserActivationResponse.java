package com.example.useradmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivationResponse {
    private String message;
    private String accessToken;
    // Potentially add user details if needed in the response
}
