package services.intelligence.models;

/**
 * Email metrics and pattern analysis results
 */
public class EmailMetrics {
    private String domain;
    private String username;
    private String domainType; // PERSONAL, CORPORATE, EDU, DISPOSABLE
    private String usernamePattern; // PROFESSIONAL, CASUAL, NUMERIC
    private Integer providerReputationScore; // 0-100
    private Integer emailQualityScore; // 0-100

    // Getters and Setters
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getUsernamePattern() {
        return usernamePattern;
    }

    public void setUsernamePattern(String usernamePattern) {
        this.usernamePattern = usernamePattern;
    }

    public Integer getProviderReputationScore() {
        return providerReputationScore;
    }

    public void setProviderReputationScore(Integer providerReputationScore) {
        this.providerReputationScore = providerReputationScore;
    }

    public Integer getEmailQualityScore() {
        return emailQualityScore;
    }

    public void setEmailQualityScore(Integer emailQualityScore) {
        this.emailQualityScore = emailQualityScore;
    }

    public boolean isProfessionalFormat() {
        return "PROFESSIONAL".equals(usernamePattern);
    }

    public boolean isCorporateDomain() {
        return "CORPORATE".equals(domainType);
    }

    public boolean isGenericUsername() {
        return username != null && 
               (username.matches(".*\\d{3,}.*") || username.length() < 4);
    }
}
