package services.intelligence.analysis;

import services.intelligence.models.EmailMetrics;
import services.intelligence.models.SecurityScore;
import services.intelligence.models.ServicePresence;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates security consciousness scoring
 */
public class SecurityScorer {

    /**
     * Calculates security score based on email metrics and service presence
     */
    public SecurityScore calculateScore(EmailMetrics metrics, ServicePresence services) {
        SecurityScore score = new SecurityScore();

        int overallScore = 50; // Base score
        List<String> riskFactors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        // Email provider security reputation
        Integer providerScore = metrics.getProviderReputationScore();
        if (providerScore != null) {
            overallScore += (providerScore - 50) / 2;

            if (providerScore < 60) {
                riskFactors.add("Low-reputation email provider");
                recommendations.add("Consider using a more established email provider");
            }
        }

        // Domain type impact
        String domainType = metrics.getDomainType();
        if ("DISPOSABLE".equals(domainType)) {
            overallScore -= 30;
            riskFactors.add("Disposable email address detected");
            recommendations.add("Disposable emails pose high security risks");
        } else if ("CORPORATE".equals(domainType)) {
            overallScore += 15;
            recommendations.add("Corporate email shows good security practices");
        } else if ("EDU".equals(domainType)) {
            overallScore += 10;
        }

        // Professional email patterns suggest better security hygiene
        if (metrics.isProfessionalFormat()) {
            overallScore += 10;
        }

        // Account diversity analysis
        int serviceCount = services.count();
        if (serviceCount > 15) {
            overallScore -= 10;
            riskFactors.add("High number of linked services increases attack surface");
            recommendations.add("Consider using different email addresses for different service categories");
        } else if (serviceCount > 8) {
            riskFactors.add("Moderate number of linked services");
            recommendations.add("Monitor account activity regularly");
        }

        // Email quality contributes to security score
        Integer emailQuality = metrics.getEmailQualityScore();
        if (emailQuality != null) {
            overallScore += (emailQuality - 50) / 5;
        }

        // Ensure score is in valid range
        overallScore = Math.max(0, Math.min(100, overallScore));
        score.setOverallScore(overallScore);

        // Determine risk level
        String riskLevel;
        if (overallScore >= 70) {
            riskLevel = "LOW";
        } else if (overallScore >= 40) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "HIGH";
        }
        score.setRiskLevel(riskLevel);

        // Add general recommendations if none exist
        if (recommendations.isEmpty()) {
            recommendations.add("Maintain good email security practices");
            recommendations.add("Use strong, unique passwords");
            recommendations.add("Enable two-factor authentication where available");
        }

        score.setRiskFactors(riskFactors);
        score.setRecommendations(recommendations);

        return score;
    }
}
