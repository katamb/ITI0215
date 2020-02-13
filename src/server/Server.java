package server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import server.dto.RequestsInfo;
import server.dto.RoutingInfo;
import server.dto.ServersInfo;
import server.util.TaskRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.logging.Level;

import static server.controller.DownloadRequestHandler.handleDownloadRequest;
import static server.controller.DownloadRequestHandler.startDownloadRequest;
import static server.controller.FileResponseHandler.handleFileResponse;
import static server.util.ResponseProvider.badRequestResponse;
import static server.util.ResponseProvider.successResponse;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static final List<ServersInfo> AVAILABLE_SERVERS = new LinkedList<>();
    public static final List<RoutingInfo> ROUTINGS_MADE = new LinkedList<>();
    public static final List<RequestsInfo> MY_REQUESTS = new LinkedList<>();
    public static final double LAZYNESS = 0.5;
    private static int PORT = 1215;

    public static void main(String[] args) {
        // TODO: Need pead saama kuskilt kataloogiserverist?
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1215", true));
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1216", true));
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1217", true));
        AVAILABLE_SERVERS.add(new ServersInfo("127.0.0.1", "1218", true));

        try {
            injectPortValue(args);
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            HttpContext context = server.createContext("/");
            context.setHandler(Server::handleRequest);
            server.start();
            logger.log(Level.INFO, String.format("Server started on port: %d", PORT));
            runScheduledTasks();
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage());
        }
    }

    private static void injectPortValue(String[] args) {
        if (args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/start-download")) {
            logger.log(Level.INFO, "Got start download (GET) request");
            startDownloadRequest(exchange);
            successResponse(exchange, "OK: Invoked server started download request.");
        } else if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/download")) {
            logger.log(Level.INFO, "Got download (GET) request");
            handleDownloadRequest(exchange);
            successResponse(exchange, "OK");
        } else if (exchange.getRequestMethod().equals("POST") && exchange.getRequestURI().getPath().equals("/file")) {
            logger.log(Level.INFO, "Got file (POST) request");
            handleFileResponse(exchange);
            successResponse(exchange, "OK");
        } else {
            badRequestResponse(exchange, "Error: Not a correct endpoint or request method!");
        }
    }

    private static void runScheduledTasks() {
        Timer t = new Timer();
        TaskRunner mTask = new TaskRunner();
        // This task is scheduled to run every 60 seconds
        t.scheduleAtFixedRate(mTask, 0, 60000);
    }
}
