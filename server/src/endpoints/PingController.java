package endpoints;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingController {
    private static final Logger logger = LoggerFactory.getLogger(PingController.class);
    static final String PING_ENDPOINT = "/";

    public static void init(HttpServer server) {
        server.createContext(PING_ENDPOINT, (exchange -> {
            new Thread(() -> {
                try {
                    logger.info("Server is available from {}", exchange.getRemoteAddress());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }));

    }
}
