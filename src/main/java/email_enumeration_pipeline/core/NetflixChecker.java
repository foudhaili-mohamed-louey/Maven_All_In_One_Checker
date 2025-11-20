
package email_enumeration_pipeline.core;

import email_enumeration_pipeline.utils.FormFinder;
import org.openqa.selenium.WebElement;
import java.util.Map;

public class NetflixChecker implements Runnable {

  private final String email;

  public NetflixChecker(String email) {
    this.email = email;
  }

  @Override
  public void run() {
    FormFinder formFinder = new FormFinder();
    try {
      String url = "https://www.netflix.com/tn-en/loginhelp";
      Map<String, WebElement> form = formFinder.getEmailForm(url);

      WebElement emailInput = form.get("emailInput");
      WebElement submitButton = form.get("submitButton");

      if (emailInput != null) {
        System.out.println("[Netflix] Email input found");
        emailInput.sendKeys(email);
      } else {
        System.out.println("[Netflix] No email input found.");
      }

      if (submitButton != null) {
        System.out.println("[Netflix] Submit button found");
        // submitButton.click();
      } else {
        System.out.println("[Netflix] No submit button found.");
      }

    } catch (Exception e) {
      System.out.println("[Netflix] Error: " + e.getMessage());
    } finally {
      formFinder.close();
    }
  }
}
