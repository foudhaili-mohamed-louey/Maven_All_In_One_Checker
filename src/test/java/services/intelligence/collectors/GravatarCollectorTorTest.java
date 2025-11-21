package services.intelligence.collectors;

import config.TorProxyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import services.intelligence.models.GravatarData;
import services.proxy.TorHttpClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GravatarCollector with Tor integration
 */
@DisplayName("GravatarCollector Tor Integration Tests")
class GravatarCollectorTorTest {
    
    @Test
    @DisplayName("Collector created without Tor client")
    void testCollectorWithoutTor() {
        GravatarCollector collector = new GravatarCollector();
        
        assertNotNull(collector);
    }
    
    @Test
    @DisplayName("Collector created with Tor client")
    void testCollectorWithTor() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        GravatarCollector collector = new GravatarCollector(torClient);
        
        assertNotNull(collector);
    }
    
    @Test
    @DisplayName("Collect returns GravatarData with Tor disabled")
    void testCollectWithTorDisabled() {
        TorProxyConfig config = new TorProxyConfig("127.0.0.1", 9050, false);
        TorHttpClient torClient = new TorHttpClient(config);
        GravatarCollector collector = new GravatarCollector(torClient);
        
        GravatarData data = collector.collect("test@example.com");
        
        assertNotNull(data);
    }
    
    @Test
    @DisplayName("Collect handles null Tor client gracefully")
    void testCollectWithNullTorClient() {
        GravatarCollector collector = new GravatarCollector(null);
        
        GravatarData data = collector.collect("test@example.com");
        
        assertNotNull(data);
    }
}
