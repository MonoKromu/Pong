package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RatingController {
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    static final String RATING_ENDPOINT = "/rating";

    public static boolean init(HttpServer server) {
        server.createContext(RATING_ENDPOINT, (exchange -> {
            if (exchange.getRequestMethod().equals("GET")) {
                logger.info("Received request at {} from {}", exchange.getRequestURI(), exchange.getRemoteAddress());
                Gson gson = new Gson();
                ArrayList<User> users = DBOperations.getRating();
                String usersJson = gson.toJson(users);
                exchange.sendResponseHeaders(200,usersJson.length());
                OutputStream output = exchange.getResponseBody();
                output.write(usersJson.getBytes());
                output.flush();
            }
            exchange.close();
        }));

        return true;
    }
}
