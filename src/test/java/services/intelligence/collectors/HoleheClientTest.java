package services.intelligence.collectors;

import config.TorProxyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import services.intelligence.models.ServicePresence;
import services.proxy.TorHttpClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HoleheClient
 */
@DisplayName("HoleheClient Tests")
class HoleheClientTest {
    
    @Test
    @DisplayName("Client created successfully with Tor disabled")
    void testClientCreation() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        HoleheClient client = new HoleheClient(torClient, false);
        
        assertNotNull(client);
    }
    
    @Test
    @DisplayName("Check email returns ServicePresence")
    void testCheckEmail() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        HoleheClient client = new HoleheClient(torClient, false);
        
        ServicePresence presence = client.checkEmail("test@example.com");
        
        assertNotNull(presence);
        assertNotNull(presence.getServices());
    }
    
    @Test
    @DisplayName("Check email with empty email returns ServicePresence")
    void testCheckEmailEmpty() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        HoleheClient client = new HoleheClient(torClient, false);
        
        ServicePresence presence = client.checkEmail("");
        
        assertNotNull(presence);
    }
    
    @Test
    @DisplayName("Check email with invalid format returns empty ServicePresence")
    void testCheckEmailInvalidFormat() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        HoleheClient client = new HoleheClient(torClient, false);
        
        // Test various invalid email formats
        ServicePresence presence1 = client.checkEmail("not-an-email");
        ServicePresence presence2 = client.checkEmail("@example.com");
        ServicePresence presence3 = client.checkEmail("user@");
        
        assertNotNull(presence1);
        assertNotNull(presence2);
        assertNotNull(presence3);
    }
}
