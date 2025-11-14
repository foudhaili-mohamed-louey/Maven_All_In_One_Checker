package services;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmailCleaningServiceImpl implements EmailCleaningService {

    private ObservableList<String> importedEmails = FXCollections.observableArrayList();
    
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);

    // ================== IMPORT / REMOVE ==================

    @Override
    public ObservableList<String> importData(File file) {
        importedEmails.clear();
        
        if (file == null || !file.exists()) {
            return importedEmails;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line != null && !line.trim().isEmpty()) {
                    importedEmails.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return importedEmails;
    }

    @Override
    public void removeDataImported() {
        importedEmails.clear();
    }

    // ================== CLEANING METHODS ==================

    @Override
    public List<String> removeDuplicates(List<String> emails) {
        return new ArrayList<>(new LinkedHashSet<>(emails));
    }

    @Override
    public List<String> removeEmptyLines(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        for (String email : emails) {
            if (email != null && !email.trim().isEmpty()) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeEmailsWithMultipleAt(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        for (String email : emails) {
            long atCount = email.chars().filter(ch -> ch == '@').count();
            if (atCount == 1) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeEmailsWithoutAt(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        for (String email : emails) {
            if (email.contains("@")) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeInvalidDomainFormat(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        Pattern pattern = Pattern.compile("^[^@]+@[^@]+\\.[A-Za-z]{2,}$");
        
        for (String email : emails) {
            if (pattern.matcher(email).matches()) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeInvalidCharacters(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        
        for (String email : emails) {
            if (EMAIL_REGEX.matcher(email).matches()) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeAdminOrBotEmails(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        String[] adminPrefixes = {"admin@", "noreply@", "no-reply@", "bot@", "do-not-reply@"};
        
        for (String email : emails) {
            String lower = email.toLowerCase();
            boolean isAdmin = false;
            
            for (String prefix : adminPrefixes) {
                if (lower.startsWith(prefix)) {
                    isAdmin = true;
                    break;
                }
            }
            
            if (!isAdmin) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeRoleBasedEmails(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        String[] roles = {
            "info@", "support@", "sales@", "contact@", "webmaster@", 
            "help@", "careers@", "jobs@", "marketing@", "service@"
        };
        
        for (String email : emails) {
            String lower = email.toLowerCase();
            boolean isRole = false;
            
            for (String role : roles) {
                if (lower.startsWith(role)) {
                    isRole = true;
                    break;
                }
            }
            
            if (!isRole) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeDisposableEmails(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        String[] disposableDomains = {
        	    "mailinator.com", "yopmail.com", "10minutemail.com", "guerrillamail.com", "tempmail.com", "throwaway.email",
        	    "getnada.com", "trashmail.com", "maildrop.cc", "dispostable.com", "fakeinbox.com", "temp-mail.org",
        	    "mytemp.email", "mailcatch.com", "mintemail.com", "moakt.com", "spambox.xyz", "spamgourmet.com",
        	    "mailnesia.com", "inboxalias.com", "anonaddy.me", "sharklasers.com", "grr.la", "guerrillamail.net",
        	    "mail-temporaire.fr", "temporarymail.com", "tempail.com", "tempmailo.com", "tempmailaddress.com", "dropmail.me",
        	    "mohmal.com", "tmail.ws", "fakemail.net", "trash-mail.com", "owlymail.com", "mailtothis.com",
        	    "spam4.me", "easytrashmail.com", "spambog.com", "spambog.de", "getairmail.com", "nowmymail.com",
        	    "nobugmail.com", "binkmail.com", "mailme24.com", "spamdecoy.net", "tempemail.co", "tempmail.de",
        	    "yopmail.net", "yopmail.fr", "guerrillamail.de", "spamfree24.org"
        	};


        
        for (String email : emails) {
            boolean disposable = false;
            String lower = email.toLowerCase();
            
            for (String domain : disposableDomains) {
                if (lower.endsWith("@" + domain)) {
                    disposable = true;
                    break;
                }
            }
            
            if (!disposable) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeNonExistentDomains(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        
        for (String email : emails) {
            try {
                int atIndex = email.indexOf("@");
                if (atIndex > 0 && atIndex < email.length() - 1) {
                    String domain = email.substring(atIndex + 1);
                    InetAddress.getByName(domain);
                    cleaned.add(email);
                }
            } catch (Exception e) {
                // Domain doesn't exist or can't be resolved - skip it
            }
        }
        return cleaned;
    }

    @Override
    public List<String> removeTooShortOrTooLongEmails(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        
        for (String email : emails) {
            int length = email.length();
            if (length >= 6 && length <= 254) {
                cleaned.add(email);
            }
        }
        return cleaned;
    }

   
    @Override
    public List<String> removeInvalidOrFakeTLDs(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        Pattern tldPattern = Pattern.compile("\\.[A-Za-z]{2,}$");
        
        for (String email : emails) {
            Matcher matcher = tldPattern.matcher(email);
            if (matcher.find()) {
                String tld = matcher.group().substring(1).toLowerCase();
                
                // Check if TLD length is reasonable (2-10 characters)
                if (tld.length() >= 2 && tld.length() <= 10) {
                    cleaned.add(email);
                }
            }
        }
        return cleaned;
    }

    @Override
    public List<String> trimAndNormalize(List<String> emails) {
        List<String> cleaned = new ArrayList<>();
        
        for (String email : emails) {
            String normalized = email.trim().toLowerCase().replaceAll("\\s+", "");
            if (!normalized.isEmpty()) {
                cleaned.add(normalized);
            }
        }
        return cleaned;
    }
    
    //Export Data
    @Override
    public boolean exportCleanedEmails(List<String> emails, File file) throws IOException {
        if (emails == null || file == null) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            
            if (fileName.endsWith(".csv")) {
                // CSV format: write emails with proper escaping
                for (String email : emails) {
                    // Escape quotes and wrap in quotes if email contains comma or quote
                    if (email.contains(",") || email.contains("\"")) {
                        String escaped = email.replace("\"", "\"\"");
                        writer.write("\"" + escaped + "\"");
                    } else {
                        writer.write(email);
                    }
                    writer.newLine();
                }
            } else if (fileName.endsWith(".txt")) {
                // TXT format: plain text, one email per line
                for (String email : emails) {
                    writer.write(email);
                    writer.newLine();
                }
            } else {
                // Unsupported format
                return false;
            }
            
            writer.flush();
            return true;
            
        } catch (IOException e) {
            System.err.println("Error exporting emails: " + e.getMessage());
            throw e;
        }
    }


 
}
