package services.intelligence.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Marketing persona and engagement scoring
 */
public class PersonaScore {
    private String segment; // "Tech Professional", "Digital Consumer", etc.
    private List<String> interests;
    private String engagementLevel; // HIGH, MEDIUM, LOW
    private Integer personaConfidence; // 0-100
    private List<String> marketingRecommendations;

    public PersonaScore() {
        this.interests = new ArrayList<>();
        this.marketingRecommendations = new ArrayList<>();
    }

    // Getters and Setters
    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getEngagementLevel() {
        return engagementLevel;
    }

    public void setEngagementLevel(String engagementLevel) {
        this.engagementLevel = engagementLevel;
    }

    public Integer getPersonaConfidence() {
        return personaConfidence;
    }

    public void setPersonaConfidence(Integer personaConfidence) {
        this.personaConfidence = personaConfidence;
    }

    public List<String> getMarketingRecommendations() {
        return marketingRecommendations;
    }

    public void setMarketingRecommendations(List<String> marketingRecommendations) {
        this.marketingRecommendations = marketingRecommendations;
    }
}
