package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import dtos.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class RoomsAddController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String LOGIN_ENDPOINT = "/room";

    public static void init(HttpServer server) {
        server.createContext(LOGIN_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    if ("PUT".equals(exchange.getRequestMethod())){
                        InputStream input = exchange.getRequestBody();
                        String body = new String(input.readAllBytes());
                        Room room = gson.fromJson(body, Room.class);
                        if(!CustomState.rooms.isEmpty()) room.id = CustomState.rooms.getLast().id+1;
                        else room.id = 1;
                        if (CustomState.rooms.add(room)) exchange.sendResponseHeaders(200,0);
                        else exchange.sendResponseHeaders(500,0);
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