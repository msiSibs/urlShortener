package com.urlshortener.controller;

import com.urlshortener.config.TestSecurityConfig;
import com.urlshortener.service.URLService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RedirectController
 */
@WebMvcTest(RedirectController.class)
@Import(TestSecurityConfig.class)
@DisplayName("RedirectController Tests")
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private URLService urlService;

    private static final String TEST_ORIGINAL_URL = "https://www.example.com";
    private static final String TEST_SHORT_CODE = "abc123";

    @Test
    @DisplayName("Should redirect to original URL successfully")
    void testRedirectSuccess() throws Exception {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(TEST_ORIGINAL_URL);

        // Act & Assert
        mockMvc.perform(get("/" + TEST_SHORT_CODE))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", TEST_ORIGINAL_URL));
    }

    @Test
    @DisplayName("Should return not found for invalid short code")
    void testRedirectNotFound() throws Exception {
        // Arrange
        when(urlService.getOriginalUrl("invalid")).thenThrow(new IllegalArgumentException("Short URL not found"));

        // Act & Assert
        mockMvc.perform(get("/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not found for expired URL")
    void testRedirectExpired() throws Exception {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenThrow(new IllegalArgumentException("Short URL has expired"));

        // Act & Assert
        mockMvc.perform(get("/" + TEST_SHORT_CODE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get URL info successfully")
    void testGetUrlInfoSuccess() throws Exception {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(TEST_ORIGINAL_URL);

        // Act & Assert
        mockMvc.perform(get("/info/" + TEST_SHORT_CODE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return not found for info on invalid short code")
    void testGetUrlInfoNotFound() throws Exception {
        // Arrange
        when(urlService.getOriginalUrl("invalid")).thenThrow(new IllegalArgumentException("Short URL not found"));

        // Act & Assert
        mockMvc.perform(get("/info/invalid"))
                .andExpect(status().isBadRequest());
    }
}