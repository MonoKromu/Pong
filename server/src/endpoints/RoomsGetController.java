package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.OutputStream;

public class RoomsGetController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String LOGIN_ENDPOINT = "/rooms";

    public static void init(HttpServer server) {
        server.createContext(LOGIN_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    if("GET".equals(exchange.getRequestMethod())){
                        String rooms = gson.toJson(CustomState.rooms);
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