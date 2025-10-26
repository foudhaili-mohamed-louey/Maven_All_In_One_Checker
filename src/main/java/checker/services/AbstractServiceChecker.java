package checker.services;

import checker.models.SubscriptionStatus;
import checker.core.ProxyConfig;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServiceChecker {

  protected static final int TIMEOUT_SECONDS = 15;
  protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

  public SubscriptionStatus check(String email, ProxyConfig proxy) {
    try {
      return performCheck(email, proxy);
    } catch (Exception e) {
      System.err.println("Error checking " + getServiceName() + " for " + email + ": " + e.getMessage());
      return SubscriptionStatus.ERROR;
    }
  }

  protected abstract SubscriptionStatus performCheck(String email, ProxyConfig proxy) throws Exception;

  /**
   * Create HTTP client with optional proxy
   */
  protected CloseableHttpClient createHttpClient(ProxyConfig proxy) {
    RequestConfig.Builder configBuilder = RequestConfig.custom()
        .setConnectTimeout(Timeout.of(TIMEOUT_SECONDS, TimeUnit.SECONDS))
        .setResponseTimeout(Timeout.of(TIMEOUT_SECONDS, TimeUnit.SECONDS));

    if (proxy != null) {
      HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort());
      configBuilder.setProxy(proxyHost);
    }

    return HttpClients.custom()
        .setDefaultRequestConfig(configBuilder.build())
        .build();
  }

  /**
   * Send POST request and get response
   */
  protected String sendPostRequest(CloseableHttpClient client, String url,
      String postData, String contentType) throws Exception {
    HttpPost post = new HttpPost(url);
    post.setHeader("User-Agent", USER_AGENT);
    post.setHeader("Content-Type", contentType);
    post.setHeader("Accept", "*/*");

    if (postData != null) {
      post.setEntity(new StringEntity(postData, StandardCharsets.UTF_8));
    }

    try (CloseableHttpResponse response = client.execute(post)) {
      return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }
  }
}
