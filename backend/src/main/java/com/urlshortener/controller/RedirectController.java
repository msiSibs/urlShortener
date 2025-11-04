package com.urlshortener.controller;

import com.urlshortener.service.URLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for URL redirection
 */
@RestController
@CrossOrigin(origins = "*") // Will configure correclty for production
public class RedirectController {
    
    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);
    
    private final URLService urlService;
    
    @Autowired
    public RedirectController(URLService urlService) {
        this.urlService = urlService;
    }
    
    /**
     * Redirect to original URL using short code
     * @param shortCode the short code
     * @return redirect response
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        logger.info("Received redirect request for short code: {}", shortCode);
        
        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", originalUrl);
            
            logger.info("Redirecting {} to {}", shortCode, originalUrl);
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid short code: {}", shortCode);
            throw e;
            
        } catch (Exception e) {
            logger.error("Unexpected error during redirect for short code {}: {}", shortCode, e.getMessage(), e);
            throw new RuntimeException("Failed to redirect", e);
        }
    }
    
    /**
     * Get information about a short code without redirecting
     * @param shortCode the short code
     * @return URL information
     */
    @GetMapping("/info/{shortCode}")
    public ResponseEntity<Map<String, Object>> getUrlInfo(@PathVariable String shortCode) {
        logger.debug("Received info request for short code: {}", shortCode);
        
        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            
            Map<String, Object> info = new HashMap<>();
            info.put("shortCode", shortCode);
            info.put("originalUrl", originalUrl);
            info.put("status", "active");
            info.put("timestamp", java.time.Instant.now().toString());
            
            return ResponseEntity.ok(info);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid short code for info request: {}", shortCode);
            throw e;
            
        } catch (Exception e) {
            logger.error("Unexpected error retrieving info for short code {}: {}", shortCode, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve URL info", e);
        }
    }
}