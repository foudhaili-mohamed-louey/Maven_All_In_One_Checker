package services.intelligence.collectors;

import services.intelligence.models.ServicePresence;
import services.proxy.TorHttpClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks presence on key services using Holehe integration
 * Integrates with Holehe (when available) or uses fallback checking
 */
public class ServicePresenceChecker {
    
    private final HoleheClient holeheClient;
    private final boolean useHolehe;
    
    // Top priority services to check (in a real implementation)
    private static final List<String> PRIORITY_SERVICES = Arrays.asList(
        "linkedin", "github", "twitter", "instagram",
        "netflix", "spotify", "amazon", "steam",
        "playstation", "xbox"
    );

    /**
     * Create checker without Holehe/Tor integration (mock mode)
     */
    public ServicePresenceChecker() {
        this(null, false);
    }
    
    /**
     * Create checker with optional Holehe integration
     * @param torClient Tor-enabled HTTP client (null to disable)
     * @param useHolehe Whether to use Holehe for checking
     */
    public ServicePresenceChecker(TorHttpClient torClient, boolean useHolehe) {
        this.useHolehe = useHolehe && torClient != null;
        this.holeheClient = (torClient != null) ? new HoleheClient(torClient, false) : null;
    }

    /**
     * Checks presence on key services
     * Uses Holehe integration if enabled, otherwise returns mock results
     */
    public ServicePresence checkServices(String email) {
        // Use Holehe if enabled
        if (useHolehe && holeheClient != null) {
            try {
                return holeheClient.checkEmail(email);
            } catch (Exception e) {
                System.err.println("Holehe check failed, falling back to mock: " + e.getMessage());
                // Fall through to mock implementation
            }
        }
        
        // Mock implementation for when Holehe is not enabled
        ServicePresence presence = new ServicePresence();
        
        // In a real implementation, this would:
        // 1. Use rate limiting (max 10 services per email)
        // 2. Make actual HTTP requests to check registration
        // 3. Return quickly to avoid blocking (timeout ~5 seconds)
        // 4. Cache results to avoid repeated checks
        
        // For now, we return empty presence to avoid making actual requests
        // This keeps the system fast and doesn't require external dependencies
        
        Set<String> categories = new HashSet<>();
        for (String service : PRIORITY_SERVICES) {
            presence.addService(service, false);
            
            // Categorize services
            if (Arrays.asList("linkedin", "github", "twitter", "instagram").contains(service)) {
                categories.add("Social");
            }
            if (Arrays.asList("netflix", "spotify", "amazon").contains(service)) {
                categories.add("Entertainment");
            }
            if (Arrays.asList("steam", "playstation", "xbox").contains(service)) {
                categories.add("Gaming");
            }
        }
        
        presence.setCategories(Arrays.asList(categories.toArray(new String[0])));
        return presence;
    }

    /**
     * Mock implementation that simulates service checking
     * Can be enabled for demo purposes
     */
    public ServicePresence mockCheckServices(String email) {
        ServicePresence presence = new ServicePresence();
        
        // Simulate some presence based on email domain
        String domain = email.contains("@") ? email.split("@")[1].toLowerCase() : "";
        
        // Professional domains might have LinkedIn/GitHub
        if (!domain.equals("gmail.com") && !domain.equals("yahoo.com")) {
            presence.addService("linkedin", true);
            presence.addService("github", Math.random() > 0.5);
        } else {
            // Personal emails might have social media
            presence.addService("twitter", Math.random() > 0.6);
            presence.addService("instagram", Math.random() > 0.5);
        }
        
        // Everyone might have streaming services
        presence.addService("netflix", Math.random() > 0.4);
        presence.addService("spotify", Math.random() > 0.5);
        presence.addService("amazon", Math.random() > 0.3);
        
        // Some might have gaming
        presence.addService("steam", Math.random() > 0.7);
        
        Set<String> categories = new HashSet<>();
        if (presence.has("linkedin") || presence.has("twitter")) categories.add("Professional");
        if (presence.has("netflix") || presence.has("spotify")) categories.add("Entertainment");
        if (presence.has("steam")) categories.add("Gaming");
        
        presence.setCategories(Arrays.asList(categories.toArray(new String[0])));
        return presence;
    }
}
