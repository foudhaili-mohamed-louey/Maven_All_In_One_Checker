package checker.core;

public class ProxyConfig {
  private String host;
  private int port;
  private String type; // HTTP, HTTPS, SOCKS5
  private boolean isWorking;

  public ProxyConfig(String host, int port, String type) {
    this.host = host;
    this.port = port;
    this.type = type;
    this.isWorking = true;
  }

  // Parse from format: "host:port" or "host:port:type"
  public static ProxyConfig fromString(String proxyString) {
    String[] parts = proxyString.trim().split(":");
    if (parts.length >= 2) {
      String host = parts[0];
      int port = Integer.parseInt(parts[1]);
      String type = parts.length >= 3 ? parts[2].toUpperCase() : "HTTP";
      return new ProxyConfig(host, port, type);
    }
    throw new IllegalArgumentException("Invalid proxy format: " + proxyString);
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getType() {
    return type;
  }

  public boolean isWorking() {
    return isWorking;
  }

  public void setWorking(boolean working) {
    isWorking = working;
  }

  @Override
  public String toString() {
    return host + ":" + port + " (" + type + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    ProxyConfig that = (ProxyConfig) obj;
    return port == that.port && host.equals(that.host);
  }

  @Override
  public int hashCode() {
    int result = host.hashCode();
    result = 31 * result + port;
    return result;
  }
}
