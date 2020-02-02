package server.util;

import server.dto.RoutingInfo;

import java.util.Random;

import static server.Server.ROUTINGS_MADE;

public class General {

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

}
