package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import dtos.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.util.ArrayList;

public class RoomsGetController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String ROOMS_GET_ENDPOINT = "/rooms";

    public static void init(HttpServer server) {
        server.createContext(ROOMS_GET_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    if("GET".equals(exchange.getRequestMethod())){
                        ArrayList<Room> visibleRooms = new ArrayList<>();
                        for(Room r : CustomState.rooms.values()){
                            if(!r.gameStarted) visibleRooms.add(r);
                        }
                        String rooms = gson.toJson(visibleRooms);

                        exchange.sendResponseHeaders(200, rooms.length());
                        OutputStream output = exchange.getResponseBody();
                        output.write(rooms.getBytes());
                        output.flush();
                        exchange.close();
                    }
                    else{
                        exchange.sendResponseHeaders(405, 0);
                        exchange.getResponseBody().close();
                        logger.info("Invalid request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                        exchange.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }));
    }
}