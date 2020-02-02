package server.controller;

import com.sun.net.httpserver.HttpExchange;
import server.dto.RoutingInfo;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static server.util.General.getRoutingFromId;
import static server.util.HttpExchangeDataExtractor.*;
import static server.util.RequestSender.sendPost;
import static server.util.ResponseProvider.badRequestResponse;


public class FileResponseHandler {

    private static final Logger logger = Logger.getLogger(FileResponseHandler.class.getName());

    public static void handleFileResponse(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = queryToMap(exchange);
        String id = queryParams.get("id");
        String content = getRequestBody(exchange);
        if (id == null) {
            badRequestResponse(exchange, "Error: Query parameters not correct!");
            return;
        }

        RoutingInfo existingRoutingInfo = getRoutingFromId(id);
        if (existingRoutingInfo == null) {
            logger.log(Level.WARNING, "No info where to send the response!");  // Should never get here
            badRequestResponse(exchange, "Error: No info where to send the response!");
            return;
        }
        existingRoutingInfo.setFileIp(getClientUrl(exchange));

        // ToDo: What if my ip is the download ip?!
        String uri = existingRoutingInfo.getDownloadIp() + "/file?id=" + id;
        sendPost(uri, content);
    }

}
