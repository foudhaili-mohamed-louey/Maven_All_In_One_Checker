package checker.services;

import checker.models.SubscriptionStatus;
import checker.core.ProxyConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;

public class NetflixChecker extends AbstractServiceChecker {

  private static final String RESET_URL = "https://www.netflix.com/LoginHelp";

  @Override
  public String getServiceName() {
    return "Netflix";
  }

  @Override
  protected SubscriptionStatus performCheck(String email, ProxyConfig proxy) throws Exception {
    try (CloseableHttpClient client = createHttpClient(proxy)) {

      // Method 1: Try password reset endpoint
      HttpPost post = new HttpPost(RESET_URL);
      post.setHeader("User-Agent", USER_AGENT);
      post.setHeader("Content-Type", "application/x-www-form-urlencoded");
      post.setHeader("Accept", "text/html,application/xhtml+xml");

      // Form data for Netflix password reset
      String formData = "email=" + email + "&forgotPassword=Reset+Password";
      post.setEntity(new StringEntity(formData, StandardCharsets.UTF_8));

      try (CloseableHttpResponse response = client.execute(post)) {
        int statusCode = response.getCode();
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        // Analyze response
        return analyzeNetflixResponse(statusCode, responseBody);
      }
    }
  }

  private SubscriptionStatus analyzeNetflixResponse(int statusCode, String response) {
    String responseLower = response.toLowerCase();

    // Netflix patterns for registered accounts
    if (responseLower.contains("we sent an email") ||
        responseLower.contains("check your email") ||
        responseLower.contains("email has been sent") ||
        responseLower.contains("reset link")) {
      return SubscriptionStatus.REGISTERED;
    }

    // Patterns for non-registered accounts
    if (responseLower.contains("cannot find") ||
        responseLower.contains("email not found") ||
        responseLower.contains("no account") ||
        responseLower.contains("not associated")) {
      return SubscriptionStatus.NOT_REGISTERED;
    }

    // If we can't determine, return UNKNOWN
    return SubscriptionStatus.UNKNOWN;
  }
}
