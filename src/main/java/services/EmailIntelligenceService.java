package services;

import services.intelligence.models.EmailIntelligenceProfile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for email intelligence analysis
 */
public interface EmailIntelligenceService {
    
    /**
     * Analyzes a single email and returns intelligence profile
     */
    CompletableFuture<EmailIntelligenceProfile> analyzeEmail(String email);
    
    /**
     * Analyzes multiple emails concurrently
     */
    CompletableFuture<List<EmailIntelligenceProfile>> analyzeEmails(List<String> emails);
    
    /**
     * Generates HTML report from profiles
     */
    String generateHTMLReport(List<EmailIntelligenceProfile> profiles);
}
