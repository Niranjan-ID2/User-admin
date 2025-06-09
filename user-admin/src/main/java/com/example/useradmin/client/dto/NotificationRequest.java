package com.example.useradmin.client.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationRequest {
    private String name; // Name of the notification template/type
    private String subscriberId; // Unique ID of the subscriber, e.g., email or user ID
    private String email;
    private String phone; // Optional
    private Map<String, Object> payload = new HashMap<>();

    // Constructors
    public NotificationRequest() {
    }

    public NotificationRequest(String name, String subscriberId, String email) {
        this.name = name;
        this.subscriberId = subscriberId;
        this.email = email;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public void addPayloadVariable(String key, Object value) {
        this.payload.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRequest that = (NotificationRequest) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(subscriberId, that.subscriberId) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subscriberId, email, phone, payload);
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "name='" + name + '\'' +
                ", subscriberId='" + subscriberId + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", payload=" + payload +
                '}';
    }
}
