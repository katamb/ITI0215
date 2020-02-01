package server.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static server.Server.DEFAULT_ENCODING;

public class HttpExchangeDataExtractor {

    public static Map<String, String> queryToMap(HttpExchange exchange) {
        // https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange
        String rawQuery = exchange.getRequestURI().getRawQuery();
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || !rawQuery.contains("&")) {
            return result;
        }

        for (String param : rawQuery.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(decode(entry[0]), decode(entry[1]));
            } else {
                result.put(decode(entry[0]), "");
            }
        }
        return result;
    }

    private static String decode(String query) {
        try {
            return URLDecoder.decode(query, DEFAULT_ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getRequestBody(HttpExchange exchange) {
        try (
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), DEFAULT_ENCODING);
                BufferedReader br = new BufferedReader(isr)
        ) {
            return br.lines().collect(Collectors.joining(" "));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static InetAddress getClientUrl(HttpExchange exchange) {
        return exchange.getRemoteAddress() != null
                ? exchange.getRemoteAddress().getAddress()
                : null;
    }
}
