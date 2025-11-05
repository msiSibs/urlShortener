package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * Restricts access to sensitive endpoints like actuator.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP security to restrict access to actuator endpoints.
     * Allows public access to API endpoints but requires authentication for actuator.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API (can be enabled with proper token handling)
            .csrf(csrf -> csrf.disable())
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow public access to all API endpoints
                .requestMatchers("/api/**").permitAll()
                // Allow public access to health endpoint
                .requestMatchers("/health", "/actuator/health").permitAll()
                // Restrict other actuator endpoints - require ADMIN role
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                // Allow public access to static resources and homepage
                .requestMatchers("/", "/index.html", "/*.css", "/*.js", "/*.ico").permitAll()
                // Allow everything else in development
                .anyRequest().permitAll()
            )
            // Use HTTP Basic authentication for actuator endpoints
            .httpBasic(basic -> {})
            // Disable form login as this is a REST API
            .formLogin(form -> form.disable());

        return http.build();
    }
}
