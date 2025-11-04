package com.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.ShortenURLRequest;
import com.urlshortener.dto.ShortenURLResponse;
import com.urlshortener.service.URLService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for URLController
 */
@WebMvcTest(URLController.class)
@DisplayName("URLController Tests")
class URLControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private URLService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_ORIGINAL_URL = "https://www.example.com";
    private static final String TEST_SHORT_CODE = "abc123";
    private static final String BASE_URL = "http://localhost:8080/";

    @Test
    @DisplayName("Should shorten URL successfully")
    void testShortenUrlSuccess() throws Exception {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);

        ShortenURLResponse response = new ShortenURLResponse();
        response.setOriginalUrl(TEST_ORIGINAL_URL);
        response.setShortUrl(BASE_URL + TEST_SHORT_CODE);
        response.setShortCode(TEST_SHORT_CODE);
        response.setExpiresAt(LocalDateTime.now().plusDays(7));

        when(urlService.shortenUrl(any(ShortenURLRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value(TEST_ORIGINAL_URL))
                .andExpect(jsonPath("$.shortUrl").value(BASE_URL + TEST_SHORT_CODE))
                .andExpect(jsonPath("$.shortCode").value(TEST_SHORT_CODE))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("Should return bad request for invalid URL")
    void testShortenUrlInvalidUrl() throws Exception {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl("invalid-url");

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request for empty URL")
    void testShortenUrlEmptyUrl() throws Exception {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl("");

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should shorten URL with custom code")
    void testShortenUrlWithCustomCode() throws Exception {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);
        request.setCustomCode("mylink");

        ShortenURLResponse response = new ShortenURLResponse();
        response.setOriginalUrl(TEST_ORIGINAL_URL);
        response.setShortUrl(BASE_URL + "mylink");
        response.setShortCode("mylink");
        response.setExpiresAt(LocalDateTime.now().plusDays(7));

        when(urlService.shortenUrl(any(ShortenURLRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("mylink"));
    }

    @Test
    @DisplayName("Should handle service exception")
    void testShortenUrlServiceException() throws Exception {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);

        when(urlService.shortenUrl(any(ShortenURLRequest.class)))
                .thenThrow(new IllegalArgumentException("Custom code already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get statistics successfully")
    void testGetStatistics() throws Exception {
        // Arrange - Mock the service to return sample statistics
        when(urlService.getStatistics()).thenReturn(java.util.Map.of(
            "totalUrls", 10,
            "totalClicks", 25,
            "activeUrls", 8,
            "expiredUrls", 2,
            "recentUrls", java.util.List.of()
        ));

        // Act & Assert
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalUrls").value(10))
                .andExpect(jsonPath("$.totalClicks").value(25))
                .andExpect(jsonPath("$.activeUrls").value(8))
                .andExpect(jsonPath("$.expiredUrls").value(2))
                .andExpect(jsonPath("$.recentUrls").isArray());
    }

    @Test
    @DisplayName("Should cleanup expired URLs successfully")
    void testCleanupExpiredUrls() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cleanup"))
                .andExpect(status().isOk());
    }
}