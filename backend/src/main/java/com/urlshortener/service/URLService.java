package com.urlshortener.service;

import com.urlshortener.dto.ShortenURLRequest;
import com.urlshortener.dto.ShortenURLResponse;
import com.urlshortener.model.URLMapping;
import com.urlshortener.repository.URLRepository;
import com.urlshortener.util.Base62Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for URL shortening operations
 */
@Service
public class URLService {
    
    private static final Logger logger = LoggerFactory.getLogger(URLService.class);
    
    private final URLRepository urlRepository;
    private final Base62Encoder base62Encoder;
    private final Random random;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${app.short-code-length:6}")
    private int shortCodeLength;
    
    @Value("${app.default-expiry-days:7}")
    private int defaultExpiryDays;
    
    private static final int MAX_RETRY_ATTEMPTS = 5;
    
    @Autowired
    public URLService(URLRepository urlRepository, Base62Encoder base62Encoder) {
        this.urlRepository = urlRepository;
        this.base62Encoder = base62Encoder;
        this.random = new Random();
    }
    
    /**
     * Shorten a URL
     * @param request the shorten URL request
     * @return the shorten URL response
     */
    public ShortenURLResponse shortenUrl(ShortenURLRequest request) {
        logger.info("Shortening URL: {}", request.getUrl());
        
        // Validate URL
        if (!isValidUrl(request.getUrl())) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        // Calculate expiry date
        LocalDateTime expiresAt = null;
        if (request.getExpiresInDays() != null && request.getExpiresInDays() > 0) {
            expiresAt = LocalDateTime.now().plusDays(request.getExpiresInDays());
        } else if (defaultExpiryDays > 0) {
            expiresAt = LocalDateTime.now().plusDays(defaultExpiryDays);
        }
        
        // Generate short code
        String shortCode;
        if (request.getCustomCode() != null && !request.getCustomCode().trim().isEmpty()) {
            shortCode = request.getCustomCode().trim();
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new IllegalArgumentException("Custom short code already exists: " + shortCode);
            }
        } else {
            shortCode = generateUniqueShortCode();
        }
        
        // Extract domain from base URL
        String domain = extractDomain(baseUrl);
        
        // Create and save URL mapping
        URLMapping urlMapping = new URLMapping(shortCode, request.getUrl(), domain, expiresAt);
        urlMapping = urlRepository.save(urlMapping);
        
        // Build response
        String shortUrl = baseUrl + "/" + shortCode;
        ShortenURLResponse response = new ShortenURLResponse(
            shortUrl, 
            shortCode, 
            request.getUrl(), 
            expiresAt
        );
        response.setCreatedAt(urlMapping.getCreatedAt());
        
        logger.info("Successfully created short URL: {} for original: {}", shortUrl, request.getUrl());
        return response;
    }
    
    /**
     * Get original URL by short code
     * @param shortCode the short code
     * @return the original URL
     */
    public String getOriginalUrl(String shortCode) {
        logger.debug("Looking up original URL for short code: {}", shortCode);
        
        Optional<URLMapping> urlMapping = urlRepository.findByShortCode(shortCode);
        
        if (urlMapping.isEmpty()) {
            logger.warn("Short code not found: {}", shortCode);
            throw new IllegalArgumentException("Short code not found: " + shortCode);
        }
        
        URLMapping mapping = urlMapping.get();
        
        // Check if URL has expired
        if (mapping.isExpired()) {
            logger.warn("Short code has expired: {}", shortCode);
            throw new IllegalArgumentException("Short code has expired: " + shortCode);
        }
        
        logger.debug("Found original URL: {} for short code: {}", mapping.getOriginalUrl(), shortCode);
        return mapping.getOriginalUrl();
    }
    
    /**
     * Generate a unique short code
     * @return unique short code
     */
    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        
        do {
            if (attempts >= MAX_RETRY_ATTEMPTS) {
                throw new RuntimeException("Failed to generate unique short code after " + MAX_RETRY_ATTEMPTS + " attempts");
            }
            
            // Generate random number and encode to Base62
            long randomNumber = Math.abs(random.nextLong());
            shortCode = base62Encoder.encodeWithMinLength(randomNumber, shortCodeLength);
            
            // Ensure we don't exceed the desired length
            if (shortCode.length() > shortCodeLength) {
                shortCode = shortCode.substring(0, shortCodeLength);
            }
            
            attempts++;
            
        } while (urlRepository.existsByShortCode(shortCode));
        
        logger.debug("Generated unique short code: {} after {} attempts", shortCode, attempts);
        return shortCode;
    }
    
    /**
     * Validate if URL is properly formatted
     * @param urlString the URL to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }
        
        try {
            URI uri = new URI(urlString);
            String scheme = uri.getScheme();
            return "http".equals(scheme) || "https".equals(scheme);
        } catch (URISyntaxException e) {
            logger.debug("Invalid URL format: {}", urlString);
            return false;
        }
    }
    
    /**
     * Extract domain from URL
     * @param urlString the URL
     * @return the domain
     */
    private String extractDomain(String urlString) {
        try {
            URI uri = new URI(urlString);
            return uri.getHost();
        } catch (URISyntaxException e) {
            logger.warn("Failed to extract domain from URL: {}", urlString);
            return "localhost";
        }
    }
    
    /**
     * Clean up expired URLs
     * @return number of cleaned URLs
     */
    public long cleanupExpiredUrls() {
        logger.info("Starting cleanup of expired URLs");
        long deletedCount = urlRepository.deleteExpiredUrls(LocalDateTime.now());
        logger.info("Cleaned up {} expired URLs", deletedCount);
        return deletedCount;
    }
    
    /**
     * Get URL statistics for a domain
     * @param domain the domain
     * @return count of URLs
     */
    public long getUrlCountByDomain(String domain) {
        return urlRepository.countByDomain(domain);
    }
}