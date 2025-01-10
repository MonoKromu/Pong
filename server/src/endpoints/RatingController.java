package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.ArrayList;

public class RatingController {
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    static final String RATING_ENDPOINT = "/rating";

    public static boolean init(HttpServer server) {
        server.createContext(RATING_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    if ("GET".equals(exchange.getRequestMethod())) {
                        logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                        Gson gson = new Gson();
                        ArrayList<User> users = DBOperations.getRating();
                        String usersJson = gson.toJson(users);
                        exchange.sendResponseHeaders(200, usersJson.length());
                        OutputStream output = exchange.getResponseBody();
                        output.write(usersJson.getBytes());
                        output.flush();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.getResponseBody().close();
                        logger.info("Invalid request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    }
                    exchange.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }));

        return true;
    }
}
