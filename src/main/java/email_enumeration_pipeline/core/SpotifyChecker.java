package email_enumeration_pipeline.core;

import email_enumeration_pipeline.utils.FormFinder;
import org.openqa.selenium.WebElement;
import java.util.Map;

public class SpotifyChecker implements Runnable {

  private final String email;

  public SpotifyChecker(String email) {
    this.email = email;
  }

  @Override
  public void run() {
    FormFinder formFinder = new FormFinder();
    try {
      String url = "https://accounts.spotify.com/en/password-reset";
      Map<String, WebElement> form = formFinder.getEmailForm(url);

      WebElement emailInput = form.get("emailInput");
      WebElement submitButton = form.get("submitButton");

      if (emailInput != null) {
        System.out.println("[Spotify] Email input found");
        emailInput.sendKeys(email);
      } else {
        System.out.println("[Spotify] No email input found.");
      }

      if (submitButton != null) {
        System.out.println("[Spotify] Submit button found");
        // submitButton.click();
      } else {
        System.out.println("[Spotify] No submit button found.");
      }

    } catch (Exception e) {
      System.out.println("[Spotify] Error: " + e.getMessage());
    } finally {
      formFinder.close();
    }
  }
}
