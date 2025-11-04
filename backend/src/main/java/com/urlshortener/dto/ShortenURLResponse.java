package com.urlshortener.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for URL shortening
 */
public class ShortenURLResponse {
    
    private String shortUrl;
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    // Default constructor
    public ShortenURLResponse() {}
    
    // Constructor with required fields
    public ShortenURLResponse(String shortUrl, String shortCode, String originalUrl) {
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with expiry
    public ShortenURLResponse(String shortUrl, String shortCode, String originalUrl, LocalDateTime expiresAt) {
        this(shortUrl, shortCode, originalUrl);
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getShortUrl() {
        return shortUrl;
    }
    
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @Override
    public String toString() {
        return "ShortenURLResponse{" +
                "shortUrl='" + shortUrl + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}