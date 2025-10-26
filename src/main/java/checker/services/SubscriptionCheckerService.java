package checker.services;

import checker.core.ProxyManager;
import checker.core.RateLimiter;
import checker.models.CheckResult;
import checker.core.ProxyConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SubscriptionCheckerService {

  private final ProxyManager proxyManager;
  private final RateLimiter rateLimiter;
  private final List<SubscriptionChecker> checkers;
  private final ExecutorService executorService;

  public SubscriptionCheckerService(ProxyManager proxyManager, int delaySeconds, int threads) {
    this.proxyManager = proxyManager;
    this.rateLimiter = new RateLimiter(delaySeconds);
    this.checkers = new ArrayList<>();
    this.executorService = Executors.newFixedThreadPool(threads);

    // Register all service checkers
    registerChecker(new NetflixChecker());
    registerChecker(new SpotifyChecker());
  }

  public void registerChecker(SubscriptionChecker checker) {
    checkers.add(checker);
  }

  /**
   * Check a single email against all services
   */
  public CheckResult checkEmail(String email) {
    CheckResult result = new CheckResult(email);

    for (SubscriptionChecker checker : checkers) {
      rateLimiter.waitIfNeeded(); // Rate limiting

      ProxyConfig proxy = proxyManager.getNextProxy();

      try {
        CheckResult.SubscriptionStatus status = checker.check(email, proxy);
        result.addResult(checker.getServiceName(), status);

        System.out.println(email + " - " + checker.getServiceName() + ": " + status);

      } catch (Exception e) {
        result.addResult(checker.getServiceName(), CheckResult.SubscriptionStatus.ERROR);
        if (proxy != null) {
          proxyManager.markProxyAsFailed(proxy);
        }
      }
    }

    return result;
  }

  /**
   * Check multiple emails (batch processing)
   */
  public List<CheckResult> checkEmails(List<String> emails, ProgressCallback callback) {
    List<Future<CheckResult>> futures = new ArrayList<>();

    for (String email : emails) {
      Future<CheckResult> future = executorService.submit(() -> checkEmail(email));
      futures.add(future);
    }

    List<CheckResult> results = new ArrayList<>();
    int completed = 0;

    for (Future<CheckResult> future : futures) {
      try {
        CheckResult result = future.get();
        results.add(result);
        completed++;

        if (callback != null) {
          callback.onProgress(completed, emails.size());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return results;
  }

  public void shutdown() {
    executorService.shutdown();
  }

  @FunctionalInterface
  public interface ProgressCallback {
    void onProgress(int completed, int total);
  }
}
