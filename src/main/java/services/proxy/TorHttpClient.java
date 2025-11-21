package services.proxy;

import config.TorProxyConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * HTTP client service that routes requests through Tor SOCKS5 proxy
 * Supports circuit rotation for changing exit nodes
 */
public class TorHttpClient {
    
    private final TorProxyConfig config;
    private final OkHttpClient client;
    private final OkHttpClient directClient;
    private long lastCircuitRotation;
    
    /**
     * Creates a TorHttpClient with the given configuration
     */
    public TorHttpClient(TorProxyConfig config) {
        this.config = config;
        this.lastCircuitRotation = System.currentTimeMillis();
        
        // Build Tor-enabled client
        if (config.isTorEnabled()) {
            Proxy torProxy = new Proxy(
                Proxy.Type.SOCKS,
                new InetSocketAddress(config.getTorHost(), config.getTorPort())
            );
            
            this.client = new OkHttpClient.Builder()
                .proxy(torProxy)
                .connectTimeout(config.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .build();
        } else {
            // Fallback to direct connection
            this.client = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .build();
        }
        
        // Always keep a direct client for non-Tor requests
        this.directClient = new OkHttpClient.Builder()
            .connectTimeout(config.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .build();
    }
    
    /**
     * Execute HTTP request through Tor (if enabled)
     */
    public Response execute(Request request) throws IOException {
        // Rotate circuit if needed
        if (config.isTorEnabled() && shouldRotateCircuit()) {
            rotateCircuit();
        }
        
        return client.newCall(request).execute();
    }
    
    /**
     * Execute HTTP request without Tor (direct connection)
     */
    public Response executeDirect(Request request) throws IOException {
        return directClient.newCall(request).execute();
    }
    
    /**
     * Get the OkHttpClient instance (for compatibility)
     */
    public OkHttpClient getClient() {
        return client;
    }
    
    /**
     * Get direct client instance (no Tor)
     */
    public OkHttpClient getDirectClient() {
        return directClient;
    }
    
    /**
     * Check if circuit should be rotated
     */
    private boolean shouldRotateCircuit() {
        long elapsed = System.currentTimeMillis() - lastCircuitRotation;
        return elapsed >= config.getCircuitRotationIntervalMs();
    }
    
    /**
     * Rotate Tor circuit by sending NEWNYM signal to control port
     * This changes the exit node for subsequent requests
     * 
     * Note: This requires Tor control port to be accessible.
     * Default control port is 9051, and requires authentication.
     * If control port is not configured, this will silently fail.
     */
    public void rotateCircuit() {
        if (!config.isTorEnabled()) {
            return;
        }
        
        try {
            // Try to connect to Tor control port (default 9051)
            int controlPort = 9051;
            String controlHost = config.getTorHost();
            
            try (Socket socket = new Socket(controlHost, controlPort)) {
                OutputStream out = socket.getOutputStream();
                
                // Send AUTHENTICATE command (assumes no password or cookie auth)
                // In production, you'd want to read from cookie file or use password
                out.write("AUTHENTICATE \"\"\r\n".getBytes(StandardCharsets.UTF_8));
                out.flush();
                
                // Send SIGNAL NEWNYM to rotate circuit
                out.write("SIGNAL NEWNYM\r\n".getBytes(StandardCharsets.UTF_8));
                out.flush();
                
                // Send QUIT
                out.write("QUIT\r\n".getBytes(StandardCharsets.UTF_8));
                out.flush();
                
                lastCircuitRotation = System.currentTimeMillis();
                System.out.println("Tor circuit rotated successfully");
            }
        } catch (Exception e) {
            // Circuit rotation failed - log but continue
            // This is not critical as requests can still proceed with current circuit
            System.err.println("Warning: Failed to rotate Tor circuit: " + e.getMessage());
        }
    }
    
    /**
     * Force circuit rotation immediately
     */
    public void forceRotateCircuit() {
        lastCircuitRotation = 0;
        rotateCircuit();
    }
    
    /**
     * Check if Tor is enabled
     */
    public boolean isTorEnabled() {
        return config.isTorEnabled();
    }
}
