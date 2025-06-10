package com.example.useradmin.client;

import com.example.useradmin.client.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.base-url}")
public interface NotificationServiceFeignClient {

    @PostMapping("/api/v1/notify/trigger")
    ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request);
}
