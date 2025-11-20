package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import services.EmailCleaningService;
import services.EmailCleaningServiceImpl;
import services.EmailIntelligenceService;
import services.EmailIntelligenceServiceImpl;
import services.intelligence.models.EmailIntelligenceProfile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete email processing pipeline
 */
@DisplayName("Integration Tests - Complete Email Pipeline")
class EmailPipelineIntegrationTest {
    
    private EmailCleaningService cleaningService;
    private EmailIntelligenceService intelligenceService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        cleaningService = new EmailCleaningServiceImpl();
        intelligenceService = new EmailIntelligenceServiceImpl();
    }
    
    @Test
    @DisplayName("Complete pipeline: Import -> Clean -> Analyze")
    void testCompletePipeline_ImportCleanAnalyze() throws Exception {
        // Step 1: Create test file with mixed quality emails
        File testFile = tempDir.resolve("test_emails.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("john.doe@gmail.com\n");
            writer.write("JANE.SMITH@YAHOO.COM\n");
            writer.write("john.doe@gmail.com\n");  // duplicate
            writer.write("invalid.email.com\n");   // no @
            writer.write("info@company.com\n");    // role-based
            writer.write("user@@test.com\n");      // multiple @
            writer.write("  developer@github.com  \n");  // whitespace
            writer.write("\n");                     // empty line
            writer.write("noreply@service.com\n"); // admin/bot
        }
        
        // Step 2: Import emails
        var importedEmails = cleaningService.importData(testFile);
        assertTrue(importedEmails.size() > 0, "Should import emails");
        
        // Step 3: Clean the email list
        List<String> emails = importedEmails;
        emails = cleaningService.removeEmptyLines(emails);
        emails = cleaningService.trimAndNormalize(emails);
        emails = cleaningService.removeDuplicates(emails);
        emails = cleaningService.removeEmailsWithoutAt(emails);
        emails = cleaningService.removeEmailsWithMultipleAt(emails);
        emails = cleaningService.removeRoleBasedEmails(emails);
        emails = cleaningService.removeAdminOrBotEmails(emails);
        
        // Should have 3 clean emails: john.doe@gmail.com, jane.smith@yahoo.com, developer@github.com
        assertEquals(3, emails.size());
        assertTrue(emails.contains("john.doe@gmail.com"));
        assertTrue(emails.contains("jane.smith@yahoo.com"));
        assertTrue(emails.contains("developer@github.com"));
        
        // Step 4: Analyze the cleaned emails
        var analysisFuture = intelligenceService.analyzeEmails(emails);
        List<EmailIntelligenceProfile> profiles = analysisFuture.get(30, TimeUnit.SECONDS);
        
        assertEquals(3, profiles.size());
        
        // Step 5: Generate HTML report
        String htmlReport = intelligenceService.generateHTMLReport(profiles);
        assertNotNull(htmlReport);
        assertFalse(htmlReport.isEmpty());
        assertTrue(htmlReport.contains("john.doe@gmail.com"));
        assertTrue(htmlReport.contains("jane.smith@yahoo.com"));
        assertTrue(htmlReport.contains("developer@github.com"));
    }
    
    @Test
    @DisplayName("Marketing flow: Import emails -> Analyze independently")
    void testMarketingFlow_IndependentAnalysis() throws Exception {
        // This simulates the marketing page workflow
        
        // Step 1: Create separate email list for marketing
        File marketingFile = tempDir.resolve("marketing_emails.txt").toFile();
        try (FileWriter writer = new FileWriter(marketingFile)) {
            writer.write("customer1@example.com\n");
            writer.write("customer2@test.com\n");
            writer.write("customer3@demo.com\n");
        }
        
        // Step 2: Import emails independently (not using cleaning service)
        List<String> marketingEmails = Arrays.asList(
            "customer1@example.com",
            "customer2@test.com",
            "customer3@demo.com"
        );
        
        // Step 3: Analyze directly without cleaning
        var future = intelligenceService.analyzeEmails(marketingEmails);
        List<EmailIntelligenceProfile> profiles = future.get(30, TimeUnit.SECONDS);
        
        assertEquals(3, profiles.size());
        
        // Step 4: Generate report
        String report = intelligenceService.generateHTMLReport(profiles);
        assertNotNull(report);
        assertTrue(report.contains("customer1@example.com"));
        assertTrue(report.contains("customer2@test.com"));
        assertTrue(report.contains("customer3@demo.com"));
    }
    
    @Test
    @DisplayName("Cleaning flow: Import -> Clean -> Export")
    void testCleaningFlow_ImportCleanExport() throws Exception {
        // Step 1: Create test file
        File inputFile = tempDir.resolve("input_emails.txt").toFile();
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("valid1@test.com\n");
            writer.write("valid2@test.com\n");
            writer.write("valid1@test.com\n");  // duplicate
            writer.write("info@company.com\n"); // role-based
        }
        
        // Step 2: Import
        var imported = cleaningService.importData(inputFile);
        assertEquals(4, imported.size());
        
        // Step 3: Clean
        List<String> cleaned = imported;
        cleaned = cleaningService.removeDuplicates(cleaned);
        cleaned = cleaningService.removeRoleBasedEmails(cleaned);
        
        assertEquals(2, cleaned.size());
        
        // Step 4: Export (simulate)
        File outputFile = tempDir.resolve("output_emails.txt").toFile();
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (String email : cleaned) {
                writer.write(email + "\n");
            }
        }
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }
    
    @Test
    @DisplayName("Test service independence: Cleaning and Marketing don't interfere")
    void testServiceIndependence() throws Exception {
        // Cleaning service operations
        File cleaningFile = tempDir.resolve("cleaning.txt").toFile();
        try (FileWriter writer = new FileWriter(cleaningFile)) {
            writer.write("cleaning1@test.com\n");
            writer.write("cleaning2@test.com\n");
        }
        var cleaningEmails = cleaningService.importData(cleaningFile);
        
        // Marketing service operations (independent)
        List<String> marketingEmails = Arrays.asList(
            "marketing1@example.com",
            "marketing2@example.com"
        );
        
        // Clean the cleaning emails
        List<String> cleaned = cleaningService.removeDuplicates(cleaningEmails);
        
        // Analyze marketing emails
        var marketingFuture = intelligenceService.analyzeEmails(marketingEmails);
        
        // Both should complete successfully without interference
        assertEquals(2, cleaned.size());
        
        List<EmailIntelligenceProfile> marketingProfiles = marketingFuture.get(30, TimeUnit.SECONDS);
        assertEquals(2, marketingProfiles.size());
        
        // Verify emails remain independent
        assertFalse(cleaned.contains("marketing1@example.com"));
        assertFalse(marketingProfiles.stream()
            .anyMatch(p -> p.getEmail().equals("cleaning1@test.com")));
    }
    
    @Test
    @DisplayName("Handle large dataset through pipeline")
    void testLargeDatasetPipeline() throws Exception {
        // Create file with 100 emails
        File largeFile = tempDir.resolve("large_emails.txt").toFile();
        try (FileWriter writer = new FileWriter(largeFile)) {
            for (int i = 0; i < 100; i++) {
                writer.write("user" + i + "@example.com\n");
            }
        }
        
        // Import
        var imported = cleaningService.importData(largeFile);
        assertEquals(100, imported.size());
        
        // Clean (should be fast)
        long startTime = System.currentTimeMillis();
        List<String> cleaned = cleaningService.removeDuplicates(imported);
        long cleanTime = System.currentTimeMillis() - startTime;
        
        assertTrue(cleanTime < 1000, "Cleaning 100 emails should take less than 1 second");
        assertEquals(100, cleaned.size());
    }
    
    @Test
    @DisplayName("Error handling: Empty file")
    void testErrorHandling_EmptyFile() throws Exception {
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();
        
        var result = cleaningService.importData(emptyFile);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    @DisplayName("Error handling: All invalid emails")
    void testErrorHandling_AllInvalidEmails() throws Exception {
        File invalidFile = tempDir.resolve("invalid.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("notanemail\n");
            writer.write("also.not.valid\n");
            writer.write("missing.at.sign.com\n");
        }
        
        var imported = cleaningService.importData(invalidFile);
        List<String> cleaned = cleaningService.removeEmailsWithoutAt(imported);
        
        assertEquals(0, cleaned.size());
    }
    
    @Test
    @DisplayName("Concurrent operations: Multiple analyses at once")
    void testConcurrentOperations() throws Exception {
        List<String> batch1 = Arrays.asList("batch1a@test.com", "batch1b@test.com");
        List<String> batch2 = Arrays.asList("batch2a@test.com", "batch2b@test.com");
        List<String> batch3 = Arrays.asList("batch3a@test.com", "batch3b@test.com");
        
        // Start multiple analyses concurrently
        var future1 = intelligenceService.analyzeEmails(batch1);
        var future2 = intelligenceService.analyzeEmails(batch2);
        var future3 = intelligenceService.analyzeEmails(batch3);
        
        // All should complete successfully
        var profiles1 = future1.get(30, TimeUnit.SECONDS);
        var profiles2 = future2.get(30, TimeUnit.SECONDS);
        var profiles3 = future3.get(30, TimeUnit.SECONDS);
        
        assertEquals(2, profiles1.size());
        assertEquals(2, profiles2.size());
        assertEquals(2, profiles3.size());
    }
}
