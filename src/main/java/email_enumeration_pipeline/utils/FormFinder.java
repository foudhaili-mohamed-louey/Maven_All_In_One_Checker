package email_enumeration_pipeline.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormFinder {

  private final WebDriver driver;

  public FormFinder() {
    ChromeOptions options = new ChromeOptions();
    // Headless mode (comment out to debug visually)
    options.addArguments("--headless=new");
    options.addArguments("--disable-gpu");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    this.driver = new ChromeDriver(options);
  }

  public WebDriver getDriver() {
    return driver;
  }

  public Map<String, WebElement> getEmailForm(String url) {
    Map<String, WebElement> formElements = new HashMap<>();

    try {
      driver.get(url);

      // Wait for at least one input to appear (max 10 seconds)
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("input")));

      List<WebElement> inputs = driver.findElements(By.tagName("input"));
      WebElement emailInput = null;
      WebElement submitButton = null;

      // Find probable email input
      for (WebElement input : inputs) {
        if (!input.isDisplayed() || !input.isEnabled())
          continue;

        String type = input.getAttribute("type");
        String name = input.getAttribute("name");
        String placeholder = input.getAttribute("placeholder");

        boolean isEmailType = "email".equalsIgnoreCase(type);
        boolean nameLooksLikeEmail = name != null && name.toLowerCase().contains("email");
        boolean placeholderLooksLikeEmail = placeholder != null &&
            (placeholder.toLowerCase().contains("email") || placeholder.toLowerCase().contains("username"));

        if (isEmailType || nameLooksLikeEmail || placeholderLooksLikeEmail) {
          emailInput = input;
          break;
        }
      }

      // If email input found, find nearest submit button
      if (emailInput != null) {
        WebElement parent = emailInput;
        // Traverse ancestors to find a button
        while (parent != null) {
          List<WebElement> buttons = parent.findElements(By.tagName("button"));
          for (WebElement btn : buttons) {
            if (btn.isDisplayed() && btn.isEnabled()) {
              submitButton = btn;
              break;
            }
          }
          if (submitButton != null)
            break;
          parent = parent.findElement(By.xpath(".."));
        }

        // fallback: find any visible submit input
        if (submitButton == null) {
          for (WebElement input : inputs) {
            if ("submit".equalsIgnoreCase(input.getAttribute("type")) && input.isDisplayed() && input.isEnabled()) {
              submitButton = input;
              break;
            }
          }
        }
      }

      formElements.put("emailInput", emailInput);
      formElements.put("submitButton", submitButton);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return formElements;
  }

  public void close() {
    driver.quit();
  }
}
