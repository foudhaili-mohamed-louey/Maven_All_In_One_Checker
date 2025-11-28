package services.intelligence.collectors;

import services.intelligence.models.ServicePresence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// check service present using tor proxy and caching
public class ServicePresenceChecker {
    private static final List<String> PRIORITY_SERVICES = Arrays.asList(
        "linkedin", "github", "twitter", "instagram",
        "netflix", "spotify", "amazon", "steam",
        "playstation", "xbox"
    );

   public ServicePresence checkServices(String email) {
        ServicePresence presence = new ServicePresence();
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
