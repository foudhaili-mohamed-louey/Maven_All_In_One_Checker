package services.intelligence.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Security consciousness scoring
 */
public class SecurityScore {
    private Integer overallScore; // 0-100
    private String riskLevel; // LOW, MEDIUM, HIGH
    private List<String> riskFactors;
    private List<String> recommendations;

    public SecurityScore() {
        this.riskFactors = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    // Getters and Setters
    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
