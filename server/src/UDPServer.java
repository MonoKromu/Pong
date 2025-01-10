import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private static final int serverPort = 9876;

    public static void init() {
        new Thread(() -> {
            try (DatagramSocket serverSocket = new DatagramSocket(serverPort)) {
                logger.info("Server started on port {}", serverPort);

                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    // ожидание запроса
                    serverSocket.receive(receivePacket);

                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Получено сообщение: " + receivedMessage);

                    // ответ клиенту
                    String response = "Сообщение получено: " + receivedMessage;
                    byte[] sendData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    serverSocket.send(sendPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
