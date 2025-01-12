package ru.mono.pong.transport;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.State;
import ru.mono.pong.transport.dtos.Action;
import ru.mono.pong.transport.dtos.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    private static final Logger logger = LoggerFactory.getLogger(UdpClient.class);
    // static final String serverAddress = "95.181.27.100"; // Адрес сервера
    static final String serverAddress = "46.181.90.183";
    static final int PORT = 8000;              // Порт сервера
    private static DatagramSocket clientSocket;

    // private Runnable update;

    public UdpClient(Runnable update) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                clientSocket = new DatagramSocket(PORT);
                logger.info("UDP client started on port {}", PORT);

                byte[] receiveData = new byte[512];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    // ожидание запроса
                    clientSocket.receive(receivePacket);
                    logger.info(receivePacket.toString());
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    GameState state = gson.fromJson(receivedMessage, GameState.class);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void start() {
        new Thread(() -> {
            Gson gson = new Gson();
            Action action = new Action(State.gameId, State.playerId, 'n');
            byte[] actionByte = gson.toJson(action).getBytes();
            try {
                DatagramPacket startPacket = new DatagramPacket(actionByte, actionByte.length, InetAddress.getByName(serverAddress), PORT);
                clientSocket.send(startPacket);
                logger.info("Start packet sent");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void sendAction(Action action) {
        try {

        } catch (Exception e) {

        }
    }
}
