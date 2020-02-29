package server.controller;

import com.sun.net.httpserver.HttpExchange;
import server.Server;
import server.dto.RequestsInfo;
import server.dto.RoutingInfo;
import server.dto.ServersInfo;
import server.exception.BadRequestException;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Server.*;
import static server.util.General.*;
import static server.util.HttpExchangeDataExtractor.*;
import static server.util.RequestSender.*;
import static server.util.ResponseProvider.badRequestResponse;

public class DownloadRequestHandler {

    private static final Logger logger = Logger.getLogger(DownloadRequestHandler.class.getName());

    public static void startDownloadRequest(HttpExchange exchange) throws IOException {
        String serverIp = getMyIp(exchange);
        Map<String, String> queryParams = queryToMap(exchange);
        String uuid = UUID.randomUUID().toString();
        String url = queryParams.get("url");
        if (url == null) {
            String message = "Url has to be given as a parameter";
            badRequestResponse(exchange, message);
            throw new BadRequestException(message);
        }

        RequestsInfo myRequest = new RequestsInfo(uuid, url, LocalDate.now());
        MY_REQUESTS.add(myRequest);
        RoutingInfo routingInfo = new RoutingInfo(uuid, serverIp, null);
        ROUTINGS_MADE.add(routingInfo);

        logger.log(Level.INFO, String.format("Created a request with id %s and asking for url %s.", uuid, url));
        forwardMessage(uuid, url, serverIp, "this is initial request, no need to check sender-ip");
    }

    public static void handleDownloadRequest(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = queryToMap(exchange);
        String id = queryParams.get("id");
        String url = queryParams.get("url");
        String serverIp = getMyIp(exchange);

        validateDownloadRequest(exchange, id, url);

        RoutingInfo routingInfo = new RoutingInfo(id, getClientUrl(exchange), null);
        ROUTINGS_MADE.add(routingInfo);
        url = decode(url);

        if (getRandomDouble() > LAZYNESS) {
            logger.log(Level.INFO, String.format("Downloading file for request id %s", id));
            downloadFileAndSendItBack(exchange, id, url);
        } else {
            logger.log(Level.INFO, String.format("Forwarding request with id %s", id));
            forwardMessage(id, url, serverIp, getClientUrl(exchange));
        }
    }

    private static void validateDownloadRequest(HttpExchange exchange, String id, String url) throws IOException {
        if (id == null || url == null) {
            String message = "Error: Query parameters not correct!";
            badRequestResponse(exchange, message);
            throw new BadRequestException(message);
        }

        // Check if this request has been already forwarded
        if (getRoutingFromId(id) != null || getRequestFromId(id) != null) {
            String message = "Error: This node has already forwarded this request!";
            badRequestResponse(exchange, message);
            throw new BadRequestException(message);
        }
    }

    private static void downloadFileAndSendItBack(HttpExchange exchange, String id, String url) throws IOException {
        // Download the file
        HttpResponse<String> response = sendGet(url, getMyIp(exchange));
        if (response == null) {
            logger.log(Level.WARNING, "Unable to query given URL!");
            String responseJson = createResponseJson(response, 404);
            returnInfoAboutRequest(exchange, id, responseJson);
            return;
        }

        String responseJson = createResponseJson(response, response.statusCode());
        returnInfoAboutRequest(exchange, id, responseJson);
    }

    private static void returnInfoAboutRequest(HttpExchange exchange, String id, String responseJson) throws IOException {
        RoutingInfo existingRoutingInfo = getRoutingFromId(id);
        if (existingRoutingInfo == null) {
            logger.log(Level.WARNING, "No info where to send the response!");  // Should never get here
            badRequestResponse(exchange, "Error: No info where to send the response!");
            return;
        }

        String uri = "http://" + existingRoutingInfo.getDownloadIp() + "/file?id=" + id;
        logger.info(uri);
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
                   "\"status\": " + Integer.toString(responseCode) + "," +
                   "\"mime-type\": " + contentType + "," +
                   "\"content\": " + encodedBody + "," +
                   " }";
        }
    }

    private static void forwardMessage(String id, String url, String currentServerIp, String requestSenderIp) {
        List<String> offlineServers = new ArrayList<>();
        for (ServersInfo server : Server.getAvailableServers()) {
            // Don't send to myself, would result in deadlock
            if (server.isAlive() && (server.getIp() + ":" + server.getPort()).equals(currentServerIp)) {
                continue;
            }
            // Don't send to server from which the request came from.
            if (requestSenderIp.equals(server.getIpWithPort())) {
                continue;
            }
            String uri = "http://" + server.getIpWithPort()
                    + "/download?id=" + id
                    + "&url=" + encode(url);
            try {
                sendAsyncGet(uri, currentServerIp);
            } catch (Exception e) {
                String serverIp = uri.split("//")[1].split("/")[0];
                logger.log(Level.WARNING, "Server " + serverIp + " is unavailable.");
                offlineServers.add(serverIp);
            }
        }
        removeServersByIp(offlineServers);
    }

    private static String encode(String query) {
        try {
            return URLEncoder.encode(query, DEFAULT_ENCODING);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}
