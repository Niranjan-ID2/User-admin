package com.example.useradmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAdminApplication.class, args);
	}

}
