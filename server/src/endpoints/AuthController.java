package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    static final String AUTH_ENDPOINT = "/auth";

    public static void init(HttpServer server) {
        server.createContext(AUTH_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Received request {} {} from {}", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress());
                    Gson gson = new Gson();
                    InputStream input = exchange.getRequestBody();
                    String body = new String(input.readAllBytes());
                    User user = gson.fromJson(body, User.class);
                    User userDB = DBOperations.getUser(user.login, user.password);
                    if (userDB != null) {
                        String userJson = gson.toJson(userDB);
                        exchange.sendResponseHeaders(200, userJson.length());
                        OutputStream output = exchange.getResponseBody();
                        output.write(userJson.getBytes());
                        output.flush();
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
