package com.urlshortener.service;

import com.urlshortener.dto.ShortenURLRequest;
import com.urlshortener.dto.ShortenURLResponse;
import com.urlshortener.model.URLMapping;
import com.urlshortener.repository.URLRepository;
import com.urlshortener.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for URLService class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("URLService Tests")
class URLServiceTest {

    @Mock
    private URLRepository urlRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private URLService urlService;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TEST_ORIGINAL_URL = "https://www.example.com";
    private static final String TEST_SHORT_CODE = "abc123";
    private static final String TEST_CUSTOM_ALIAS = "mylink";

    @BeforeEach
    void setUp() {
        // Set up configuration properties using reflection
        ReflectionTestUtils.setField(urlService, "baseUrl", BASE_URL);
        ReflectionTestUtils.setField(urlService, "shortCodeLength", 6);
        ReflectionTestUtils.setField(urlService, "defaultExpiryDays", 365);
    }

    @Test
    @DisplayName("Should shorten URL successfully")
    void testShortenUrlSuccess() {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);
        
        when(base62Encoder.encodeWithMinLength(anyLong(), eq(6))).thenReturn(TEST_SHORT_CODE);
        when(urlRepository.existsByShortCode(TEST_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(URLMapping.class))).thenAnswer(invocation -> {
            URLMapping mapping = invocation.getArgument(0);
            mapping.setId("test-id");
            return mapping;
        });

        // Act
        ShortenURLResponse response = urlService.shortenUrl(request);

        // Assert
        assertNotNull(response);
        assertEquals(BASE_URL + "/" + TEST_SHORT_CODE, response.getShortUrl());
        assertEquals(TEST_ORIGINAL_URL, response.getOriginalUrl());
        assertEquals(TEST_SHORT_CODE, response.getShortCode());
        assertNotNull(response.getExpiresAt());
        
        verify(urlRepository).save(any(URLMapping.class));
        verify(base62Encoder).encodeWithMinLength(anyLong(), eq(6));
    }

    @Test
    @DisplayName("Should shorten URL with custom code")
    void testShortenUrlWithCustomCode() {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);
        request.setCustomCode(TEST_CUSTOM_ALIAS);
        
        when(urlRepository.existsByShortCode(TEST_CUSTOM_ALIAS)).thenReturn(false);
        when(urlRepository.save(any(URLMapping.class))).thenAnswer(invocation -> {
            URLMapping mapping = invocation.getArgument(0);
            mapping.setId("test-id");
            return mapping;
        });

        // Act
        ShortenURLResponse response = urlService.shortenUrl(request);

        // Assert
        assertNotNull(response);
        assertEquals(BASE_URL + "/" + TEST_CUSTOM_ALIAS, response.getShortUrl());
        assertEquals(TEST_ORIGINAL_URL, response.getOriginalUrl());
        assertEquals(TEST_CUSTOM_ALIAS, response.getShortCode());
        
        verify(urlRepository).save(any(URLMapping.class));
        verify(base62Encoder, never()).encodeWithMinLength(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception for invalid URL")
    void testShortenUrlInvalidUrl() {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl("invalid-url");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> urlService.shortenUrl(request)
        );
        
        assertTrue(exception.getMessage().contains("Invalid URL"));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception for duplicate custom code")
    void testShortenUrlDuplicateCustomCode() {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);
        request.setCustomCode(TEST_CUSTOM_ALIAS);
        
        when(urlRepository.existsByShortCode(TEST_CUSTOM_ALIAS)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> urlService.shortenUrl(request)
        );
        
        assertTrue(exception.getMessage().contains("Custom code already exists") || 
                  exception.getMessage().contains("already exists"));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get original URL successfully")
    void testGetOriginalUrlSuccess() {
        // Arrange
        URLMapping mapping = createTestURLMapping();
        when(urlRepository.findByShortCode(TEST_SHORT_CODE)).thenReturn(Optional.of(mapping));

        // Act
        String originalUrl = urlService.getOriginalUrl(TEST_SHORT_CODE);

        // Assert
        assertEquals(TEST_ORIGINAL_URL, originalUrl);
    }

    @Test
    @DisplayName("Should throw exception when short code not found")
    void testGetOriginalUrlNotFound() {
        // Arrange
        when(urlRepository.findByShortCode(TEST_SHORT_CODE)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> urlService.getOriginalUrl(TEST_SHORT_CODE)
        );
        
        assertTrue(exception.getMessage().contains("Short code not found") || 
                  exception.getMessage().contains("not found"));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when URL is expired")
    void testGetOriginalUrlExpired() {
        // Arrange
        URLMapping mapping = createTestURLMapping();
        mapping.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired yesterday
        when(urlRepository.findByShortCode(TEST_SHORT_CODE)).thenReturn(Optional.of(mapping));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> urlService.getOriginalUrl(TEST_SHORT_CODE)
        );
        
        assertTrue(exception.getMessage().contains("expired") || 
                  exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should generate unique short code when collision occurs")
    void testGenerateUniqueShortCodeWithCollision() {
        // Arrange
        String firstCode = "abc123";
        String secondCode = "def456";
        
        when(base62Encoder.encodeWithMinLength(anyLong(), eq(6)))
            .thenReturn(firstCode)
            .thenReturn(secondCode);
        when(urlRepository.existsByShortCode(firstCode)).thenReturn(true);
        when(urlRepository.existsByShortCode(secondCode)).thenReturn(false);

        // Use reflection to call private method
        String result = (String) ReflectionTestUtils.invokeMethod(urlService, "generateUniqueShortCode");

        // Assert
        assertEquals(secondCode, result);
        verify(base62Encoder, times(2)).encodeWithMinLength(anyLong(), eq(6));
    }

    @Test
    @DisplayName("Should validate URL correctly")
    void testUrlValidation() {
        // Valid URLs
        assertTrue(isValidUrl("https://www.example.com"));
        assertTrue(isValidUrl("http://example.com"));
        assertTrue(isValidUrl("https://sub.example.com/path?query=value"));
        assertTrue(isValidUrl("http://localhost:8080"));
        
        // Invalid URLs
        assertFalse(isValidUrl("not-a-url"));
        assertFalse(isValidUrl("ftp://example.com"));
        assertFalse(isValidUrl(""));
        assertFalse(isValidUrl(null));
    }

    @Test
    @DisplayName("Should handle shorten URL with custom expiration")
    void testShortenUrlWithCustomExpiration() {
        // Arrange
        ShortenURLRequest request = new ShortenURLRequest();
        request.setUrl(TEST_ORIGINAL_URL);
        request.setExpiresInDays(30);
        
        when(base62Encoder.encodeWithMinLength(anyLong(), eq(6))).thenReturn(TEST_SHORT_CODE);
        when(urlRepository.existsByShortCode(TEST_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(URLMapping.class))).thenAnswer(invocation -> {
            URLMapping mapping = invocation.getArgument(0);
            mapping.setId("test-id");
            return mapping;
        });

        // Act
        ShortenURLResponse response = urlService.shortenUrl(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getExpiresAt());
        // Verify expiration is approximately 30 days from now
        LocalDateTime expectedExpiration = LocalDateTime.now().plusDays(30);
        assertTrue(response.getExpiresAt().isBefore(expectedExpiration.plusMinutes(1)));
        assertTrue(response.getExpiresAt().isAfter(expectedExpiration.minusMinutes(1)));
    }

    private URLMapping createTestURLMapping() {
        URLMapping mapping = new URLMapping();
        mapping.setId("test-id");
        mapping.setOriginalUrl(TEST_ORIGINAL_URL);
        mapping.setShortCode(TEST_SHORT_CODE);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setExpiresAt(LocalDateTime.now().plusDays(365));
        mapping.setDomain("example.com");
        return mapping;
    }

    private boolean isValidUrl(String url) {
        // Use reflection to call private method
        return (Boolean) ReflectionTestUtils.invokeMethod(urlService, "isValidUrl", url);
    }
}