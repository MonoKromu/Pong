package ru.mono.pong.transport;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.State;
import ru.mono.pong.transport.dtos.Action;
import ru.mono.pong.transport.dtos.GameState;

import java.io.IOException;
import java.net.*;

public class UdpClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(UdpClient.class);
    // static final String serverAddress = "95.181.27.100"; // Адрес сервера
    static final String serverAddress = "46.181.90.183";
    static final int PORT = 8000;              // Порт сервера
    private static DatagramSocket receiveSocket;
    private static DatagramSocket sendSocket;

    private final Runnable update;

    public UdpClient(Runnable update, boolean start) {
        this.update = update;
        try {
            receiveSocket = new DatagramSocket(PORT + 5);
            sendSocket = new DatagramSocket(PORT + 2);
            logger.info("UDP client started on port {} \t Sending on port {}", receiveSocket.getLocalPort(), sendSocket.getLocalPort());
            if (start) this.start();
            this.listen();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() {
        new Thread(() -> {
            if (!State.currentGameState.isGameOver) {
                try {
                    Gson gson = new Gson();
                    byte[] receiveData = new byte[256];
                    int i = 0;
                    while (!State.currentGameState.isGameOver) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        // ожидание запроса
                        receiveSocket.receive(receivePacket);
                        if (i % 100 == 0) {
                            logger.info(new String(receivePacket.getData()));
                        }
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        GameState state = gson.fromJson(receivedMessage, GameState.class);
                        i++;
                        State.currentGameState = state;
                        update.run();
                    }
                    receiveSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void start() {
        new Thread(() -> {
            Gson gson = new Gson();
            Action action = new Action(State.currentRoomId, State.currentPlayerId, 'n');
            byte[] actionByte = gson.toJson(action).getBytes();
            try {
                DatagramPacket startPacket = new DatagramPacket(actionByte, actionByte.length, InetAddress.getByName(serverAddress), PORT + 1);
                sendSocket.send(startPacket);
                logger.info("Start packet sent");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void sendAction(Action action) {
        try {
            Gson gson = new Gson();
            byte[] actionByte = gson.toJson(action).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(actionByte, actionByte.length, InetAddress.getByName(serverAddress), PORT + 1);
            sendSocket.send(sendPacket);
            logger.info("Send port {}/", sendSocket.getLocalPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        receiveSocket.close();
        sendSocket.close();
    }
}
