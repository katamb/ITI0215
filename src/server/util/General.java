package server.util;

import com.sun.net.httpserver.HttpExchange;
import server.controller.DownloadRequestHandler;
import server.dto.RequestsInfo;
import server.dto.RoutingInfo;

import java.util.Random;
import java.util.logging.Logger;

import static server.Server.*;

public class General {

    private static final Logger logger = Logger.getLogger(DownloadRequestHandler.class.getName());

    private General() {
        throw new IllegalStateException("Utility class");
    }

    public static double getRandomDouble() {
        Random r = new Random();
        return r.nextDouble();
    }

    public static RoutingInfo getRoutingFromId(String id) {
        return ROUTINGS_MADE.stream()
                .filter(routing -> routing.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public static RequestsInfo getRequestFromId(String id) {
        return MY_REQUESTS.stream()
                .filter(request -> request.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public static String getMyIp(HttpExchange exchange) {
        return exchange.getLocalAddress().toString().replace("/", "");
    }

}
