package com.example.useradmin.client;

import com.example.useradmin.client.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NotificationServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final String notificationTriggerEndpoint = "/api/v1/notify/trigger"; // As per user spec

    public NotificationServiceClient(RestTemplate restTemplate,
                                     @Value("${notification.service.base-url}") String notificationServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceBaseUrl + notificationTriggerEndpoint;
    }

    public void sendOtpEmail(String userEmail, String otp) {
        NotificationRequest request = new NotificationRequest();
        request.setName("USER_REGISTRATION_OTP"); // Example template name
        request.setSubscriberId(userEmail);
        request.setEmail(userEmail);

        // Payload for the notification template
        request.addPayloadVariable("otpCode", otp);
        request.addPayloadVariable("userName", userEmail); // Or a more generic placeholder if name not known yet

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> entity = new HttpEntity<>(request, headers);

        try {
            logger.info("Sending OTP notification request to {}: {}", notificationServiceUrl, request);
            // Assuming the notification service returns a 2xx on success and doesn't require specific response handling here
            ResponseEntity<String> response = restTemplate.exchange(notificationServiceUrl, HttpMethod.POST, entity, String.class);
            logger.info("Notification service response status: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                // Log non-successful responses that are not exceptions
                logger.warn("Notification service responded with status {} and body: {}", response.getStatusCode(), response.getBody());
                // Depending on requirements, might throw a custom exception here
            }
        } catch (RestClientException e) {
            logger.error("Error calling notification service at {}: {}", notificationServiceUrl, e.getMessage());
            // Depending on requirements, might throw a custom exception here to be handled upstream
            // For example: throw new NotificationServiceException("Failed to send OTP email", e);
            // For now, just logging the error. The calling service needs to be aware of potential failures.
        }
    }
}
