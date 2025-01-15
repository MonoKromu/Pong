package endpoints;

import com.sun.net.httpserver.HttpServer;

public class Builder {
    public static void build(HttpServer server) {
        AuthController.init(server);
        RegisterController.init(server);
        PasswordController.init(server);
        RatingController.init(server);
        RoomsGetController.init(server);
        RoomsAddController.init(server);
        PingController.init(server);
    }
}
