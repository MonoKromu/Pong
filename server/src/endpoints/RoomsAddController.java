package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import dtos.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class RoomsAddController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String ROOMS_ADD_ENDPOINT = "/room";

    public static void init(HttpServer server) {
        server.createContext(ROOMS_ADD_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes());
                    Room room = gson.fromJson(body, Room.class);
                    if ("POST".equals(exchange.getRequestMethod())){
                        room.id = CustomState.lastID+1;
                        CustomState.lastID = room.id;
                        room.hostIP = exchange.getRemoteAddress().getAddress();
                        CustomState.rooms.put(room.id, room);
                        String roomJson = gson.toJson(room);
                        exchange.sendResponseHeaders(200,roomJson.length());
                        OutputStream output = exchange.getResponseBody();
                        output.write(roomJson.getBytes());
                        output.flush();
                        exchange.close();
                    }
                    else if("PUT".equals(exchange.getRequestMethod())){
                        Room targetRoom = CustomState.rooms.get(room.id);
                        targetRoom.guest = room.guest;
                        targetRoom.guestIP = exchange.getRemoteAddress().getAddress();
                        exchange.sendResponseHeaders(200,0);
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