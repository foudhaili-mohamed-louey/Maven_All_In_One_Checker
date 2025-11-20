package services.intelligence.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for Gravatar profile information
 */
public class GravatarData {
    private boolean profileExists;
    private String displayName;
    private String profileImageUrl;
    private List<String> linkedAccounts;
    private Integer estimatedAccountAge;

    public GravatarData() {
        this.linkedAccounts = new ArrayList<>();
    }

    public static GravatarData empty() {
        GravatarData data = new GravatarData();
        data.setProfileExists(false);
        return data;
    }

    // Getters and Setters
    public boolean isProfileExists() {
        return profileExists;
    }

    public void setProfileExists(boolean profileExists) {
        this.profileExists = profileExists;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<String> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public Integer getEstimatedAccountAge() {
        return estimatedAccountAge;
    }

    public void setEstimatedAccountAge(Integer estimatedAccountAge) {
        this.estimatedAccountAge = estimatedAccountAge;
    }
}
