package services.intelligence.collectors;

import services.intelligence.models.EmailMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes email structure and patterns without external APIs
 */
public class EmailPatternAnalyzer {
    private static final Map<String, Integer> PROVIDER_REPUTATION = new HashMap<>();

    static {
        // Provider reputation scores (0-100)
        PROVIDER_REPUTATION.put("gmail.com", 95);
        PROVIDER_REPUTATION.put("outlook.com", 90);
        PROVIDER_REPUTATION.put("hotmail.com", 85);
        PROVIDER_REPUTATION.put("yahoo.com", 75);
        PROVIDER_REPUTATION.put("protonmail.com", 85);
        PROVIDER_REPUTATION.put("icloud.com", 90);
        PROVIDER_REPUTATION.put("aol.com", 70);
        PROVIDER_REPUTATION.put("mail.com", 65);
    }

    /**
     * Analyzes email structure
     */
    public EmailMetrics analyze(String email) {
        EmailMetrics metrics = new EmailMetrics();

        if (email == null || !email.contains("@")) {
            return metrics;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return metrics;
        }

        String username = parts[0];
        String domain = parts[1];

        metrics.setUsername(username);
        metrics.setDomain(domain);

        // Classify domain type
        metrics.setDomainType(classifyDomain(domain));

        // Analyze username pattern
        metrics.setUsernamePattern(analyzeUsernamePattern(username));

        // Calculate provider reputation
        metrics.setProviderReputationScore(getProviderReputation(domain));

        // Calculate overall email quality score
        metrics.setEmailQualityScore(calculateQualityScore(username, domain, metrics));

        return metrics;
    }

    private String classifyDomain(String domain) {
        domain = domain.toLowerCase();

        // Educational domains
        if (domain.endsWith(".edu") || domain.contains(".edu.")) {
            return "EDU";
        }

        // Common personal providers
        if (isCommonProvider(domain)) {
            return "PERSONAL";
        }

        // Disposable email indicators
        if (isDisposableDomain(domain)) {
            return "DISPOSABLE";
        }

        // Corporate/Business email
        return "CORPORATE";
    }

    private boolean isCommonProvider(String domain) {
        return domain.equals("gmail.com") || 
               domain.equals("yahoo.com") ||
               domain.equals("outlook.com") ||
               domain.equals("hotmail.com") ||
               domain.equals("icloud.com") ||
               domain.equals("aol.com") ||
               domain.equals("protonmail.com") ||
               domain.equals("mail.com");
    }

    private boolean isDisposableDomain(String domain) {
        return domain.contains("temp") ||
               domain.contains("disposable") ||
               domain.contains("throwaway") ||
               domain.equals("mailinator.com") ||
               domain.equals("yopmail.com") ||
               domain.equals("10minutemail.com") ||
               domain.equals("guerrillamail.com");
    }

    private String analyzeUsernamePattern(String username) {
        if (username == null || username.isEmpty()) {
            return "UNKNOWN";
        }

        username = username.toLowerCase();

        // Professional format (firstname.lastname or similar)
        if (username.matches("[a-z]+\\.[a-z]+")) {
            return "PROFESSIONAL";
        }

        // Has many numbers (casual/generated)
        if (username.matches(".*\\d{3,}.*")) {
            return "NUMERIC";
        }

        // Simple/early adopter (short, clean username)
        if (username.length() < 6 && username.matches("[a-z]+")) {
            return "SIMPLE";
        }

        // Default to casual
        return "CASUAL";
    }

    private Integer getProviderReputation(String domain) {
        domain = domain.toLowerCase();
        
        // Known provider
        if (PROVIDER_REPUTATION.containsKey(domain)) {
            return PROVIDER_REPUTATION.get(domain);
        }

        // Corporate domains get medium-high score
        if (!isCommonProvider(domain) && !isDisposableDomain(domain)) {
            return 80;
        }

        // Unknown gets neutral score
        return 50;
    }

    private Integer calculateQualityScore(String username, String domain, EmailMetrics metrics) {
        int score = 50; // Base score

        // Domain type impact
        switch (metrics.getDomainType()) {
            case "CORPORATE":
                score += 20;
                break;
            case "EDU":
                score += 15;
                break;
            case "PERSONAL":
                score += 10;
                break;
            case "DISPOSABLE":
                score -= 30;
                break;
        }

        // Username pattern impact
        switch (metrics.getUsernamePattern()) {
            case "PROFESSIONAL":
                score += 20;
                break;
            case "SIMPLE":
                score += 10;
                break;
            case "CASUAL":
                score += 5;
                break;
            case "NUMERIC":
                score -= 5;
                break;
        }

        // Provider reputation impact (scaled)
        Integer providerScore = metrics.getProviderReputationScore();
        if (providerScore != null) {
            score += (providerScore - 50) / 5;
        }

        // Ensure score is in valid range
        return Math.max(0, Math.min(100, score));
    }
}
