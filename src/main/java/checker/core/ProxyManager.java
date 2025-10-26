package checker.core;

import checker.models.ProxyConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyManager {
  private List<ProxyConfig> proxies;
  private AtomicInteger currentIndex;
  private List<ProxyConfig> blacklistedProxies;

  public ProxyManager() {
    this.proxies = new ArrayList<>();
    this.currentIndex = new AtomicInteger(0);
    this.blacklistedProxies = new ArrayList<>();
  }

  // Load proxies from file (format: host:port or host:port:type)
  public void loadProxiesFromFile(String filePath) throws IOException {
    List<String> lines = Files.readAllLines(Path.of(filePath));

    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty() || line.startsWith("#")) {
        continue; // Skip empty lines and comments
      }

      try {
        ProxyConfig proxy = ProxyConfig.fromString(line);
        proxies.add(proxy);
      } catch (Exception e) {
        System.err.println("Invalid proxy format: " + line);
      }
    }

    System.out.println("Loaded " + proxies.size() + " proxies");
  }

  // Get next working proxy (round-robin)
  public synchronized ProxyConfig getNextProxy() {
    if (proxies.isEmpty()) {
      return null;
    }

    int attempts = 0;
    while (attempts < proxies.size()) {
      int index = currentIndex.getAndIncrement() % proxies.size();
      ProxyConfig proxy = proxies.get(index);

      if (proxy.isWorking() && !blacklistedProxies.contains(proxy)) {
        return proxy;
      }
      attempts++;
    }

    return null; // All proxies are blacklisted
  }

  // Mark proxy as failed
  public void markProxyAsFailed(ProxyConfig proxy) {
    proxy.setWorking(false);
    if (!blacklistedProxies.contains(proxy)) {
      blacklistedProxies.add(proxy);
      System.out.println("Blacklisted proxy: " + proxy);
    }
  }

  public int getTotalProxies() {
    return proxies.size();
  }

  public int getWorkingProxies() {
    return (int) proxies.stream().filter(ProxyConfig::isWorking).count();
  }
}
