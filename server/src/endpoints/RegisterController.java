package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import db.DBOperations;
import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    static final String REGISTER_ENDPOINT = "/user";

    public static void init(HttpServer server) {
        server.createContext(REGISTER_ENDPOINT, (exchange -> {
            logger.info("Received request at {}", exchange.getRequestURI());

            Gson gson = new Gson();
            InputStream input = exchange.getRequestBody();
            String body = new String(input.readAllBytes());
            User user = gson.fromJson(body, User.class);
            if (DBOperations.postUser(user.Nickname, user.Password)) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            }
            else {
                exchange.sendResponseHeaders(422, 0);
                exchange.close();
            }

        }));

    }
}
