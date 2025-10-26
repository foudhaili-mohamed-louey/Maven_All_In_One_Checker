package services;

import java.util.List;

/**
 * Interface for checking email subscription status.
 * Provides functionality to verify emails using web-based checkers.
 */
public interface SubscriptionCheckerInterface {
    
    /**
     * Check if an email address is subscribed to a service.
     * 
     * @param email The email address to check
     * @return true if the email is subscribed, false otherwise
     */
    boolean checkSubscription(String email);
    
    /**
     * Check multiple email addresses for subscription status.
     * 
     * @param emails List of email addresses to check
     * @return List of subscription statuses corresponding to input emails
     */
    List<Boolean> checkSubscriptions(List<String> emails);
    
    /**
     * Get the checker service URL.
     * 
     * @return The URL used for checking subscriptions
     */
    String getCheckerUrl();
    
    /**
     * Set the checker service URL.
     * 
     * @param url The URL to use for checking subscriptions
     */
    void setCheckerUrl(String url);
    
    /**
     * Initialize the checker service (e.g., start browser, configure driver).
     */
    void initialize();
    
    /**
     * Clean up resources (e.g., close browser, release driver).
     */
    void cleanup();
}
