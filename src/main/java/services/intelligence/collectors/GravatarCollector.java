package services.intelligence.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import services.intelligence.models.GravatarData;
import services.proxy.TorHttpClient;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects Gravatar profile data for email addresses
 * Supports routing through Tor proxy for enhanced privacy
 */
public class GravatarCollector {
    private static final String GRAVATAR_API_URL = "https://www.gravatar.com/";
    private final OkHttpClient httpClient;
    private final TorHttpClient torClient;

    /**
     * Create GravatarCollector with direct HTTP connection
     */
    public GravatarCollector() {
        this(null);
    }
    
    /**
     * Create GravatarCollector with optional Tor support
     * @param torClient Tor-enabled HTTP client (null for direct connection)
     */
    public GravatarCollector(TorHttpClient torClient) {
        this.torClient = torClient;
        
        if (torClient != null) {
            this.httpClient = torClient.getClient();
        } else {
            this.httpClient = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .build();
        }
    }

    /**
     * Fetches Gravatar profile data
     */
    public GravatarData collect(String email) {
        try {
            String hash = md5Hash(email.trim().toLowerCase());
            String url = GRAVATAR_API_URL + hash + ".json";

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    return parseGravatarResponse(jsonData);
                } else {
                    // Profile doesn't exist or not public
                    return GravatarData.empty();
                }
            }
        } catch (Exception e) {
            // Graceful degradation - return empty data
            return GravatarData.empty();
        }
    }

    private GravatarData parseGravatarResponse(String jsonData) {
        GravatarData data = new GravatarData();
        data.setProfileExists(true);

        try {
            JsonObject root = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray entries = root.getAsJsonArray("entry");

            if (entries != null && entries.size() > 0) {
                JsonObject entry = entries.get(0).getAsJsonObject();

                // Extract display name
                if (entry.has("displayName")) {
                    data.setDisplayName(entry.get("displayName").getAsString());
                }

                // Extract profile image URL
                if (entry.has("thumbnailUrl")) {
                    data.setProfileImageUrl(entry.get("thumbnailUrl").getAsString());
                }

                // Extract linked accounts
                if (entry.has("accounts")) {
                    JsonArray accounts = entry.getAsJsonArray("accounts");
                    List<String> linkedAccounts = new ArrayList<>();
                    for (JsonElement account : accounts) {
                        JsonObject acc = account.getAsJsonObject();
                        if (acc.has("shortname")) {
                            linkedAccounts.add(acc.get("shortname").getAsString());
                        }
                    }
                    data.setLinkedAccounts(linkedAccounts);
                }
            }
        } catch (Exception e) {
            // If parsing fails, still return that profile exists
        }

        return data;
    }

    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
