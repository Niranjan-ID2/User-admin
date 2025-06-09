package com.example.useradmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // For CSRF
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF as we are building a stateless REST API
            .csrf(AbstractHttpConfigurer::disable)
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/activate").permitAll()
                // For profile updates, ideally, we'd check if the authenticated user matches the email in the path.
                // For now, let's require any authenticated user.
                // A more complete solution would involve a custom authorization check.
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{email}/profile").authenticated()
                // Actuator health endpoints - permit all for now, can be restricted further
                .requestMatchers("/actuator/**").permitAll()
                // Any other request must be authenticated (default behavior if not specified otherwise)
                .anyRequest().authenticated()
            )
            // Configure session management to be stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            // HTTP Basic authentication can be enabled as a placeholder if needed,
            // but a proper token-based auth (JWT) would be typical for stateless APIs.
            // For now, .authenticated() will trigger basic auth if no other mechanism is found by Spring Security.
            // .httpBasic(Customizer.withDefaults()); // Uncomment to explicitly enable HTTP Basic

        return http.build();
    }
}
