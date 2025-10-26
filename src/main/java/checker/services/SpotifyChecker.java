package checker.services;

import checker.models.SubscriptionStatus;
import checker.models.ProxyConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class SpotifyChecker extends AbstractServiceChecker {

  private static final String RESET_URL = "https://accounts.spotify.com/api/password-reset";

  @Override
  public String getServiceName() {
    return "Spotify";
  }

  @Override
  protected SubscriptionStatus performCheck(String email, ProxyConfig proxy) throws Exception {
    try (CloseableHttpClient client = createHttpClient(proxy)) {

      // Spotify uses JSON API
      JsonObject jsonPayload = new JsonObject();
      jsonPayload.addProperty("email", email);

      String response = sendPostRequest(client, RESET_URL,
          jsonPayload.toString(),
          "application/json");

      return analyzeSpotifyResponse(response);
    }
  }

  private SubscriptionStatus analyzeSpotifyResponse(String response) {
    try {
      JsonObject json = JsonParser.parseString(response).getAsJsonObject();

      // Check for success/error indicators
      if (json.has("status")) {
        int status = json.get("status").getAsInt();

        if (status == 1 || status == 200) {
          return SubscriptionStatus.REGISTERED;
        } else if (status == 404 || status == 400) {
          return SubscriptionStatus.NOT_REGISTERED;
        }
      }

      // Alternative: check response message
      if (json.has("error")) {
        String error = json.get("error").getAsString().toLowerCase();
        if (error.contains("not found") || error.contains("invalid email")) {
          return SubscriptionStatus.NOT_REGISTERED;
        }
      }

      if (json.has("success") && json.get("success").getAsBoolean()) {
        return SubscriptionStatus.REGISTERED;
      }

    } catch (Exception e) {
      // If response is HTML instead of JSON, analyze text
      String responseLower = response.toLowerCase();
      if (responseLower.contains("email sent") || responseLower.contains("reset link")) {
        return SubscriptionStatus.REGISTERED;
      }
      if (responseLower.contains("not found") || responseLower.contains("no account")) {
        return SubscriptionStatus.NOT_REGISTERED;
      }
    }

    return SubscriptionStatus.UNKNOWN;
  }
}
