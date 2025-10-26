package services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CheckerServiceImpl
 */
class CheckerServiceImplTest {
    
    private CheckerServiceImpl checkerService;
    
    @Mock
    private SubscriptionCheckerInterface mockSubscriptionChecker;
    
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        checkerService = new CheckerServiceImpl(mockSubscriptionChecker);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    void testInitialize() {
        checkerService.initialize();
        verify(mockSubscriptionChecker, times(1)).initialize();
    }
    
    @Test
    void testCleanup() {
        checkerService.cleanup();
        verify(mockSubscriptionChecker, times(1)).cleanup();
    }
    
    @Test
    void testCheckEmailValid() {
        String email = "test@example.com";
        when(mockSubscriptionChecker.checkSubscription(email)).thenReturn(true);
        
        CheckerResult result = checkerService.checkEmail(email);
        
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertTrue(result.isValid());
        assertEquals("Valid", result.getStatus());
        verify(mockSubscriptionChecker, times(1)).checkSubscription(email);
    }
    
    @Test
    void testCheckEmailInvalid() {
        String email = "invalid@example.com";
        when(mockSubscriptionChecker.checkSubscription(email)).thenReturn(false);
        
        CheckerResult result = checkerService.checkEmail(email);
        
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertFalse(result.isValid());
        assertEquals("Invalid", result.getStatus());
        verify(mockSubscriptionChecker, times(1)).checkSubscription(email);
    }
    
    @Test
    void testCheckEmailWithException() {
        String email = "error@example.com";
        when(mockSubscriptionChecker.checkSubscription(email))
            .thenThrow(new RuntimeException("Test exception"));
        
        CheckerResult result = checkerService.checkEmail(email);
        
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertFalse(result.isValid());
        assertTrue(result.getStatus().contains("Error"));
    }
    
    @Test
    void testCheckEmails() {
        List<String> emails = Arrays.asList(
            "valid1@example.com",
            "valid2@example.com",
            "invalid@example.com"
        );
        
        when(mockSubscriptionChecker.checkSubscription("valid1@example.com")).thenReturn(true);
        when(mockSubscriptionChecker.checkSubscription("valid2@example.com")).thenReturn(true);
        when(mockSubscriptionChecker.checkSubscription("invalid@example.com")).thenReturn(false);
        
        List<CheckerResult> results = checkerService.checkEmails(emails);
        
        assertNotNull(results);
        assertEquals(3, results.size());
        
        assertTrue(results.get(0).isValid());
        assertTrue(results.get(1).isValid());
        assertFalse(results.get(2).isValid());
        
        verify(mockSubscriptionChecker, times(3)).checkSubscription(anyString());
    }
    
    @Test
    void testGetSetSubscriptionChecker() {
        SubscriptionCheckerInterface newChecker = mock(SubscriptionCheckerInterface.class);
        
        checkerService.setSubscriptionChecker(newChecker);
        
        assertEquals(newChecker, checkerService.getSubscriptionChecker());
    }
    
    @Test
    void testCheckEmailWithNullChecker() {
        checkerService.setSubscriptionChecker(null);
        
        assertThrows(IllegalStateException.class, () -> {
            checkerService.checkEmail("test@example.com");
        });
    }
}
