package com.example.useradmin.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String name; // Name of the notification template/type
    private String subscriberId; // Unique ID of the subscriber, e.g., email or user ID
    private String email;
    private String phone; // Optional
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();

    public void addPayloadVariable(String key, Object value) {
        this.payload.put(key, value);
    }
}
