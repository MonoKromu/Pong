package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    static final String REGISTER_ENDPOINT = "/user";

    public static void init(HttpServer server) {
        server.createContext(REGISTER_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());

                    Gson gson = new Gson();
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes());
                    logger.info(body);
                    User user = gson.fromJson(body, User.class);
                    logger.info(user.toString());
                    if (DBOperations.postUser(user.login, user.password)) {
                        exchange.sendResponseHeaders(200, 0);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(422, 0);
                        exchange.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }));

    }
}
