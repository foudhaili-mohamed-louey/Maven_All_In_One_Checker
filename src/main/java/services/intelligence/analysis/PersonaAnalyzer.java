package services.intelligence.analysis;

import services.intelligence.models.EmailMetrics;
import services.intelligence.models.GravatarData;
import services.intelligence.models.PersonaScore;
import services.intelligence.models.ServicePresence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds marketing personas from collected data
 */
public class PersonaAnalyzer {

    /**
     * Builds marketing persona from collected intelligence
     */
    public PersonaScore buildPersona(GravatarData gravatar, EmailMetrics metrics, ServicePresence services) {
        PersonaScore persona = new PersonaScore();

        // Determine segment based on available data
        String segment = determineSegment(gravatar, metrics, services);
        persona.setSegment(segment);

        // Assign interests based on segment
        persona.setInterests(determineInterests(segment, metrics, services));

        // Calculate engagement level
        persona.setEngagementLevel(determineEngagementLevel(segment, metrics, services, gravatar));

        // Calculate confidence in persona assignment
        persona.setPersonaConfidence(calculateConfidence(gravatar, metrics, services));

        // Generate marketing recommendations
        persona.setMarketingRecommendations(generateRecommendations(segment, metrics));

        return persona;
    }

    private String determineSegment(GravatarData gravatar, EmailMetrics metrics, ServicePresence services) {
        // Tech Professional
        if ((services.has("github") || services.has("linkedin")) && 
            metrics.isProfessionalFormat()) {
            return "Tech Professional";
        }

        // Business Decision Maker
        if (metrics.isCorporateDomain() && 
            services.has("linkedin") && 
            !metrics.isGenericUsername()) {
            return "B2B Decision Maker";
        }

        // Digital Entertainment Consumer
        if ((services.has("netflix") || services.has("spotify")) &&
            (services.has("steam") || services.has("playstation") || services.has("xbox"))) {
            return "Digital Entertainment Consumer";
        }

        // Early Digital Adopter
        if (gravatar.isProfileExists() && services.count() > 5) {
            return "Early Adopter";
        }

        // Social Media Enthusiast
        if (services.has("twitter") || services.has("instagram")) {
            return "Social Media Enthusiast";
        }

        // Professional - based on email pattern
        if (metrics.isProfessionalFormat() || metrics.isCorporateDomain()) {
            return "Professional";
        }

        // Digital Consumer (default for personal emails)
        if ("PERSONAL".equals(metrics.getDomainType())) {
            return "Digital Consumer";
        }

        // General Consumer (fallback)
        return "General Consumer";
    }

    private List<String> determineInterests(String segment, EmailMetrics metrics, ServicePresence services) {
        List<String> interests = new ArrayList<>();

        switch (segment) {
            case "Tech Professional":
                interests.addAll(Arrays.asList("Technology", "Professional Development", "SaaS", "Innovation"));
                break;
            case "B2B Decision Maker":
                interests.addAll(Arrays.asList("Business Software", "Enterprise Solutions", "Productivity", "Analytics"));
                break;
            case "Digital Entertainment Consumer":
                interests.addAll(Arrays.asList("Streaming", "Gaming", "Digital Media", "Entertainment"));
                break;
            case "Early Adopter":
                interests.addAll(Arrays.asList("New Technology", "Digital Services", "Innovation", "Beta Testing"));
                break;
            case "Social Media Enthusiast":
                interests.addAll(Arrays.asList("Social Networks", "Content Creation", "Digital Marketing", "Trends"));
                break;
            case "Professional":
                interests.addAll(Arrays.asList("Career Development", "Business Tools", "Networking"));
                break;
            default:
                interests.addAll(Arrays.asList("General Technology", "Digital Services"));
        }

        // Add service-specific interests
        if (services.has("netflix")) interests.add("Video Streaming");
        if (services.has("spotify")) interests.add("Music Streaming");
        if (services.has("github")) interests.add("Software Development");
        if (services.has("steam")) interests.add("PC Gaming");

        return interests;
    }

    private String determineEngagementLevel(String segment, EmailMetrics metrics, ServicePresence services, GravatarData gravatar) {
        int score = 0;

        // Segment impact
        if (segment.equals("Tech Professional") || segment.equals("B2B Decision Maker")) {
            score += 30;
        } else if (segment.equals("Early Adopter")) {
            score += 25;
        } else if (segment.equals("Professional")) {
            score += 20;
        } else {
            score += 10;
        }

        // Email quality impact
        Integer emailQuality = metrics.getEmailQualityScore();
        if (emailQuality != null) {
            score += emailQuality / 5;
        }

        // Service presence impact
        score += services.count() * 5;

        // Gravatar presence indicates engagement
        if (gravatar != null && gravatar.isProfileExists()) {
            score += 15;
        }

        // Determine level
        if (score >= 60) {
            return "HIGH";
        } else if (score >= 35) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private Integer calculateConfidence(GravatarData gravatar, EmailMetrics metrics, ServicePresence services) {
        int confidence = 50; // Base confidence

        // Data completeness increases confidence
        if (gravatar != null && gravatar.isProfileExists()) {
            confidence += 15;
        }

        if (metrics.getEmailQualityScore() != null && metrics.getEmailQualityScore() > 70) {
            confidence += 15;
        }

        if (services.count() > 0) {
            confidence += Math.min(20, services.count() * 4);
        }

        return Math.min(100, confidence);
    }

    private List<String> generateRecommendations(String segment, EmailMetrics metrics) {
        List<String> recommendations = new ArrayList<>();

        switch (segment) {
            case "Tech Professional":
                recommendations.add("Target with SaaS and developer tools");
                recommendations.add("Focus on technical content and case studies");
                recommendations.add("Emphasize ROI and productivity gains");
                break;
            case "B2B Decision Maker":
                recommendations.add("Present enterprise-level solutions");
                recommendations.add("Offer personalized demos and consultations");
                recommendations.add("Focus on business value and scalability");
                break;
            case "Digital Entertainment Consumer":
                recommendations.add("Promote entertainment and lifestyle products");
                recommendations.add("Use engaging, visual content");
                recommendations.add("Leverage social proof and reviews");
                break;
            case "Early Adopter":
                recommendations.add("Offer early access to new features");
                recommendations.add("Invite to beta programs");
                recommendations.add("Emphasize innovation and cutting-edge technology");
                break;
            case "Professional":
                recommendations.add("Focus on career advancement and productivity");
                recommendations.add("Offer professional development resources");
                recommendations.add("Emphasize time-saving benefits");
                break;
            default:
                recommendations.add("Use clear, benefit-focused messaging");
                recommendations.add("Provide educational content");
                recommendations.add("Build trust through testimonials");
        }

        return recommendations;
    }
}
