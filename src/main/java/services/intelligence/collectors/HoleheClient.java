package services.intelligence.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import services.intelligence.models.ServicePresence;
import services.proxy.TorHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Holehe integration for checking email presence across multiple services
 * Routes all requests through Tor proxy to avoid rate limiting
 * 
 * Holehe is a tool that allows you to check if an email is attached to 
 * an account on sites like twitter, instagram, imgur, etc.
 * 
 * This implementation supports:
 * 1. Command-line mode: Uses holehe CLI (if installed)
 * 2. Manual API mode: Makes direct HTTP requests to services
 */
public class HoleheClient {
    
    private final TorHttpClient torClient;
    private final boolean useCommandLine;
    
    // Services to check (priority services)
    private static final List<String> PRIORITY_SERVICES = Arrays.asList(
        "twitter", "instagram", "github", "linkedin",
        "spotify", "discord", "pinterest", "tumblr",
        "imgur", "snapchat", "adobe", "amazon"
    );
    
    /**
     * Creates HoleheClient with Tor support
     * @param torClient Tor-enabled HTTP client
     * @param useCommandLine Whether to use holehe CLI (requires holehe installed)
     */
    public HoleheClient(TorHttpClient torClient, boolean useCommandLine) {
        this.torClient = torClient;
        this.useCommandLine = useCommandLine;
    }
    
    /**
     * Check email presence across multiple services
     * @param email Email address to check
     * @return ServicePresence with results
     */
    public ServicePresence checkEmail(String email) {
        if (useCommandLine && isHoleheInstalled()) {
            return checkViaCommandLine(email);
        } else {
            return checkViaManualApi(email);
        }
    }
    
    /**
     * Check using holehe command-line tool
     * Requires holehe to be installed: pip install holehe
     */
    private ServicePresence checkViaCommandLine(String email) {
        ServicePresence presence = new ServicePresence();
        
        // Validate email format to prevent command injection
        if (!isValidEmail(email)) {
            System.err.println("Invalid email format, skipping holehe check");
            return presence;
        }
        
        Process process = null;
        try {
            // Execute holehe command with JSON output
            ProcessBuilder pb = new ProcessBuilder("holehe", email, "--only-used");
            
            // Set environment to use Tor proxy if enabled
            if (torClient.isTorEnabled()) {
                Map<String, String> env = pb.environment();
                env.put("http_proxy", "socks5://127.0.0.1:9050");
                env.put("https_proxy", "socks5://127.0.0.1:9050");
            }
            
            process = pb.start();
            
            // Read output using try-with-resources
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Parse holehe output
                parseHoleheOutput(output.toString(), presence);
            } else {
                // Holehe failed, fall back to manual API
                System.err.println("Holehe command failed, falling back to manual API");
                return checkViaManualApi(email);
            }
            
        } catch (Exception e) {
            System.err.println("Error executing holehe: " + e.getMessage());
            // Return empty presence on error
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        
        return presence;
    }
    
    /**
     * Validate email format to prevent command injection
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Basic email validation pattern
        // Allows alphanumeric, dots, hyphens, underscores in local part
        // and alphanumeric, dots, hyphens in domain part
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
    
    /**
     * Check using manual API requests through Tor
     * This is a simplified implementation that checks a subset of services
     */
    private ServicePresence checkViaManualApi(String email) {
        ServicePresence presence = new ServicePresence();
        Set<String> categories = new HashSet<>();
        
        // Check each service with rate limiting
        int checked = 0;
        int maxServices = 10; // Limit to avoid excessive requests
        
        for (String service : PRIORITY_SERVICES) {
            if (checked >= maxServices) {
                break;
            }
            
            try {
                boolean exists = checkServicePresence(service, email);
                presence.addService(service, exists);
                
                // Categorize
                categorizeService(service, exists, categories);
                
                checked++;
                
                // Rate limiting delay between requests
                Thread.sleep(500);
                
            } catch (Exception e) {
                // Service check failed, mark as not found
                presence.addService(service, false);
                System.err.println("Failed to check " + service + ": " + e.getMessage());
            }
        }
        
        presence.setCategories(new ArrayList<>(categories));
        return presence;
    }
    
    /**
     * Check if email is registered on a specific service
     * This is a simplified implementation - actual Holehe uses service-specific checks
     */
    private boolean checkServicePresence(String service, String email) throws IOException, InterruptedException {
        // This is a mock implementation
        // Real implementation would make service-specific API calls
        // For example, for GitHub: https://api.github.com/users/{username}
        
        switch (service.toLowerCase()) {
            case "github":
                return checkGitHub(email);
            case "twitter":
                return checkTwitter(email);
            default:
                // For other services, return false (not implemented)
                return false;
        }
    }
    
    /**
     * Check GitHub presence
     */
    private boolean checkGitHub(String email) throws IOException {
        // Validate email format
        if (!isValidEmail(email) || !email.contains("@")) {
            return false;
        }
        
        // GitHub doesn't have a direct email lookup API
        // This is a simplified check
        String username = email.split("@")[0];
        
        Request request = new Request.Builder()
            .url("https://api.github.com/users/" + username)
            .build();
        
        try (Response response = torClient.execute(request)) {
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check Twitter presence (simplified)
     */
    private boolean checkTwitter(String email) {
        // Twitter doesn't expose email lookup API publicly
        // Real Holehe uses sophisticated checks
        return false;
    }
    
    /**
     * Parse holehe command output
     */
    private void parseHoleheOutput(String output, ServicePresence presence) {
        // Holehe output format varies, this handles common formats
        
        // Try to parse as JSON first
        try {
            JsonElement jsonElement = JsonParser.parseString(output);
            if (jsonElement.isJsonArray()) {
                JsonArray results = jsonElement.getAsJsonArray();
                for (JsonElement elem : results) {
                    if (elem.isJsonObject()) {
                        JsonObject obj = elem.getAsJsonObject();
                        String service = obj.has("name") ? obj.get("name").getAsString() : "";
                        boolean exists = obj.has("exists") ? obj.get("exists").getAsBoolean() : false;
                        if (!service.isEmpty()) {
                            presence.addService(service, exists);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Not JSON format, try text parsing
            parseHoleheTextOutput(output, presence);
        }
    }
    
    /**
     * Parse holehe text output
     */
    private void parseHoleheTextOutput(String output, ServicePresence presence) {
        // Parse text-based output
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.contains("[+]") || line.contains("✓")) {
                // Service found
                String service = extractServiceName(line);
                if (service != null) {
                    presence.addService(service, true);
                }
            }
        }
    }
    
    /**
     * Extract service name from holehe output line
     */
    private String extractServiceName(String line) {
        // Extract service name from various formats
        // Example: "[+] Email used on twitter"
        line = line.toLowerCase();
        for (String service : PRIORITY_SERVICES) {
            if (line.contains(service.toLowerCase())) {
                return service;
            }
        }
        return null;
    }
    
    /**
     * Categorize service
     */
    private void categorizeService(String service, boolean exists, Set<String> categories) {
        if (!exists) return;
        
        String serviceLower = service.toLowerCase();
        if (Arrays.asList("twitter", "instagram", "linkedin", "pinterest", "tumblr", "snapchat").contains(serviceLower)) {
            categories.add("Social");
        }
        if (Arrays.asList("github", "stackoverflow").contains(serviceLower)) {
            categories.add("Professional");
        }
        if (Arrays.asList("spotify", "discord", "imgur", "tumblr").contains(serviceLower)) {
            categories.add("Entertainment");
        }
        if (Arrays.asList("amazon", "adobe").contains(serviceLower)) {
            categories.add("Shopping/Services");
        }
    }
    
    /**
     * Check if holehe is installed
     */
    private boolean isHoleheInstalled() {
        Process process = null;
        try {
            process = new ProcessBuilder("holehe", "--help").start();
            int exitCode = process.waitFor();
            return exitCode == 0 || exitCode == 1; // Some CLIs return 1 for help
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}
