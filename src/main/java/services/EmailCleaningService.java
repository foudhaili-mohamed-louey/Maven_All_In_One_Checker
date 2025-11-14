package services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.collections.ObservableList;


public interface EmailCleaningService {

    // ========== Import / Export Operations ==========

    ObservableList<String> importData(File file);

    // ========== Remove Imported Data ==========
    void removeDataImported();

    // ========== Basic Cleaning Functions ==========

    /**
     * Removes duplicate email addresses while preserving order.
     * 
     * @param emails List of email addresses to process
     * @return List with duplicates removed
     * 
     * Example: ["a@gmail.com", "a@gmail.com"] → ["a@gmail.com"]
     */
    List<String> removeDuplicates(List<String> emails);

    /**
     * Removes empty or blank lines from the email list.
     * 
     * @param emails List of email addresses to process
     * @return List without empty entries
     * 
     * Example: ["", "test@gmail.com", "  "] → ["test@gmail.com"]
     */
    List<String> removeEmptyLines(List<String> emails);

    /**
     * Trims whitespace, converts to lowercase, and removes invisible characters.
     * This should typically be called early in the cleaning pipeline.
     * 
     * @param emails List of email addresses to normalize
     * @return List of normalized email addresses
     * 
     * Example: ["  USER@GMAIL.COM  "] → ["user@gmail.com"]
     */
    List<String> trimAndNormalize(List<String> emails);

    // ========== Format Validation ==========

    /**
     * Removes emails without an '@' character.
     * 
     * @param emails List of email addresses to validate
     * @return List containing only emails with '@' symbol
     * 
     * Example: ["invalidemail.com"] → removed
     */
    List<String> removeEmailsWithoutAt(List<String> emails);

    /**
     * Removes emails containing more than one '@' character.
     * 
     * @param emails List of email addresses to validate
     * @return List containing only emails with exactly one '@'
     * 
     * Example: ["a@@gmail.com", "user@test@domain.com"] → removed
     */
    List<String> removeEmailsWithMultipleAt(List<String> emails);

    /**
     * Removes emails with invalid characters (spaces, commas, special chars).
     * Only allows: alphanumeric, dots, underscores, hyphens, plus signs, and percent.
     * 
     * @param emails List of email addresses to validate
     * @return List containing only emails with valid characters
     * 
     * Example: ["user name@gmail.com", "user,test@domain.com"] → removed
     */
    List<String> removeInvalidCharacters(List<String> emails);

    // ========== Advanced Filtering ==========

    /**
     * Removes role-based or generic business emails.
     * Targets: info@, support@, sales@, contact@, webmaster@, help@, careers@, etc.
     * 
     * @param emails List of email addresses to filter
     * @return List without role-based emails
     * 
     * Example: ["info@domain.com", "support@company.com"] → removed
     */
    List<String> removeRoleBasedEmails(List<String> emails);

    /**
     * Removes administrative or bot emails.
     * Targets: admin@, noreply@, no-reply@, bot@, do-not-reply@, etc.
     * 
     * @param emails List of email addresses to filter
     * @return List without admin/bot emails
     * 
     * Example: ["admin@domain.com", "noreply@site.com"] → removed
     */
    List<String> removeAdminOrBotEmails(List<String> emails);

    /**
     * Removes disposable or temporary email addresses.
     * Targets domains: mailinator, yopmail, 10minutemail, guerrillamail, etc.
     * 
     * @param emails List of email addresses to filter
     * @return List without disposable emails
     * 
     * Example: ["user@mailinator.com", "test@yopmail.com"] → removed
     */
    List<String> removeDisposableEmails(List<String> emails);

    // ========== Domain Validation ==========

    /**
     * Removes emails with invalid or fake top-level domains (TLDs).
     * Validates TLD length and basic structure.
     * 
     * @param emails List of email addresses to validate
     * @return List containing only emails with valid TLDs
     * 
     * Example: ["user@domain.zzz", "user@test.x"] → removed
     */
    List<String> removeInvalidOrFakeTLDs(List<String> emails);

    /**
     * Removes emails whose domains don't exist or can't be resolved via DNS.
     * This is a network operation and may be slow for large lists.
     * 
     * @param emails List of email addresses to validate
     * @return List containing only emails with resolvable domains
     * 
     * Example: ["user@fakedomain123.zzz"] → removed
     */
    List<String> removeNonExistentDomains(List<String> emails);

	List<String> removeInvalidDomainFormat(List<String> emails);

	List<String> removeTooShortOrTooLongEmails(List<String> emails);
	
	boolean exportCleanedEmails(List<String> emails, File file) throws IOException;
}
