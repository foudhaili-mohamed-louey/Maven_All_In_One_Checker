package services;

import services.intelligence.analysis.PersonaAnalyzer;
import services.intelligence.analysis.SecurityScorer;
import services.intelligence.collectors.EmailPatternAnalyzer;
import services.intelligence.collectors.GravatarCollector;
import services.intelligence.collectors.ServicePresenceChecker;
import services.intelligence.models.*;
import services.intelligence.reporting.HTMLReportGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Implementation of email intelligence service
 */
public class EmailIntelligenceServiceImpl implements EmailIntelligenceService {
    
    private final ExecutorService executorService;
    private final GravatarCollector gravatarCollector;
    private final EmailPatternAnalyzer emailAnalyzer;
    private final ServicePresenceChecker serviceChecker;
    private final PersonaAnalyzer personaAnalyzer;
    private final SecurityScorer securityScorer;
    private final HTMLReportGenerator reportGenerator;

    public EmailIntelligenceServiceImpl() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.gravatarCollector = new GravatarCollector();
        this.emailAnalyzer = new EmailPatternAnalyzer();
        this.serviceChecker = new ServicePresenceChecker();
        this.personaAnalyzer = new PersonaAnalyzer();
        this.securityScorer = new SecurityScorer();
        this.reportGenerator = new HTMLReportGenerator();
    }

    @Override
    public CompletableFuture<EmailIntelligenceProfile> analyzeEmail(String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Collect data from various sources
                GravatarData gravatar = collectGravatarData(email);
                EmailMetrics metrics = emailAnalyzer.analyze(email);
                ServicePresence services = serviceChecker.checkServices(email);
                
                // Analyze and score
                PersonaScore persona = personaAnalyzer.buildPersona(gravatar, metrics, services);
                SecurityScore security = securityScorer.calculateScore(metrics, services);
                
                // Build profile
                EmailIntelligenceProfile profile = new EmailIntelligenceProfile(
                    email, gravatar, metrics, services, persona
                );
                profile.setSecurityScore(security);
                
                return profile;
            } catch (Exception e) {
                // Graceful degradation - return basic profile
                EmailIntelligenceProfile profile = new EmailIntelligenceProfile();
                profile.setEmail(email);
                profile.setEmailMetrics(emailAnalyzer.analyze(email));
                return profile;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<EmailIntelligenceProfile>> analyzeEmails(List<String> emails) {
        List<CompletableFuture<EmailIntelligenceProfile>> futures = emails.stream()
                .map(this::analyzeEmail)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    @Override
    public String generateHTMLReport(List<EmailIntelligenceProfile> profiles) {
        return reportGenerator.generateReport(profiles);
    }

    /**
     * Collects Gravatar data with error handling
     */
    private GravatarData collectGravatarData(String email) {
        try {
            return gravatarCollector.collect(email);
        } catch (Exception e) {
            // Log warning and return empty data
            System.err.println("Gravatar lookup failed for " + email + ": " + e.getMessage());
            return GravatarData.empty();
        }
    }

    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
