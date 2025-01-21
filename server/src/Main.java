import com.sun.net.httpserver.HttpServer;
import endpoints.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int serverPort = 8000;

    public static void main(String[] args) {
        try {
            logger.info("Starting server");
            WebServer.init();
            UDPServer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
