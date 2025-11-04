package com.urlshortener.repository;

import com.urlshortener.model.URLMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface URLRepository extends MongoRepository<URLMapping, String> {
    
    /**
     * Find a URL mapping by short code
     * @param shortCode the short code to search for
     * @return Optional containing the URLMapping if found
     */
    Optional<URLMapping> findByShortCode(String shortCode);
    
    /**
     * Check if a short code already exists
     * @param shortCode the short code to check
     * @return true if exists, false otherwise
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * Find all URL mappings by domain
     * @param domain the domain to search for
     * @return List of URLMappings for the domain
     */
    List<URLMapping> findByDomain(String domain);
    
    /**
     * Find URLs created after a specific date
     * @param createdAt the date threshold
     * @return List of URLMappings created after the date
     */
    List<URLMapping> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * Find non-expired URLs for a domain
     * @param domain the domain to search for
     * @return List of non-expired URLMappings
     */
    @Query("{ 'domain': ?0, '$or': [ { 'expiresAt': null }, { 'expiresAt': { '$gt': ?1 } } ] }")
    List<URLMapping> findActiveUrlsByDomain(String domain, LocalDateTime now);
    
    /**
     * Count URLs created by domain
     * @param domain the domain to count for
     * @return count of URLs for the domain
     */
    long countByDomain(String domain);
    
    /**
     * Delete expired URLs
     * @param now current timestamp
     * @return number of deleted documents
     */
    @Query(value = "{ 'expiresAt': { '$lt': ?0 } }", delete = true)
    long deleteExpiredUrls(LocalDateTime now);
}