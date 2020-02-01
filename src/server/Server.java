package server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import server.dto.RequestsInfo;
import server.dto.RoutingInfo;
import server.dto.ServersInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import static server.controller.DownloadRequestHandler.handleDownloadRequest;
import static server.controller.FileResponseHandler.handleFileResponse;
import static server.util.ResponseProvider.badRequestResponse;

public class Server {

    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static List<ServersInfo> AVAILABLE_SERVERS = new LinkedList<>();  // TODO: kataloogiserver
    public static List<RoutingInfo> ROUTINGS_MADE = new LinkedList<>();
    public static List<RequestsInfo> REQUESTS = new LinkedList<>();
    public static final double LAZYNESS = 0.5;
    private static final int DEFAULT_PORT = 1215;

    public static void main(String[] args) {
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1216", true));
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1217", true));
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1218", true));

        Logger logger = Logger.getLogger(Server.class.getName());
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(getPort(args)), 0);
            HttpContext context = server.createContext("/");
            context.setHandler(Server::handleRequest);
            server.start();
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage());
        }
    }

    private static int getPort(String[] args) {
        if (args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        return DEFAULT_PORT;
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/download")) {
            handleDownloadRequest(exchange);
        } else if (exchange.getRequestMethod().equals("POST") && exchange.getRequestURI().getPath().equals("/file")) {
            handleFileResponse(exchange);
        }
        badRequestResponse(exchange, "Error: Not a correct endpoint or request method!");
    }

}
