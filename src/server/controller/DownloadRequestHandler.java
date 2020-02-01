package server.controller;

import com.sun.net.httpserver.HttpExchange;
import server.dto.RequestsInfo;
import server.dto.RoutingInfo;
import server.dto.ServersInfo;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.Server.AVAILABLE_SERVERS;
import static server.Server.DEFAULT_ENCODING;
import static server.Server.LAZYNESS;
import static server.Server.REQUESTS;
import static server.Server.ROUTINGS_MADE;
import static server.util.General.getRandomDouble;
import static server.util.HttpExchangeDataExtractor.getClientUrl;
import static server.util.HttpExchangeDataExtractor.queryToMap;
import static server.util.RequestSender.sendGet;
import static server.util.ResponseProvider.badRequestResponse;

public class DownloadRequestHandler {

    private static final Logger logger = Logger.getLogger(DownloadRequestHandler.class.getName());

    public static void handleDownloadRequest(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = queryToMap(exchange);
        String id = queryParams.get("id");
        String url = queryParams.get("url");
        if (id == null || url == null) {
            badRequestResponse(exchange, "Error: Query parameters not correct!");
            return;
        }

        url = URLDecoder.decode(url, DEFAULT_ENCODING);
        RequestsInfo request = new RequestsInfo(id, url, LocalDate.now());
        REQUESTS.add(request);
        RoutingInfo routingInfo = new RoutingInfo(id, getClientUrl(exchange).toString(), null);

        if (true){//getRandomDouble() > LAZYNESS) {
            logger.log(Level.INFO, "Downloading file for request id " + id);
            downloadFileAndSendItBack(request, routingInfo);//todo
        } else {
            logger.log(Level.INFO, "Forwarding request with id " + id);
            forwardMessage(request, routingInfo);
        }
    }

    private static void downloadFileAndSendItBack(RequestsInfo request, RoutingInfo routingInfo) {
        HttpResponse<String> response = sendGet(request.getUrl());
        String encodedBody = Base64.getEncoder().encodeToString(response.body().getBytes());
        String contentType = response.headers().map().get("content-type").get(0);
        int responseCode = response.statusCode();
        System.out.println("");

    }

    private static void forwardMessage(RequestsInfo request, RoutingInfo routingInfo) {
        RoutingInfo existingRoutingInfo = ROUTINGS_MADE.stream()
                .filter(routing -> routing.getId().equals(request.getId()))
                .findAny()
                .orElse(null);
        if (existingRoutingInfo != null) {
            logger.log(Level.INFO, "This node has already forwarded this request!");
            return;
        }

        ROUTINGS_MADE.add(routingInfo);
        for (ServersInfo server : AVAILABLE_SERVERS) {
            String uri = "http://" + server.getIp() + ":" + server.getPort()
                    + "/download?id=" + request.getId()
                    + "&url=" + request.getUrl();
            sendGet(uri);
        }
    }

}
