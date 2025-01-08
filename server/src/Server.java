import com.sun.net.httpserver.HttpServer;
import endpoints.Builder;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int serverPort = 8000;

    public static void main(String[] args) {
        try {
            logger.info("Starting server");
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