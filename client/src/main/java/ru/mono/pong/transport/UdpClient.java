package ru.mono.pong.transport;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.State;
import ru.mono.pong.transport.dtos.Action;
import ru.mono.pong.transport.dtos.GameState;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;

public class UdpClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(UdpClient.class);
    // static final String serverAddress = "95.181.27.100"; // Адрес сервера
    static String serverAddress = State.serverAddress.split(":")[0];
    static final int PORT = 8000;              // Порт сервера
    private static DatagramSocket receiveSocket;
    private static DatagramSocket sendSocket;

    private final Runnable update;

    public UdpClient(Runnable update, boolean start) {
        this.update = update;
        try {
            receiveSocket = new DatagramSocket(PORT + 5);
            receiveSocket.setReuseAddress(true);
            sendSocket = new DatagramSocket(PORT + 2);
            sendSocket.setReuseAddress(true);
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
                    logger.info("Start listening");
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
                    logger.info(String.valueOf(State.currentGameState.winner));
                    //receiveSocket.close();
                    //sendSocket.close();
                    logger.info("UDP ports is closed - while: {}\t {}", receiveSocket.getLocalPort(), sendSocket.getLocalPort());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                receiveSocket.close();
                sendSocket.close();
                logger.info("UDP ports is closed - else: {}\t {}", receiveSocket.getLocalPort(), sendSocket.getLocalPort());
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
        new Thread(() -> {
            Gson gson = new Gson();
            byte[] actionByte = gson.toJson(action).getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(actionByte, actionByte.length, InetAddress.getByName(serverAddress), PORT + 1);
                sendSocket.send(sendPacket);
                logger.info("Send from port {}", sendSocket.getLocalPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void close() {
        new Thread(() -> {
            try {
                logger.info("UDP ports is closed - close(): {}\t {}", receiveSocket.getLocalPort(), sendSocket.getLocalPort());
                receiveSocket.close();
                sendSocket.close();
                logger.info("UDP ports is closed - close(): {}\t {}", receiveSocket.getLocalPort(), sendSocket.getLocalPort());
            } catch (UncheckedIOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
