package server.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestSender {

    private RequestSender() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = Logger.getLogger(RequestSender.class.getName());
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public static void sendAsyncGet(String uri, String senderIp) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri))
                    .header("Sender-Ip", senderIp)
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public static HttpResponse<String> sendGet(String uri, String senderIp) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri))
                    .header("Sender-Ip", senderIp)
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
        return null;
    }

    public static void sendPostAsync(String uri, String content) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(content))
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public static HttpResponse<String> sendPost(String uri, String content) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(content))
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
        }
        return null;
    }

}
