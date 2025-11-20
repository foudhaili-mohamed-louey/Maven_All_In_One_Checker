package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import services.intelligence.models.EmailIntelligenceProfile;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailIntelligenceServiceImpl
 */
@DisplayName("EmailIntelligenceService Tests")
class EmailIntelligenceServiceImplTest {
    
    private EmailIntelligenceService service;
    
    @BeforeEach
    void setUp() {
        service = new EmailIntelligenceServiceImpl();
    }
    
    // =============== Single Email Analysis Tests ===============
    
    @Test
    @DisplayName("Analyze single valid email")
    void testAnalyzeEmail_ValidEmail() throws Exception {
        String email = "test@example.com";
        
        CompletableFuture<EmailIntelligenceProfile> future = service.analyzeEmail(email);
        EmailIntelligenceProfile profile = future.get(10, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        assertEquals(email, profile.getEmail());
    }
    
    @Test
    @DisplayName("Analyze email with common domain")
    void testAnalyzeEmail_CommonDomain() throws Exception {
        String email = "user@gmail.com";
        
        CompletableFuture<EmailIntelligenceProfile> future = service.analyzeEmail(email);
        EmailIntelligenceProfile profile = future.get(10, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        assertEquals(email, profile.getEmail());
    }
    
    @Test
    @DisplayName("Handle null email gracefully")
    void testAnalyzeEmail_NullEmail() {
        // Service should handle null gracefully, not throw exception
        CompletableFuture<EmailIntelligenceProfile> future = service.analyzeEmail(null);
        assertDoesNotThrow(() -> {
            EmailIntelligenceProfile profile = future.get(5, TimeUnit.SECONDS);
            // Should return a profile (possibly with minimal data)
            assertNotNull(profile);
        });
    }
    
    @Test
    @DisplayName("Handle empty email gracefully")
    void testAnalyzeEmail_EmptyEmail() {
        // Service should handle empty string gracefully, not throw exception
        CompletableFuture<EmailIntelligenceProfile> future = service.analyzeEmail("");
        assertDoesNotThrow(() -> {
            EmailIntelligenceProfile profile = future.get(5, TimeUnit.SECONDS);
            // Should return a profile (possibly with minimal data)
            assertNotNull(profile);
        });
    }
    
    // =============== Multiple Emails Analysis Tests ===============
    
    @Test
    @DisplayName("Analyze multiple emails concurrently")
    void testAnalyzeEmails_MultipleValid() throws Exception {
        List<String> emails = Arrays.asList(
            "user1@example.com",
            "user2@test.com",
            "user3@demo.com"
        );
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(15, TimeUnit.SECONDS);
        
        assertNotNull(profiles);
        assertEquals(3, profiles.size());
        
        // Verify each email was analyzed
        assertTrue(profiles.stream().anyMatch(p -> p.getEmail().equals("user1@example.com")));
        assertTrue(profiles.stream().anyMatch(p -> p.getEmail().equals("user2@test.com")));
        assertTrue(profiles.stream().anyMatch(p -> p.getEmail().equals("user3@demo.com")));
    }
    
    @Test
    @DisplayName("Handle empty email list")
    void testAnalyzeEmails_EmptyList() throws Exception {
        List<String> emails = Arrays.asList();
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(5, TimeUnit.SECONDS);
        
        assertNotNull(profiles);
        assertEquals(0, profiles.size());
    }
    
    @Test
    @DisplayName("Analyze single email in list")
    void testAnalyzeEmails_SingleEmail() throws Exception {
        List<String> emails = Arrays.asList("single@example.com");
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(10, TimeUnit.SECONDS);
        
        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertEquals("single@example.com", profiles.get(0).getEmail());
    }
    
    @Test
    @DisplayName("Handle large batch of emails")
    void testAnalyzeEmails_LargeBatch() throws Exception {
        List<String> emails = Arrays.asList(
            "user1@example.com",
            "user2@example.com",
            "user3@example.com",
            "user4@example.com",
            "user5@example.com",
            "user6@example.com",
            "user7@example.com",
            "user8@example.com",
            "user9@example.com",
            "user10@example.com"
        );
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(30, TimeUnit.SECONDS);
        
        assertNotNull(profiles);
        assertEquals(10, profiles.size());
    }
    
    // =============== HTML Report Generation Tests ===============
    
    @Test
    @DisplayName("Generate HTML report from profiles")
    void testGenerateHTMLReport_ValidProfiles() throws Exception {
        List<String> emails = Arrays.asList("test@example.com");
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(10, TimeUnit.SECONDS);
        
        String html = service.generateHTMLReport(profiles);
        
        assertNotNull(html);
        assertFalse(html.isEmpty());
        assertTrue(html.contains("<!DOCTYPE html>") || html.contains("<html"));
        assertTrue(html.contains("test@example.com"));
    }
    
    @Test
    @DisplayName("Generate HTML report from empty list")
    void testGenerateHTMLReport_EmptyList() {
        List<EmailIntelligenceProfile> emptyProfiles = Arrays.asList();
        
        String html = service.generateHTMLReport(emptyProfiles);
        
        assertNotNull(html);
        assertFalse(html.isEmpty());
        assertTrue(html.contains("<!DOCTYPE html>") || html.contains("<html"));
    }
    
    @Test
    @DisplayName("Generate HTML report with multiple profiles")
    void testGenerateHTMLReport_MultipleProfiles() throws Exception {
        List<String> emails = Arrays.asList(
            "user1@example.com",
            "user2@test.com"
        );
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(15, TimeUnit.SECONDS);
        
        String html = service.generateHTMLReport(profiles);
        
        assertNotNull(html);
        assertTrue(html.contains("user1@example.com"));
        assertTrue(html.contains("user2@test.com"));
    }
    
    // =============== Async Behavior Tests ===============
    
    @Test
    @DisplayName("Verify async analysis completes")
    void testAsyncAnalysis_Completes() throws Exception {
        List<String> emails = Arrays.asList("async@example.com");
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        
        // Should complete eventually
        List<EmailIntelligenceProfile> profiles = future.get(10, TimeUnit.SECONDS);
        assertTrue(future.isDone());
        assertNotNull(profiles);
    }
    
    @Test
    @DisplayName("Verify concurrent analysis efficiency")
    void testConcurrentAnalysis_Efficiency() throws Exception {
        List<String> emails = Arrays.asList(
            "concurrent1@example.com",
            "concurrent2@example.com",
            "concurrent3@example.com"
        );
        
        long startTime = System.currentTimeMillis();
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = future.get(20, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        // Concurrent analysis should be reasonably fast
        assertTrue((endTime - startTime) < 20000, "Analysis should complete within 20 seconds");
        assertEquals(3, profiles.size());
    }
    
    // =============== Error Handling Tests ===============
    
    @Test
    @DisplayName("Handle null email in list")
    void testAnalyzeEmails_WithNullEmail() throws Exception {
        List<String> emails = Arrays.asList(
            "valid@example.com",
            null,
            "another@test.com"
        );
        
        // Should handle null gracefully and analyze valid emails
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        
        // Should not throw exception
        assertDoesNotThrow(() -> future.get(10, TimeUnit.SECONDS));
    }
    
    @Test
    @DisplayName("Handle mixed valid and invalid emails")
    void testAnalyzeEmails_MixedValidity() throws Exception {
        List<String> emails = Arrays.asList(
            "valid@example.com",
            "",
            "another@test.com",
            "   "
        );
        
        CompletableFuture<List<EmailIntelligenceProfile>> future = service.analyzeEmails(emails);
        
        // Should complete without throwing
        assertDoesNotThrow(() -> future.get(10, TimeUnit.SECONDS));
    }
}
