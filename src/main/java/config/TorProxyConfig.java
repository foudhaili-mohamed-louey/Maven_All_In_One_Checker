package config;

/**
 * Configuration for Tor proxy settings
 * Supports SOCKS5 proxy for routing HTTP requests through Tor network
 */
public class TorProxyConfig {
    
    // Default Tor SOCKS5 proxy settings
    private static final String DEFAULT_TOR_HOST = "127.0.0.1";
    private static final int DEFAULT_TOR_PORT = 9050;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 30000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 30000;
    private static final int DEFAULT_CIRCUIT_ROTATION_INTERVAL_MS = 10000;
    
    private final String torHost;
    private final int torPort;
    private final boolean torEnabled;
    private final int connectionTimeoutMs;
    private final int readTimeoutMs;
    private final int circuitRotationIntervalMs;
    
    /**
     * Creates a TorProxyConfig with default settings
     */
    public TorProxyConfig() {
        this(DEFAULT_TOR_HOST, DEFAULT_TOR_PORT, false);
    }
    
    /**
     * Creates a TorProxyConfig with custom settings
     */
    public TorProxyConfig(String torHost, int torPort, boolean torEnabled) {
        this(torHost, torPort, torEnabled, 
             DEFAULT_CONNECTION_TIMEOUT_MS, 
             DEFAULT_READ_TIMEOUT_MS,
             DEFAULT_CIRCUIT_ROTATION_INTERVAL_MS);
    }
    
    /**
     * Creates a TorProxyConfig with full custom settings
     */
    public TorProxyConfig(String torHost, int torPort, boolean torEnabled,
                         int connectionTimeoutMs, int readTimeoutMs,
                         int circuitRotationIntervalMs) {
        this.torHost = torHost != null ? torHost : DEFAULT_TOR_HOST;
        this.torPort = torPort > 0 ? torPort : DEFAULT_TOR_PORT;
        this.torEnabled = torEnabled;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.circuitRotationIntervalMs = circuitRotationIntervalMs;
    }
    
    /**
     * Creates config from environment variables
     * Supports:
     * - TOR_ENABLED: true/false to enable Tor routing
     * - TOR_HOST: Tor proxy host (default: 127.0.0.1)
     * - TOR_PORT: Tor SOCKS5 port (default: 9050)
     * - TOR_CONNECTION_TIMEOUT_MS: Connection timeout in milliseconds
     * - TOR_READ_TIMEOUT_MS: Read timeout in milliseconds
     * - TOR_CIRCUIT_ROTATION_INTERVAL_MS: Circuit rotation interval
     */
    public static TorProxyConfig fromEnvironment() {
        String enabled = System.getenv("TOR_ENABLED");
        String host = System.getenv("TOR_HOST");
        String port = System.getenv("TOR_PORT");
        String connTimeout = System.getenv("TOR_CONNECTION_TIMEOUT_MS");
        String readTimeout = System.getenv("TOR_READ_TIMEOUT_MS");
        String rotationInterval = System.getenv("TOR_CIRCUIT_ROTATION_INTERVAL_MS");
        
        boolean torEnabled = "true".equalsIgnoreCase(enabled);
        String torHost = host != null ? host : DEFAULT_TOR_HOST;
        int torPort = port != null ? Integer.parseInt(port) : DEFAULT_TOR_PORT;
        int connectionTimeoutMs = connTimeout != null ? Integer.parseInt(connTimeout) : DEFAULT_CONNECTION_TIMEOUT_MS;
        int readTimeoutMs = readTimeout != null ? Integer.parseInt(readTimeout) : DEFAULT_READ_TIMEOUT_MS;
        int circuitRotationMs = rotationInterval != null ? Integer.parseInt(rotationInterval) : DEFAULT_CIRCUIT_ROTATION_INTERVAL_MS;
        
        return new TorProxyConfig(torHost, torPort, torEnabled, 
                                  connectionTimeoutMs, readTimeoutMs, circuitRotationMs);
    }
    
    // Getters
    public String getTorHost() {
        return torHost;
    }
    
    public int getTorPort() {
        return torPort;
    }
    
    public boolean isTorEnabled() {
        return torEnabled;
    }
    
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
    
    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }
    
    public int getCircuitRotationIntervalMs() {
        return circuitRotationIntervalMs;
    }
    
    @Override
    public String toString() {
        return "TorProxyConfig{" +
                "torHost='" + torHost + '\'' +
                ", torPort=" + torPort +
                ", torEnabled=" + torEnabled +
                ", connectionTimeoutMs=" + connectionTimeoutMs +
                ", readTimeoutMs=" + readTimeoutMs +
                ", circuitRotationIntervalMs=" + circuitRotationIntervalMs +
                '}';
    }
}
