package com.urlshortener.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls_shortened")
public class URLMapping {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String shortCode;
    
    private String originalUrl;
    
    private LocalDateTime createdAt;
    
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiresAt;
    
    private String domain;

    public URLMapping() {}
    
    // Constructor with required fields
    public URLMapping(String shortCode, String originalUrl, String domain) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.domain = domain;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with expiration
    public URLMapping(String shortCode, String originalUrl, String domain, LocalDateTime expiresAt) {
        this(shortCode, originalUrl, domain);
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    // Util
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    @Override
    public String toString() {
        return "URLMapping{" +
                "id='" + id + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", domain='" + domain + '\'' +
                '}';
    }
}