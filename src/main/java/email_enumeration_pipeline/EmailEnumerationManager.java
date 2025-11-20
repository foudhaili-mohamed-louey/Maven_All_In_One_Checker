package email_enumeration_pipeline;

import email_enumeration_pipeline.core.NetflixChecker;
import email_enumeration_pipeline.core.SpotifyChecker;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EmailEnumerationManager {

  private final int threadCount;

  public EmailEnumerationManager(int threadCount) {
    this.threadCount = threadCount;
  }

  public EmailEnumerationManager() {
    this.threadCount = 2;
  }

  public void checkEmails(List<String> emails) {
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    try {
      for (String email : emails) {
        executor.submit(new NetflixChecker(email));
        executor.submit(new SpotifyChecker(email));
      }

      executor.shutdown();

      if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
        executor.shutdownNow();
      }

      System.out.println("All checks completed.");

    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
