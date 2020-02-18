package server.util;

import server.Server;
import server.dto.ServersInfo;

import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            updateServers(response);
        } catch (IOException | InterruptedException e) {
            try {
                readFromFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    private void updateServers(String response) throws IOException {
        List<ServersInfo> ret = new LinkedList<>();
        String[] availableServers = response.split("\n");
        for (String server: availableServers) {
            String[] serverWithPort = server.split(":");
            ret.add(new ServersInfo(serverWithPort[0], serverWithPort[1], true));
        }
        String serversAsString = ret.stream().map(ServersInfo::getIpWithPort).collect(Collectors.joining(", "));
        logger.log(Level.INFO, String.format("Available servers: %s", serversAsString));
        Server.setAvailableServers(ret);
        writeToFile(response);
    }

    private void writeToFile(String input) throws IOException {
        File serverFile = new File("resources/servers.txt");
        Path path = Paths.get(serverFile.getAbsolutePath());
        serverFile.createNewFile();
        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            writer.write(input);
        }
    }

    private void readFromFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("resources/servers.txt"), StandardCharsets.UTF_8);
        String response = lines.stream().collect(Collectors.joining("\n"));
        updateServers(response);
    }
}