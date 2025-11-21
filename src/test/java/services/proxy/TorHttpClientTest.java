package services.proxy;

import config.TorProxyConfig;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TorHttpClient
 */
@DisplayName("TorHttpClient Tests")
class TorHttpClientTest {
    
    @Test
    @DisplayName("Client created with Tor disabled")
    void testClientWithTorDisabled() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient client = new TorHttpClient(config);
        
        assertNotNull(client);
        assertFalse(client.isTorEnabled());
        assertNotNull(client.getClient());
        assertNotNull(client.getDirectClient());
    }
    
    @Test
    @DisplayName("Client created with Tor enabled")
    void testClientWithTorEnabled() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, true);
        TorHttpClient client = new TorHttpClient(config);
        
        assertNotNull(client);
        assertTrue(client.isTorEnabled());
        assertNotNull(client.getClient());
        assertNotNull(client.getDirectClient());
    }
    
    @Test
    @DisplayName("Direct client is always available")
    void testDirectClient() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, true);
        TorHttpClient client = new TorHttpClient(config);
        
        OkHttpClient directClient = client.getDirectClient();
        assertNotNull(directClient);
    }
    
    @Test
    @DisplayName("Force rotate circuit does not throw exception")
    void testForceRotateCircuit() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, true);
        TorHttpClient client = new TorHttpClient(config);
        
        // Should not throw exception even if Tor is not running
        assertDoesNotThrow(() -> client.forceRotateCircuit());
    }
    
    @Test
    @DisplayName("Rotate circuit with Tor disabled does nothing")
    void testRotateCircuitWithTorDisabled() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient client = new TorHttpClient(config);
        
        // Should not throw exception
        assertDoesNotThrow(() -> client.rotateCircuit());
    }
}
