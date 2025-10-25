package services;

import java.util.List;

/**
 * Result object to hold both cleaned emails and the count of removed items
 */
public class CleaningResult {
    private final List<String> cleanedEmails;
    private final int removedCount;
    
    public CleaningResult(List<String> cleanedEmails, int removedCount) {
        this.cleanedEmails = cleanedEmails;
        this.removedCount = removedCount;
    }
    
    public List<String> getCleanedEmails() {
        return cleanedEmails;
    }
    
    public int getRemovedCount() {
        return removedCount;
    }
}
