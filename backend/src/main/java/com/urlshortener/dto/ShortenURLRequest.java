package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for URL shortening
 */
public class ShortenURLRequest {
    
    @NotBlank(message = "URL cannot be empty")
    @Pattern(
        regexp = "^https?://.*", //can potentially be improved
        message = "URL must start with http:// or https://"
    )
    private String url;
    
    @Positive(message = "Expiry days must be positive")
    private Integer expiresInDays;
    
    private String customCode;
    
    // Default constructor
    public ShortenURLRequest() {}
    
    // Constructor with required fields
    public ShortenURLRequest(String url) {
        this.url = url;
    }
    
    // Constructor with expiry
    public ShortenURLRequest(String url, Integer expiresInDays) {
        this.url = url;
        this.expiresInDays = expiresInDays;
    }
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Integer getExpiresInDays() {
        return expiresInDays;
    }
    
    public void setExpiresInDays(Integer expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
    
    public String getCustomCode() {
        return customCode;
    }
    
    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }
    
    @Override
    public String toString() {
        return "ShortenURLRequest{" +
                "url='" + url + '\'' +
                ", expiresInDays=" + expiresInDays +
                ", customCode='" + customCode + '\'' +
                '}';
    }
}