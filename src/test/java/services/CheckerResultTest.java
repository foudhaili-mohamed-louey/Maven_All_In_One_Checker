package services;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CheckerResult
 */
class CheckerResultTest {
    
    @Test
    void testConstructorAndGetters() {
        String email = "test@example.com";
        boolean isValid = true;
        String status = "Valid";
        
        CheckerResult result = new CheckerResult(email, isValid, status);
        
        assertEquals(email, result.getEmail());
        assertTrue(result.isValid());
        assertEquals(status, result.getStatus());
    }
    
    @Test
    void testGetValidEmails() {
        List<CheckerResult> results = Arrays.asList(
            new CheckerResult("valid1@example.com", true, "Valid"),
            new CheckerResult("invalid1@example.com", false, "Invalid"),
            new CheckerResult("valid2@example.com", true, "Valid"),
            new CheckerResult("invalid2@example.com", false, "Invalid")
        );
        
        List<String> validEmails = CheckerResult.getValidEmails(results);
        
        assertEquals(2, validEmails.size());
        assertTrue(validEmails.contains("valid1@example.com"));
        assertTrue(validEmails.contains("valid2@example.com"));
        assertFalse(validEmails.contains("invalid1@example.com"));
        assertFalse(validEmails.contains("invalid2@example.com"));
    }
    
    @Test
    void testGetInvalidEmails() {
        List<CheckerResult> results = Arrays.asList(
            new CheckerResult("valid1@example.com", true, "Valid"),
            new CheckerResult("invalid1@example.com", false, "Invalid"),
            new CheckerResult("valid2@example.com", true, "Valid"),
            new CheckerResult("invalid2@example.com", false, "Invalid")
        );
        
        List<String> invalidEmails = CheckerResult.getInvalidEmails(results);
        
        assertEquals(2, invalidEmails.size());
        assertTrue(invalidEmails.contains("invalid1@example.com"));
        assertTrue(invalidEmails.contains("invalid2@example.com"));
        assertFalse(invalidEmails.contains("valid1@example.com"));
        assertFalse(invalidEmails.contains("valid2@example.com"));
    }
    
    @Test
    void testGetValidEmailsEmpty() {
        List<CheckerResult> results = Arrays.asList(
            new CheckerResult("invalid1@example.com", false, "Invalid"),
            new CheckerResult("invalid2@example.com", false, "Invalid")
        );
        
        List<String> validEmails = CheckerResult.getValidEmails(results);
        
        assertTrue(validEmails.isEmpty());
    }
    
    @Test
    void testGetInvalidEmailsEmpty() {
        List<CheckerResult> results = Arrays.asList(
            new CheckerResult("valid1@example.com", true, "Valid"),
            new CheckerResult("valid2@example.com", true, "Valid")
        );
        
        List<String> invalidEmails = CheckerResult.getInvalidEmails(results);
        
        assertTrue(invalidEmails.isEmpty());
    }
}
