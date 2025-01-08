package endpoints;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;

public class RegisterController {
    static final String REGISTER_ENDPOINT = "/user";

    public static HttpServer init(HttpServer server) {
        server.createContext(REGISTER_ENDPOINT, (exchange -> {
            String respText = "пасаси";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        }));

        return server;
    }
}
