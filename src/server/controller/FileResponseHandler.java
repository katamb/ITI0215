package server.controller;

import com.sun.net.httpserver.HttpExchange;
import server.dto.RoutingInfo;
import server.exception.BadRequestException;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import static server.util.General.getMyIp;
import static server.util.General.getRoutingFromId;
import static server.util.HttpExchangeDataExtractor.*;
import static server.util.RequestSender.sendPost;
import static server.util.ResponseProvider.badRequestResponse;
import static server.util.ResponseProvider.successResponse;


public class FileResponseHandler {

    private static final Logger logger = Logger.getLogger(FileResponseHandler.class.getName());

    public static void handleFileResponse(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = queryToMap(exchange);
        String id = queryParams.get("id");
        String content = getRequestBody(exchange);
        if (id == null) {
            String message = "Error: Query parameters not correct!";
            badRequestResponse(exchange, message);
            throw new BadRequestException(message);
        }

        RoutingInfo existingRoutingInfo = getRoutingFromId(id);
        if (existingRoutingInfo == null) {
            String message = "Error: Query parameters not correct!";
            badRequestResponse(exchange, message);
            throw new BadRequestException(message);
        }
        existingRoutingInfo.setFileIp(getClientUrl(exchange));

        // My ip is download ip
        if (existingRoutingInfo.getDownloadIp().equals(getMyIp(exchange))) {
            logger.info("Got the requested file (with id " + id + ") and content: " + content);
            successResponse(exchange, "OK");
            return;
        }

        String uri = "http://" + existingRoutingInfo.getDownloadIp() + "/file?id=" + id;
        sendPost(uri, content);
    }

}