package server.util;

import server.Server;
import server.dto.ServersInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaskRunner extends TimerTask {

    private static final Logger logger = Logger.getLogger(TaskRunner.class.getName());

    String catalogServer = "http://dijkstra.cs.ttu.ee/~emil.fenenko/hajussys/available_servers";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public TaskRunner() {
        //Some stuffs
    }

    @Override
    public void run() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(catalogServer))
                .build();
        List<ServersInfo> ret = new LinkedList<>();
        try {
            String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            String[] availableServers = response.split("\n");
            for (String server: availableServers) {
                String[] serverWithPort = server.split("\t");
                ret.add(new ServersInfo(serverWithPort[0], serverWithPort[1], true));
            }
            String serversAsString = ret.stream().map(ServersInfo::getIpWithPort).collect(Collectors.joining(", "));
            logger.log(Level.INFO, String.format("Available servers: %s", serversAsString));
            Server.setAvailableServers(ret);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}