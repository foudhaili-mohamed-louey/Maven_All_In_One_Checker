package services;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object to hold checked email results
 */
public class CheckerResult {
    private final String email;
    private final boolean isValid;
    private final String status;
    
    public CheckerResult(String email, boolean isValid, String status) {
        this.email = email;
        this.isValid = isValid;
        this.status = status;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public String getStatus() {
        return status;
    }
    
    /**
     * Filter a list of CheckerResults to get only valid emails
     */
    public static List<String> getValidEmails(List<CheckerResult> results) {
        List<String> validEmails = new ArrayList<>();
        for (CheckerResult result : results) {
            if (result.isValid()) {
                validEmails.add(result.getEmail());
            }
        }
        return validEmails;
    }
    
    /**
     * Filter a list of CheckerResults to get only invalid emails
     */
    public static List<String> getInvalidEmails(List<CheckerResult> results) {
        List<String> invalidEmails = new ArrayList<>();
        for (CheckerResult result : results) {
            if (!result.isValid()) {
                invalidEmails.add(result.getEmail());
            }
        }
        return invalidEmails;
    }
}
