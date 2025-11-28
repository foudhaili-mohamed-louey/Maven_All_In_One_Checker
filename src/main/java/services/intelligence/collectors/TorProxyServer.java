package services.intelligence.collectors;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TorProxyServer {
  public static void main(String[] args) throws Exception {
    System.setProperty("socksProxyHost", "127.0.0.1");
    System.setProperty("socksProxyPort", "9150");

    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create("https://api.ipify.org"))
        .GET()
        .build();

    HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

    System.out.println("Response: " + res.body());
  }
}
