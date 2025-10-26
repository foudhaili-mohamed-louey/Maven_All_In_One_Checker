package services;

import java.util.List;

/**
 * Service interface for email checking operations.
 * Provides functionality to verify email validity using subscription checkers.
 */
public interface CheckerService {
    
    /**
     * Check a single email address for validity.
     * 
     * @param email The email address to check
     * @return CheckerResult containing the validation result
     */
    CheckerResult checkEmail(String email);
    
    /**
     * Check multiple email addresses for validity.
     * 
     * @param emails List of email addresses to check
     * @return List of CheckerResult objects
     */
    List<CheckerResult> checkEmails(List<String> emails);
    
    /**
     * Initialize the checker service.
     */
    void initialize();
    
    /**
     * Clean up resources.
     */
    void cleanup();
    
    /**
     * Get the subscription checker being used.
     * 
     * @return The SubscriptionCheckerInterface instance
     */
    SubscriptionCheckerInterface getSubscriptionChecker();
    
    /**
     * Set the subscription checker to use.
     * 
     * @param checker The SubscriptionCheckerInterface instance
     */
    void setSubscriptionChecker(SubscriptionCheckerInterface checker);
}
