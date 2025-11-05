package com.urlshortener.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration that disables security for testing purposes.
 * This configuration is only active when running tests.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Override the security filter chain to allow all requests during testing.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for tests
            .csrf(csrf -> csrf.disable())
            // Allow all requests in test environment
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // Disable form login
            .formLogin(form -> form.disable());

        return http.build();
    }
}
