package server.util;

import com.sun.net.httpserver.HttpExchange;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseProvider {

    private ResponseProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static void successResponse(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(200, message.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

    public static void badRequestResponse(HttpExchange exchange, String message) throws IOException {
        Logger logger = Logger.getLogger(Server.class.getName());
        logger.log(Level.INFO, message);
        exchange.sendResponseHeaders(400, message.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

}
