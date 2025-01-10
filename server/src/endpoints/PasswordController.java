package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class PasswordController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String LOGIN_ENDPOINT = "/password";

    public static void init(HttpServer server) {
        server.createContext(LOGIN_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes());
                    User user = gson.fromJson(body, User.class);
                    if (DBOperations.putPassword(user.login, user.password, user.newPassword)) {
                        exchange.sendResponseHeaders(200, 0);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(401, 0);
                        exchange.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }));

    }
}
