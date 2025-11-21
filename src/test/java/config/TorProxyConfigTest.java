package config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TorProxyConfig
 */
@DisplayName("TorProxyConfig Tests")
class TorProxyConfigTest {
    
    @Test
    @DisplayName("Default configuration has Tor disabled")
    void testDefaultConfig() {
        TorProxyConfig config = new TorProxyConfig();
        
        assertFalse(config.isTorEnabled());
        assertEquals("127.0.0.1", config.getTorHost());
        assertEquals(9050, config.getTorPort());
        assertEquals(30000, config.getConnectionTimeoutMs());
        assertEquals(30000, config.getReadTimeoutMs());
    }
    
    @Test
    @DisplayName("Custom configuration with Tor enabled")
    void testCustomConfig() {
        TorProxyConfig config = new TorProxyConfig("localhost", 9150, true);
        
        assertTrue(config.isTorEnabled());
        assertEquals("localhost", config.getTorHost());
        assertEquals(9150, config.getTorPort());
    }
    
    @Test
    @DisplayName("Configuration handles null host")
    void testNullHost() {
        TorProxyConfig config = new TorProxyConfig(null, 9050, true);
        
        assertEquals("127.0.0.1", config.getTorHost());
    }
    
    @Test
    @DisplayName("Configuration handles invalid port")
    void testInvalidPort() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", -1, true);
        
        assertEquals(9050, config.getTorPort());
    }
    
    @Test
    @DisplayName("Full custom configuration")
    void testFullCustomConfig() {
        TorProxyConfig config = new TorProxyConfig(
            "192.168.1.1", 
            9999, 
            true,
            15000,
            20000,
            5000
        );
        
        assertTrue(config.isTorEnabled());
        assertEquals("192.168.1.1", config.getTorHost());
        assertEquals(9999, config.getTorPort());
        assertEquals(15000, config.getConnectionTimeoutMs());
        assertEquals(20000, config.getReadTimeoutMs());
        assertEquals(5000, config.getCircuitRotationIntervalMs());
    }
    
    @Test
    @DisplayName("ToString returns valid string")
    void testToString() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, true);
        
        String str = config.toString();
        assertNotNull(str);
        assertTrue(str.contains("127.0.0.1"));
        assertTrue(str.contains("9050"));
        assertTrue(str.contains("true"));
    }
}
