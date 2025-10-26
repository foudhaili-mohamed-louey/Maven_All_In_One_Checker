package services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Selenium-based implementation of SubscriptionCheckerInterface.
 * Uses a web browser to check email subscription status.
 */
public class SeleniumSubscriptionChecker implements SubscriptionCheckerInterface {
    
    private WebDriver driver;
    private String checkerUrl;
    private static final int DEFAULT_TIMEOUT = 10;
    
    /**
     * Constructor with default checker URL.
     */
    public SeleniumSubscriptionChecker() {
        this("https://www.verifyemailaddress.org/");
    }
    
    /**
     * Constructor with custom checker URL.
     * 
     * @param checkerUrl The URL to use for checking subscriptions
     */
    public SeleniumSubscriptionChecker(String checkerUrl) {
        this.checkerUrl = checkerUrl;
    }
    
    @Override
    public void initialize() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Run in headless mode
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_TIMEOUT));
        }
    }
    
    @Override
    public void cleanup() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
    
    @Override
    public boolean checkSubscription(String email) {
        if (driver == null) {
            throw new IllegalStateException("Checker not initialized. Call initialize() first.");
        }
        
        try {
            // Navigate to the checker URL
            driver.get(checkerUrl);
            
            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
            
            // Find the email input field (this is a generic implementation)
            // You may need to adjust selectors based on the actual checker website
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='email'], input[type='text'], input[name*='email']")
            ));
            
            // Clear and enter email
            emailInput.clear();
            emailInput.sendKeys(email);
            
            // Find and click the submit button
            WebElement submitButton = driver.findElement(
                By.cssSelector("button[type='submit'], input[type='submit'], button")
            );
            submitButton.click();
            
            // Wait for results (this is generic - adjust based on actual site)
            Thread.sleep(2000); // Simple wait for demonstration
            
            // Check for success indicators (generic implementation)
            String pageSource = driver.getPageSource().toLowerCase();
            boolean isValid = pageSource.contains("valid") || 
                            pageSource.contains("deliverable") ||
                            pageSource.contains("success");
            
            return isValid;
            
        } catch (Exception e) {
            System.err.println("Error checking email " + email + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Boolean> checkSubscriptions(List<String> emails) {
        List<Boolean> results = new ArrayList<>();
        
        for (String email : emails) {
            results.add(checkSubscription(email));
        }
        
        return results;
    }
    
    @Override
    public String getCheckerUrl() {
        return checkerUrl;
    }
    
    @Override
    public void setCheckerUrl(String url) {
        this.checkerUrl = url;
    }
}
