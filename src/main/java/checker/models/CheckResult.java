package checker.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import checker.models.SubscriptionStatus;

public class CheckResult {
  private String email;
  private Map<String, SubscriptionStatus> subscriptions;
  private LocalDateTime checkedAt;

  public CheckResult(String email) {
    this.email = email;
    this.subscriptions = new HashMap<>();
    this.checkedAt = LocalDateTime.now();
  }

  public void addResult(String service, SubscriptionStatus status) {
    subscriptions.put(service, status);
  }

  public String getEmail() {
    return email;
  }

  public Map<String, SubscriptionStatus> getSubscriptions() {
    return subscriptions;
  }

  public LocalDateTime getCheckedAt() {
    return checkedAt;
  }

  @Override
  public String toString() {
    return "CheckResult{email='" + email + "', subscriptions=" + subscriptions + "}";
  }
}
