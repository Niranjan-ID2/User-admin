package com.example.useradmin.client;

import com.example.useradmin.client.dto.NotificationRequest;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final NotificationServiceFeignClient notificationServiceFeignClient;

    public NotificationServiceClient(NotificationServiceFeignClient notificationServiceFeignClient) {
        this.notificationServiceFeignClient = notificationServiceFeignClient;
    }

    public void sendOtpEmail(String userEmail, String otp) {
        NotificationRequest request = new NotificationRequest();
        request.setName("USER_REGISTRATION_OTP"); // Example template name
        request.setSubscriberId(userEmail);
        request.setEmail(userEmail);

        // Payload for the notification template
        request.addPayloadVariable("otpCode", otp);
        request.addPayloadVariable("userName", userEmail); // Or a more generic placeholder if name not known yet

        try {
            logger.info("Sending OTP notification request via Feign: {}", request);
            ResponseEntity<String> response = notificationServiceFeignClient.sendNotification(request);
            logger.info("Notification service (Feign) response status: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("Notification service (Feign) responded with status {} and body: {}", response.getStatusCode(), response.getBody());
                // Consider throwing a custom exception for non-2xx responses if specific handling is needed.
                // For example: throw new NotificationServiceException("Notification service returned status " + response.getStatusCode());
            }
        } catch (FeignException e) {
            logger.error("Error calling notification service via Feign: Status: {}, Body: {}, Message: {}", e.status(), e.contentUTF8(), e.getMessage(), e);
            // Depending on requirements, re-throw as a custom exception or handle as per specific needs.
            // For example: throw new NotificationServiceException("Failed to send OTP email due to Feign client error", e);
        } catch (Exception e) { // Catch any other unexpected exceptions
            logger.error("Unexpected error sending OTP email via Feign for email {}: {}", userEmail, e.getMessage(), e);
            // throw new NotificationServiceException("Unexpected error sending OTP email", e);
        }
    }
}
