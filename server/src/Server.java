import com.sun.net.httpserver.HttpServer;
import endpoints.AuthController;

import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) {
        try {
            int serverPort = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            server.createContext("/", (exchange -> {
                String respText = "пасаси";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            }));
            AuthController.init(server);
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}