import com.sun.net.httpserver.HttpServer;
import endpoints.Builder;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int serverPort = 8000;

    public static void init() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            Builder.build(server);
            server.setExecutor(null);
            server.start();
            logger.info("Server started on port {}", serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}