package services;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CheckerService with all components working together
 */
class CheckerServiceIntegrationTest {
    
    private CheckerService checkerService;
    
    @BeforeEach
    void setUp() {
        // Create a mock subscription checker for testing
        SubscriptionCheckerInterface mockChecker = new MockSubscriptionChecker();
        checkerService = new CheckerServiceImpl(mockChecker);
    }
    
    @AfterEach
    void tearDown() {
        if (checkerService != null) {
            checkerService.cleanup();
        }
    }
    
    @Test
    void testFullWorkflow() {
        // Initialize
        checkerService.initialize();
        
        // Check single email
        CheckerResult result = checkerService.checkEmail("valid@example.com");
        assertNotNull(result);
        assertEquals("valid@example.com", result.getEmail());
        
        // Cleanup
        checkerService.cleanup();
    }
    
    @Test
    void testMultipleEmailsWorkflow() {
        checkerService.initialize();
        
        List<String> emails = Arrays.asList(
            "valid1@example.com",
            "valid2@example.com",
            "invalid@example.com"
        );
        
        List<CheckerResult> results = checkerService.checkEmails(emails);
        
        assertNotNull(results);
        assertEquals(3, results.size());
        
        // Verify all emails were checked
        for (int i = 0; i < emails.size(); i++) {
            assertEquals(emails.get(i), results.get(i).getEmail());
        }
        
        checkerService.cleanup();
    }
    
    @Test
    void testFilteringValidEmails() {
        checkerService.initialize();
        
        List<String> emails = Arrays.asList(
            "valid1@example.com",
            "invalid@example.com",
            "valid2@example.com"
        );
        
        List<CheckerResult> results = checkerService.checkEmails(emails);
        List<String> validEmails = CheckerResult.getValidEmails(results);
        
        assertTrue(validEmails.size() >= 1, "Should have at least one valid email");
        
        checkerService.cleanup();
    }
    
    @Test
    @Disabled("Disabled in CI/CD - requires browser driver setup")
    void testServiceWithDefaultConstructor() {
        CheckerService service = new CheckerServiceImpl();
        
        assertNotNull(service.getSubscriptionChecker());
        assertDoesNotThrow(() -> {
            service.initialize();
            service.cleanup();
        });
    }
    
    /**
     * Mock implementation of SubscriptionCheckerInterface for testing
     */
    private static class MockSubscriptionChecker implements SubscriptionCheckerInterface {
        private String checkerUrl = "https://mock-checker.example.com";
        private boolean initialized = false;
        
        @Override
        public boolean checkSubscription(String email) {
            if (!initialized) {
                throw new IllegalStateException("Not initialized");
            }
            // Simple mock logic: emails containing "valid" are valid
            return email.toLowerCase().contains("valid");
        }
        
        @Override
        public List<Boolean> checkSubscriptions(List<String> emails) {
            return emails.stream()
                .map(this::checkSubscription)
                .toList();
        }
        
        @Override
        public String getCheckerUrl() {
            return checkerUrl;
        }
        
        @Override
        public void setCheckerUrl(String url) {
            this.checkerUrl = url;
        }
        
        @Override
        public void initialize() {
            initialized = true;
        }
        
        @Override
        public void cleanup() {
            initialized = false;
        }
    }
}
