package services.intelligence.models;

import java.time.LocalDateTime;

/**
 * Complete email intelligence profile
 */
public class EmailIntelligenceProfile {
    private String email;
    private GravatarData gravatarData;
    private EmailMetrics emailMetrics;
    private ServicePresence servicePresence;
    private PersonaScore personaScore;
    private SecurityScore securityScore;
    private LocalDateTime analyzedAt;

    public EmailIntelligenceProfile() {
        this.analyzedAt = LocalDateTime.now();
    }

    public EmailIntelligenceProfile(String email, GravatarData gravatarData, 
                                    EmailMetrics emailMetrics, ServicePresence servicePresence,
                                    PersonaScore personaScore) {
        this.email = email;
        this.gravatarData = gravatarData;
        this.emailMetrics = emailMetrics;
        this.servicePresence = servicePresence;
        this.personaScore = personaScore;
        this.analyzedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GravatarData getGravatarData() {
        return gravatarData;
    }

    public void setGravatarData(GravatarData gravatarData) {
        this.gravatarData = gravatarData;
    }

    public EmailMetrics getEmailMetrics() {
        return emailMetrics;
    }

    public void setEmailMetrics(EmailMetrics emailMetrics) {
        this.emailMetrics = emailMetrics;
    }

    public ServicePresence getServicePresence() {
        return servicePresence;
    }

    public void setServicePresence(ServicePresence servicePresence) {
        this.servicePresence = servicePresence;
    }

    public PersonaScore getPersonaScore() {
        return personaScore;
    }

    public void setPersonaScore(PersonaScore personaScore) {
        this.personaScore = personaScore;
    }

    public SecurityScore getSecurityScore() {
        return securityScore;
    }

    public void setSecurityScore(SecurityScore securityScore) {
        this.securityScore = securityScore;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
