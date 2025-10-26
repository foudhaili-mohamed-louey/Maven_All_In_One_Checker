package checker.core;

public class RateLimiter {
  private final long delayMillis;
  private long lastRequestTime;

  public RateLimiter(int delaySeconds) {
    this.delayMillis = delaySeconds * 1000L;
    this.lastRequestTime = 0;
  }

  public synchronized void waitIfNeeded() {
    long currentTime = System.currentTimeMillis();
    long timeSinceLastRequest = currentTime - lastRequestTime;

    if (timeSinceLastRequest < delayMillis) {
      long sleepTime = delayMillis - timeSinceLastRequest;
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    lastRequestTime = System.currentTimeMillis();
  }
}
