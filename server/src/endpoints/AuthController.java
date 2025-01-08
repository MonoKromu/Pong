package endpoints;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import dtos.User;

import java.io.InputStream;
import java.io.OutputStream;

public class AuthController {
    static final String LOGIN_ENDPOINT = "/auth";

    public static HttpServer init(HttpServer server) {
        server.createContext(LOGIN_ENDPOINT, (exchange -> {
            Gson gson = new Gson();
            InputStream input = exchange.getRequestBody();
            String body = new String(input.readAllBytes());
            User user = gson.fromJson(body, User.class);
            System.out.println(user.Nickname + " " + user.Password);
            exchange.sendResponseHeaders(200,0);
            //OutputStream output = exchange.getResponseBody();
            //output.write(respText.getBytes());
            //output.flush();
            exchange.close();
        }));

        return server;
    }
}
