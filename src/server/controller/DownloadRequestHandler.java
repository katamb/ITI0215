package server.controller;

import com.sun.net.httpserver.HttpExchange;
import server.dto.RoutingInfo;
import server.dto.ServersInfo;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Server.AVAILABLE_SERVERS;
import static server.Server.DEFAULT_ENCODING;
import static server.Server.LAZYNESS;
import static server.Server.ROUTINGS_MADE;
import static server.util.General.getRandomDouble;
import static server.util.General.getRoutingFromId;
import static server.util.HttpExchangeDataExtractor.getClientUrl;
import static server.util.HttpExchangeDataExtractor.queryToMap;
import static server.util.RequestSender.sendGet;
import static server.util.RequestSender.sendPost;
import static server.util.ResponseProvider.badRequestResponse;

public class DownloadRequestHandler {

    private static final Logger logger = Logger.getLogger(DownloadRequestHandler.class.getName());

    public static void handleDownloadRequest(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = queryToMap(exchange);
        String id = queryParams.get("id");
        String url = queryParams.get("url");
        if (!isDownloadRequestValid(exchange, id, url)) {
            return;
        }

        url = URLDecoder.decode(url, DEFAULT_ENCODING);
        RoutingInfo routingInfo = new RoutingInfo(id, getClientUrl(exchange), null);
        ROUTINGS_MADE.add(routingInfo);

        if (getRandomDouble() > LAZYNESS) {
            logger.log(Level.INFO, "Downloading file for request id " + id);
            downloadFileAndSendItBack(exchange, id, url);
        } else {
            logger.log(Level.INFO, "Forwarding request with id " + id);
            forwardMessage(id, url);
        }
    }

    private static boolean isDownloadRequestValid(HttpExchange exchange, String id, String url) throws IOException {
        if (id == null || url == null) {
            badRequestResponse(exchange, "Error: Query parameters not correct!");
            return false;
        }

        // Check if this request has been already forwarded
        if (getRoutingFromId(id) != null) {
            logger.log(Level.INFO, "This node has already forwarded this request!");
            return false;
        }
        return true;
    }

    private static void downloadFileAndSendItBack(HttpExchange exchange, String id, String url) throws IOException {
        // Download the file
        HttpResponse<String> response = sendGet(url);
        if (response == null) {
            logger.log(Level.WARNING, "Unable to query given URL!");
            badRequestResponse(exchange, "Error: Unable to query given URL!");
            return;
        }

        int responseCode = response.statusCode();
        String responseJson = createResponseJson(response, responseCode);
        RoutingInfo existingRoutingInfo = getRoutingFromId(id);
        if (existingRoutingInfo == null) {
            logger.log(Level.WARNING, "No info where to send the response!");  // Should never get here
            badRequestResponse(exchange, "Error: No info where to send the response!");
            return;
        }

        String uri = existingRoutingInfo.getDownloadIp() + "/file?id=" + id;
        sendPost(uri, responseJson);
    }

    private static String createResponseJson(HttpResponse<String> response, int responseCode) {
        if (responseCode != 200) {
            return "{" +
                   "\"status\": " + Integer.toString(responseCode) +
                   " }";
        } else {
            String encodedBody = Base64.getEncoder().encodeToString(response.body().getBytes());
            String contentType = response.headers().map().get("content-type").get(0);
            return "{" +
                   "\"status\": " + Integer.toString(responseCode) +
                   "\"mime-type\": " + contentType +
                   "\"content\": " + encodedBody +
                   " }";
        }
    }

    private static void forwardMessage(String id, String url) {
        for (ServersInfo server : AVAILABLE_SERVERS) {
            String uri = "http://" + server.getIp() + ":" + server.getPort()
                    + "/download?id=" + id
                    + "&url=" + url;
            sendGet(uri);
        }
    }

}
