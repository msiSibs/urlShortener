package com.urlshortener.controller;

import com.urlshortener.dto.ShortenURLRequest;
import com.urlshortener.dto.ShortenURLResponse;
import com.urlshortener.service.URLService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for URL shortening operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Configure this properly for production
public class URLController {
    
    private static final Logger logger = LoggerFactory.getLogger(URLController.class);
    
    private final URLService urlService;
    
    @Autowired
    public URLController(URLService urlService) {
        this.urlService = urlService;
    }
    
    /**
     * Shorten a URL
     * @param request the shorten URL request
     * @return the shorten URL response
     */
    @PostMapping("/shorten")
    public ResponseEntity<ShortenURLResponse> shortenUrl(@Valid @RequestBody ShortenURLRequest request) {
        logger.info("Received shorten URL request for: {}", request.getUrl());
        
        try {
            ShortenURLResponse response = urlService.shortenUrl(request);
            logger.info("Successfully shortened URL: {} -> {}", request.getUrl(), response.getShortUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for URL shortening: {}", e.getMessage());
            throw e; // Will be handled by global exception handler
            
        } catch (Exception e) {
            logger.error("Unexpected error while shortening URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to shorten URL", e);
        }
    }
    
        /**
     * Get service statistics
     * @return statistics response
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.debug("Received request for statistics");
        
        try {
            Map<String, Object> stats = urlService.getStatistics();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve statistics", e);
        }
    }
    
    /**
     * Clean up expired URLs
     * @return cleanup result
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredUrls() {
        logger.info("Received request to cleanup expired URLs");
        
        try {
            long deletedCount = urlService.cleanupExpiredUrls();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Cleanup completed successfully");
            result.put("deletedCount", deletedCount);
            result.put("timestamp", java.time.Instant.now().toString());
            
            logger.info("Cleanup completed, deleted {} expired URLs", deletedCount);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error during cleanup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to cleanup expired URLs", e);
        }
    }
}