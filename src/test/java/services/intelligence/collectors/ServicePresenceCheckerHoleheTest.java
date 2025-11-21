package services.intelligence.collectors;

import config.TorProxyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import services.intelligence.models.ServicePresence;
import services.proxy.TorHttpClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ServicePresenceChecker with Holehe integration
 */
@DisplayName("ServicePresenceChecker Holehe Integration Tests")
class ServicePresenceCheckerHoleheTest {
    
    @Test
    @DisplayName("Checker created without Tor client")
    void testCheckerWithoutTor() {
        ServicePresenceChecker checker = new ServicePresenceChecker();
        
        assertNotNull(checker);
    }
    
    @Test
    @DisplayName("Checker created with Tor client disabled")
    void testCheckerWithTorDisabled() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        ServicePresenceChecker checker = new ServicePresenceChecker(torClient, false);
        
        assertNotNull(checker);
    }
    
    @Test
    @DisplayName("Check services returns ServicePresence")
    void testCheckServices() {
        ServicePresenceChecker checker = new ServicePresenceChecker();
        
        ServicePresence presence = checker.checkServices("test@example.com");
        
        assertNotNull(presence);
        assertNotNull(presence.getServices());
    }
    
    @Test
    @DisplayName("Check services with Tor enabled returns ServicePresence")
    void testCheckServicesWithTor() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, true);
        TorHttpClient torClient = new TorHttpClient(config);
        ServicePresenceChecker checker = new ServicePresenceChecker(torClient, false);
        
        ServicePresence presence = checker.checkServices("test@example.com");
        
        assertNotNull(presence);
        assertNotNull(presence.getServices());
    }
    
    @Test
    @DisplayName("Mock check services still works")
    void testMockCheckServices() {
        ServicePresenceChecker checker = new ServicePresenceChecker();
        
        ServicePresence presence = checker.mockCheckServices("test@gmail.com");
        
        assertNotNull(presence);
        assertTrue(presence.getTotalServicesFound() >= 0);
    }
}
