package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for EmailCleaningServiceImpl
 */
@DisplayName("EmailCleaningService Tests")
class EmailCleaningServiceImplTest {
    
    private EmailCleaningService service;
    
    @BeforeEach
    void setUp() {
        service = new EmailCleaningServiceImpl();
    }
    
    // =============== Import Data Tests ===============
    
    @Test
    @DisplayName("Import valid emails from file")
    void testImportData_ValidFile() throws IOException {
        // Create temporary file with test data
        File tempFile = Files.createTempFile("test_emails", ".txt").toFile();
        tempFile.deleteOnExit();
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("test1@example.com\n");
            writer.write("test2@example.com\n");
            writer.write("test3@example.com\n");
        }
        
        var result = service.importData(tempFile);
        
        assertEquals(3, result.size());
        assertTrue(result.contains("test1@example.com"));
        assertTrue(result.contains("test2@example.com"));
        assertTrue(result.contains("test3@example.com"));
    }
    
    @Test
    @DisplayName("Import from null file returns empty list")
    void testImportData_NullFile() {
        var result = service.importData(null);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    @DisplayName("Import from non-existent file returns empty list")
    void testImportData_NonExistentFile() {
        File nonExistent = new File("nonexistent_file.txt");
        var result = service.importData(nonExistent);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    @DisplayName("Import ignores empty lines")
    void testImportData_IgnoresEmptyLines() throws IOException {
        File tempFile = Files.createTempFile("test_emails", ".txt").toFile();
        tempFile.deleteOnExit();
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("test1@example.com\n");
            writer.write("\n");
            writer.write("  \n");
            writer.write("test2@example.com\n");
        }
        
        var result = service.importData(tempFile);
        assertEquals(2, result.size());
    }
    
    // =============== Remove Duplicates Tests ===============
    
    @Test
    @DisplayName("Remove duplicate emails while preserving order")
    void testRemoveDuplicates_PreservesOrder() {
        List<String> emails = Arrays.asList(
            "first@example.com",
            "second@example.com",
            "first@example.com",
            "third@example.com",
            "second@example.com"
        );
        
        List<String> result = service.removeDuplicates(emails);
        
        assertEquals(3, result.size());
        assertEquals("first@example.com", result.get(0));
        assertEquals("second@example.com", result.get(1));
        assertEquals("third@example.com", result.get(2));
    }
    
    @Test
    @DisplayName("Remove duplicates handles empty list")
    void testRemoveDuplicates_EmptyList() {
        List<String> result = service.removeDuplicates(Arrays.asList());
        assertEquals(0, result.size());
    }
    
    @Test
    @DisplayName("Remove duplicates with no duplicates")
    void testRemoveDuplicates_NoDuplicates() {
        List<String> emails = Arrays.asList("a@test.com", "b@test.com", "c@test.com");
        List<String> result = service.removeDuplicates(emails);
        assertEquals(3, result.size());
    }
    
    // =============== Remove Empty Lines Tests ===============
    
    @Test
    @DisplayName("Remove empty lines")
    void testRemoveEmptyLines() {
        List<String> emails = Arrays.asList(
            "test@example.com",
            "",
            "  ",
            "another@example.com",
            null
        );
        
        List<String> result = service.removeEmptyLines(emails);
        
        assertEquals(2, result.size());
        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("another@example.com"));
    }
    
    @Test
    @DisplayName("Remove empty lines handles all empty")
    void testRemoveEmptyLines_AllEmpty() {
        List<String> emails = Arrays.asList("", "  ", null);
        List<String> result = service.removeEmptyLines(emails);
        assertEquals(0, result.size());
    }
    
    // =============== Trim and Normalize Tests ===============
    
    @Test
    @DisplayName("Trim and normalize emails")
    void testTrimAndNormalize() {
        List<String> emails = Arrays.asList(
            "  TEST@EXAMPLE.COM  ",
            "User@Test.com",
            "lowercase@example.com"
        );
        
        List<String> result = service.trimAndNormalize(emails);
        
        assertEquals(3, result.size());
        assertEquals("test@example.com", result.get(0));
        assertEquals("user@test.com", result.get(1));
        assertEquals("lowercase@example.com", result.get(2));
    }
    
    // =============== Remove Emails Without @ Tests ===============
    
    @Test
    @DisplayName("Remove emails without @ symbol")
    void testRemoveEmailsWithoutAt() {
        List<String> emails = Arrays.asList(
            "valid@example.com",
            "invalidemail.com",
            "another@test.com",
            "alsobadexample.com"
        );
        
        List<String> result = service.removeEmailsWithoutAt(emails);
        
        assertEquals(2, result.size());
        assertTrue(result.contains("valid@example.com"));
        assertTrue(result.contains("another@test.com"));
    }
    
    // =============== Remove Emails With Multiple @ Tests ===============
    
    @Test
    @DisplayName("Remove emails with multiple @ symbols")
    void testRemoveEmailsWithMultipleAt() {
        List<String> emails = Arrays.asList(
            "valid@example.com",
            "invalid@@example.com",
            "user@test@domain.com",
            "correct@site.com"
        );
        
        List<String> result = service.removeEmailsWithMultipleAt(emails);
        
        assertEquals(2, result.size());
        assertTrue(result.contains("valid@example.com"));
        assertTrue(result.contains("correct@site.com"));
    }
    
    // =============== Remove Invalid Characters Tests ===============
    
    @Test
    @DisplayName("Remove emails with invalid characters")
    void testRemoveInvalidCharacters() {
        List<String> emails = Arrays.asList(
            "valid@example.com",
            "user name@example.com",
            "user,test@domain.com",
            "valid.user+tag@test.com"
        );
        
        List<String> result = service.removeInvalidCharacters(emails);
        
        // Should keep valid emails (including ones with dots, plus, etc.)
        assertTrue(result.contains("valid@example.com"));
        assertTrue(result.contains("valid.user+tag@test.com"));
        assertFalse(result.contains("user name@example.com"));
        assertFalse(result.contains("user,test@domain.com"));
    }
    
    // =============== Remove Role-Based Emails Tests ===============
    
    @ParameterizedTest
    @ValueSource(strings = {
        "info@example.com",
        "support@test.com",
        "sales@company.com",
        "contact@site.com",
        "webmaster@test.com",
        "help@domain.com"
    })
    @DisplayName("Remove role-based emails")
    void testRemoveRoleBasedEmails_RoleEmails(String email) {
        List<String> emails = Arrays.asList(email, "john.doe@example.com");
        List<String> result = service.removeRoleBasedEmails(emails);
        
        assertEquals(1, result.size());
        assertEquals("john.doe@example.com", result.get(0));
    }
    
    @Test
    @DisplayName("Keep valid personal emails when removing role-based")
    void testRemoveRoleBasedEmails_KeepsPersonal() {
        List<String> emails = Arrays.asList(
            "john.doe@example.com",
            "info@example.com",
            "jane.smith@test.com"
        );
        
        List<String> result = service.removeRoleBasedEmails(emails);
        
        assertEquals(2, result.size());
        assertTrue(result.contains("john.doe@example.com"));
        assertTrue(result.contains("jane.smith@test.com"));
    }
    
    // =============== Remove Admin/Bot Emails Tests ===============
    
    @ParameterizedTest
    @ValueSource(strings = {
        "noreply@example.com",
        "no-reply@test.com",
        "admin@company.com",
        "bot@site.com",
        "do-not-reply@domain.com"
    })
    @DisplayName("Remove admin and bot emails")
    void testRemoveAdminOrBotEmails_AdminBotEmails(String email) {
        List<String> emails = Arrays.asList(email, "user@example.com");
        List<String> result = service.removeAdminOrBotEmails(emails);
        
        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0));
    }
    
    // =============== Remove Disposable Emails Tests ===============
    
    @ParameterizedTest
    @ValueSource(strings = {
        "user@mailinator.com",
        "test@yopmail.com",
        "temp@10minutemail.com",
        "fake@guerrillamail.com"
    })
    @DisplayName("Remove disposable email addresses")
    void testRemoveDisposableEmails_DisposableDomains(String email) {
        List<String> emails = Arrays.asList(email, "real@gmail.com");
        List<String> result = service.removeDisposableEmails(emails);
        
        // Should keep only the real email
        assertTrue(result.contains("real@gmail.com"));
        assertFalse(result.contains(email));
    }
    
    // =============== Remove Invalid TLDs Tests ===============
    
    @Test
    @DisplayName("Remove emails with invalid TLDs")
    void testRemoveInvalidOrFakeTLDs() {
        List<String> emails = Arrays.asList(
            "user@domain.com",
            "user@test.xyz",
            "user@fake.x",
            "user@valid.org"
        );
        
        List<String> result = service.removeInvalidOrFakeTLDs(emails);
        
        // Should keep valid TLDs
        assertTrue(result.contains("user@domain.com"));
        assertTrue(result.contains("user@valid.org"));
    }
    
    // =============== Edge Cases and Integration Tests ===============
    
    @Test
    @DisplayName("Handle empty input list")
    void testEmptyList_AllMethods() {
        List<String> empty = Arrays.asList();
        
        assertEquals(0, service.removeDuplicates(empty).size());
        assertEquals(0, service.removeEmptyLines(empty).size());
        assertEquals(0, service.trimAndNormalize(empty).size());
        assertEquals(0, service.removeEmailsWithoutAt(empty).size());
        assertEquals(0, service.removeInvalidCharacters(empty).size());
    }
    
    @Test
    @DisplayName("Complete cleaning pipeline")
    void testCompleteCleaningPipeline() {
        List<String> emails = Arrays.asList(
            "  VALID@EXAMPLE.COM  ",
            "valid@example.com",  // duplicate
            "",
            "invalid.email.com",
            "user@@test.com",
            "good.user@test.com",
            "support@company.com",  // role-based, not admin
            null
        );
        
        // Apply cleaning pipeline
        List<String> result = emails;
        result = service.removeEmptyLines(result);
        result = service.trimAndNormalize(result);
        result = service.removeDuplicates(result);
        result = service.removeEmailsWithoutAt(result);
        result = service.removeEmailsWithMultipleAt(result);
        result = service.removeRoleBasedEmails(result);
        
        // Should end up with valid@example.com and good.user@test.com
        // (both are valid, non-role emails)
        assertEquals(2, result.size());
        assertTrue(result.contains("valid@example.com"));
        assertTrue(result.contains("good.user@test.com"));
    }
    
    @Test
    @DisplayName("Test large list performance")
    void testLargeListPerformance() {
        // Create a list with 1000 emails
        List<String> largeList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add("user" + i + "@example.com");
        }
        
        // Should handle large lists efficiently
        long startTime = System.currentTimeMillis();
        List<String> result = service.removeDuplicates(largeList);
        long endTime = System.currentTimeMillis();
        
        assertEquals(1000, result.size());
        assertTrue((endTime - startTime) < 1000, "Should process 1000 emails in less than 1 second");
    }
    
    @Test
    @DisplayName("Test concurrent modifications safety")
    void testConcurrentModificationsSafety() {
        List<String> emails = Arrays.asList(
            "test1@example.com",
            "test2@example.com",
            "test3@example.com"
        );
        
        // Original list should remain unchanged
        List<String> result = service.removeDuplicates(emails);
        assertEquals(3, emails.size()); // Original unchanged
        assertEquals(3, result.size()); // Result has correct size
    }
}
