package com.urlshortener.repository;

import com.urlshortener.model.URLMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for URLRepository
 */
@DataMongoTest
@ActiveProfiles("test")
@DisplayName("URLRepository Integration Tests")
class URLRepositoryTest {

    @Autowired
    private URLRepository urlRepository;

    private URLMapping testMapping;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
        
        testMapping = new URLMapping();
        testMapping.setShortCode("abc123");
        testMapping.setOriginalUrl("https://www.example.com");
        testMapping.setDomain("example.com");
        testMapping.setCreatedAt(LocalDateTime.now());
        testMapping.setExpiresAt(LocalDateTime.now().plusDays(30));
    }

    @Test
    @DisplayName("Should save and find URL mapping by short code")
    void testSaveAndFindByShortCode() {
        // Arrange & Act
        URLMapping saved = urlRepository.save(testMapping);
        Optional<URLMapping> found = urlRepository.findByShortCode("abc123");

        // Assert
        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("abc123", found.get().getShortCode());
        assertEquals("https://www.example.com", found.get().getOriginalUrl());
        assertEquals("example.com", found.get().getDomain());
    }

    @Test
    @DisplayName("Should check if short code exists")
    void testExistsByShortCode() {
        // Arrange
        urlRepository.save(testMapping);

        // Act & Assert
        assertTrue(urlRepository.existsByShortCode("abc123"));
        assertFalse(urlRepository.existsByShortCode("nonexistent"));
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

        urlRepository.save(testMapping);
        urlRepository.save(anotherMapping);
        urlRepository.save(differentDomain);

        // Act
        List<URLMapping> exampleDomainUrls = urlRepository.findByDomain("example.com");
        List<URLMapping> testDomainUrls = urlRepository.findByDomain("test.com");

        // Assert
        assertEquals(2, exampleDomainUrls.size());
        assertEquals(1, testDomainUrls.size());
        assertTrue(exampleDomainUrls.stream().allMatch(url -> "example.com".equals(url.getDomain())));
        assertEquals("test.com", testDomainUrls.get(0).getDomain());
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

        urlRepository.save(testMapping); // Created today
        urlRepository.save(oldMapping);  // Created yesterday

        // Act
        List<URLMapping> recentUrls = urlRepository.findByCreatedAtAfter(yesterday.plusHours(12));

        // Assert
        assertEquals(1, recentUrls.size());
        assertEquals("abc123", recentUrls.get(0).getShortCode());
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

        urlRepository.save(testMapping);        // Active (expires in future)
        urlRepository.save(expiredMapping);     // Expired
        urlRepository.save(neverExpiresMapping); // Never expires

        // Act
        List<URLMapping> activeUrls = urlRepository.findActiveUrlsByDomain("example.com", LocalDateTime.now());

        // Assert
        assertEquals(2, activeUrls.size());
        assertTrue(activeUrls.stream().anyMatch(url -> "abc123".equals(url.getShortCode())));
        assertTrue(activeUrls.stream().anyMatch(url -> "never".equals(url.getShortCode())));
        assertFalse(activeUrls.stream().anyMatch(url -> "expired".equals(url.getShortCode())));
    }

    @Test
    @DisplayName("Should count URLs by domain")
    void testCountByDomain() {
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

        urlRepository.save(testMapping);
        urlRepository.save(anotherMapping);
        urlRepository.save(differentDomain);

        // Act & Assert
        assertEquals(2, urlRepository.countByDomain("example.com"));
        assertEquals(1, urlRepository.countByDomain("test.com"));
        assertEquals(0, urlRepository.countByDomain("nonexistent.com"));
    }

    @Test
    @DisplayName("Should delete expired URLs")
    void testDeleteExpiredUrls() {
        // Arrange
        URLMapping expiredMapping = new URLMapping();
        expiredMapping.setShortCode("expired");
        expiredMapping.setOriginalUrl("https://expired.example.com");
        expiredMapping.setDomain("example.com");
        expiredMapping.setCreatedAt(LocalDateTime.now());
        expiredMapping.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired

        urlRepository.save(testMapping);    // Active
        urlRepository.save(expiredMapping); // Expired

        // Act
        long deletedCount = urlRepository.deleteExpiredUrls(LocalDateTime.now());

        // Assert
        assertEquals(1, deletedCount);
        assertEquals(1, urlRepository.count()); // Only active mapping should remain
        assertTrue(urlRepository.existsByShortCode("abc123"));
        assertFalse(urlRepository.existsByShortCode("expired"));
    }

    @Test
    @DisplayName("Should enforce unique short code constraint")
    void testUniqueShortCodeConstraint() {
        // Arrange
        urlRepository.save(testMapping);

        URLMapping duplicateMapping = new URLMapping();
        duplicateMapping.setShortCode("abc123"); // Same short code
        duplicateMapping.setOriginalUrl("https://different.example.com");
        duplicateMapping.setDomain("different.com");
        duplicateMapping.setCreatedAt(LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> urlRepository.save(duplicateMapping));
    }
}