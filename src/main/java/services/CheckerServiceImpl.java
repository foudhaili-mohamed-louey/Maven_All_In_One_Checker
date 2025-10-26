package services;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CheckerService using SubscriptionCheckerInterface.
 */
public class CheckerServiceImpl implements CheckerService {
    
    private SubscriptionCheckerInterface subscriptionChecker;
    
    /**
     * Constructor with default SeleniumSubscriptionChecker.
     */
    public CheckerServiceImpl() {
        this.subscriptionChecker = new SeleniumSubscriptionChecker();
    }
    
    /**
     * Constructor with custom subscription checker.
     * 
     * @param subscriptionChecker The subscription checker to use
     */
    public CheckerServiceImpl(SubscriptionCheckerInterface subscriptionChecker) {
        this.subscriptionChecker = subscriptionChecker;
    }
    
    @Override
    public void initialize() {
        if (subscriptionChecker != null) {
            subscriptionChecker.initialize();
        }
    }
    
    @Override
    public void cleanup() {
        if (subscriptionChecker != null) {
            subscriptionChecker.cleanup();
        }
    }
    
    @Override
    public CheckerResult checkEmail(String email) {
        if (subscriptionChecker == null) {
            throw new IllegalStateException("Subscription checker not set");
        }
        
        try {
            boolean isValid = subscriptionChecker.checkSubscription(email);
            String status = isValid ? "Valid" : "Invalid";
            return new CheckerResult(email, isValid, status);
        } catch (Exception e) {
            return new CheckerResult(email, false, "Error: " + e.getMessage());
        }
    }
    
    @Override
    public List<CheckerResult> checkEmails(List<String> emails) {
        List<CheckerResult> results = new ArrayList<>();
        
        for (String email : emails) {
            results.add(checkEmail(email));
        }
        
        return results;
    }
    
    @Override
    public SubscriptionCheckerInterface getSubscriptionChecker() {
        return subscriptionChecker;
    }
    
    @Override
    public void setSubscriptionChecker(SubscriptionCheckerInterface checker) {
        this.subscriptionChecker = checker;
    }
}
