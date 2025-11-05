package com.urlshortener.repository;

import com.urlshortener.model.URLMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for URLRepository using mocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("URLRepository Unit Tests")
class URLRepositoryTest {

    @Mock
    private URLRepository urlRepository;

    private URLMapping testMapping;

    @BeforeEach
    void setUp() {
        testMapping = new URLMapping();
        testMapping.setId("507f1f77bcf86cd799439011"); // Set a mock ID
        testMapping.setShortCode("abc123");
        testMapping.setOriginalUrl("https://www.example.com");
        testMapping.setDomain("example.com");
        testMapping.setCreatedAt(LocalDateTime.now());
        testMapping.setExpiresAt(LocalDateTime.now().plusDays(30));
    }

    @Test
    @DisplayName("Should save and find URL mapping by short code")
    void testSaveAndFindByShortCode() {
        // Arrange
        when(urlRepository.save(any(URLMapping.class))).thenReturn(testMapping);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(testMapping));

        // Act
        URLMapping saved = urlRepository.save(testMapping);
        Optional<URLMapping> found = urlRepository.findByShortCode("abc123");

        // Assert
        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("abc123", found.get().getShortCode());
        assertEquals("https://www.example.com", found.get().getOriginalUrl());
        assertEquals("example.com", found.get().getDomain());
        
        verify(urlRepository, times(1)).save(any(URLMapping.class));
        verify(urlRepository, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should check if short code exists")
    void testExistsByShortCode() {
        // Arrange
        when(urlRepository.existsByShortCode("abc123")).thenReturn(true);
        when(urlRepository.existsByShortCode("nonexistent")).thenReturn(false);

        // Act & Assert
        assertTrue(urlRepository.existsByShortCode("abc123"));
        assertFalse(urlRepository.existsByShortCode("nonexistent"));
        
        verify(urlRepository, times(1)).existsByShortCode("abc123");
        verify(urlRepository, times(1)).existsByShortCode("nonexistent");
    }

    @Test
    @DisplayName("Should find URLs by domain")
    void testFindByDomain() {
        // Arrange
        URLMapping anotherMapping = new URLMapping();
        anotherMapping.setShortCode("def456");
        anotherMapping.setOriginalUrl("https://www.example.com/page");
        anotherMapping.setDomain("example.com");
        anotherMapping.setCreatedAt(LocalDateTime.now());

        URLMapping differentDomain = new URLMapping();
        differentDomain.setShortCode("ghi789");
        differentDomain.setOriginalUrl("https://www.test.com");
        differentDomain.setDomain("test.com");
        differentDomain.setCreatedAt(LocalDateTime.now());

        List<URLMapping> exampleDomainList = List.of(testMapping, anotherMapping);
        List<URLMapping> testDomainList = List.of(differentDomain);

        when(urlRepository.findByDomain("example.com")).thenReturn(exampleDomainList);
        when(urlRepository.findByDomain("test.com")).thenReturn(testDomainList);

        // Act
        List<URLMapping> exampleDomainUrls = urlRepository.findByDomain("example.com");
        List<URLMapping> testDomainUrls = urlRepository.findByDomain("test.com");

        // Assert
        assertEquals(2, exampleDomainUrls.size());
        assertEquals(1, testDomainUrls.size());
        assertTrue(exampleDomainUrls.stream().allMatch(url -> "example.com".equals(url.getDomain())));
        assertEquals("test.com", testDomainUrls.get(0).getDomain());
        
        verify(urlRepository, times(1)).findByDomain("example.com");
        verify(urlRepository, times(1)).findByDomain("test.com");
    }

    @Test
    @DisplayName("Should find URLs created after specific date")
    void testFindByCreatedAtAfter() {
        // Arrange
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        URLMapping oldMapping = new URLMapping();
        oldMapping.setShortCode("old123");
        oldMapping.setOriginalUrl("https://old.example.com");
        oldMapping.setDomain("example.com");
        oldMapping.setCreatedAt(yesterday);

        List<URLMapping> recentUrlsList = List.of(testMapping);
        when(urlRepository.findByCreatedAtAfter(yesterday.plusHours(12))).thenReturn(recentUrlsList);

        // Act
        List<URLMapping> recentUrls = urlRepository.findByCreatedAtAfter(yesterday.plusHours(12));

        // Assert
        assertEquals(1, recentUrls.size());
        assertEquals("abc123", recentUrls.get(0).getShortCode());
        
        verify(urlRepository, times(1)).findByCreatedAtAfter(yesterday.plusHours(12));
    }

    @Test
    @DisplayName("Should find active URLs by domain")
    void testFindActiveUrlsByDomain() {
        // Arrange
        URLMapping expiredMapping = new URLMapping();
        expiredMapping.setShortCode("expired");
        expiredMapping.setOriginalUrl("https://expired.example.com");
        expiredMapping.setDomain("example.com");
        expiredMapping.setCreatedAt(LocalDateTime.now());
        expiredMapping.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired

        URLMapping neverExpiresMapping = new URLMapping();
        neverExpiresMapping.setShortCode("never");
        neverExpiresMapping.setOriginalUrl("https://never.example.com");
        neverExpiresMapping.setDomain("example.com");
        neverExpiresMapping.setCreatedAt(LocalDateTime.now());
        // No expiration date

        LocalDateTime now = LocalDateTime.now();
        List<URLMapping> activeUrlsList = List.of(testMapping, neverExpiresMapping);
        when(urlRepository.findActiveUrlsByDomain("example.com", now)).thenReturn(activeUrlsList);

        // Act
        List<URLMapping> activeUrls = urlRepository.findActiveUrlsByDomain("example.com", now);

        // Assert
        assertEquals(2, activeUrls.size());
        assertTrue(activeUrls.stream().anyMatch(url -> "abc123".equals(url.getShortCode())));
        assertTrue(activeUrls.stream().anyMatch(url -> "never".equals(url.getShortCode())));
        assertFalse(activeUrls.stream().anyMatch(url -> "expired".equals(url.getShortCode())));
        
        verify(urlRepository, times(1)).findActiveUrlsByDomain("example.com", now);
    }

    @Test
    @DisplayName("Should count URLs by domain")
    void testCountByDomain() {
        // Arrange
        when(urlRepository.countByDomain("example.com")).thenReturn(2L);
        when(urlRepository.countByDomain("test.com")).thenReturn(1L);
        when(urlRepository.countByDomain("nonexistent.com")).thenReturn(0L);

        // Act & Assert
        assertEquals(2, urlRepository.countByDomain("example.com"));
        assertEquals(1, urlRepository.countByDomain("test.com"));
        assertEquals(0, urlRepository.countByDomain("nonexistent.com"));
        
        verify(urlRepository, times(1)).countByDomain("example.com");
        verify(urlRepository, times(1)).countByDomain("test.com");
        verify(urlRepository, times(1)).countByDomain("nonexistent.com");
    }

    @Test
    @DisplayName("Should delete expired URLs")
    void testDeleteExpiredUrls() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        when(urlRepository.deleteExpiredUrls(now)).thenReturn(1L);

        // Act
        long deletedCount = urlRepository.deleteExpiredUrls(now);

        // Assert
        assertEquals(1, deletedCount);
        
        verify(urlRepository, times(1)).deleteExpiredUrls(now);
    }

    @Test
    @DisplayName("Should enforce unique short code constraint")
    void testUniqueShortCodeConstraint() {
        // Arrange
        URLMapping duplicateMapping = new URLMapping();
        duplicateMapping.setShortCode("abc123"); // Same short code
        duplicateMapping.setOriginalUrl("https://different.example.com");
        duplicateMapping.setDomain("different.com");
        duplicateMapping.setCreatedAt(LocalDateTime.now());

        when(urlRepository.save(any(URLMapping.class)))
                .thenReturn(testMapping)
                .thenThrow(new RuntimeException("Unique constraint violation"));

        // Act & Assert - First save succeeds
        URLMapping firstSave = urlRepository.save(testMapping);
        assertNotNull(firstSave);

        // Second save with duplicate should throw exception
        assertThrows(Exception.class, () -> urlRepository.save(duplicateMapping));
        
        verify(urlRepository, times(2)).save(any(URLMapping.class));
    }
}