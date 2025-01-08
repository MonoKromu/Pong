package endpoints;

import com.sun.net.httpserver.HttpServer;

public class Builder {
    public static void build(HttpServer server) {
        AuthController.init(server);
        RegisterController.init(server);
        RatingController.init(server);
    }
}
