import com.google.gson.Gson;
import domain.Worker;
import dtos.Action;
import dtos.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private static final int serverPort = 8000;
    private static HashMap<Integer, Worker> workers = new HashMap<>();
    private static HashMap<Integer, GameState> states = new HashMap<>();
    public static DatagramSocket serverSocket;

    public static void init() {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                serverSocket = new DatagramSocket(serverPort);
                logger.info("Server started on port {}", serverPort);

                byte[] receiveData = new byte[512];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    // ожидание запроса
                    serverSocket.receive(receivePacket);

                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Action action = gson.fromJson(receivedMessage, Action.class);
                    if(action.key=='n') {
                        states.put(action.id, new GameState());
                        workers.put(action.id, new Worker(states.get(action.id), () -> {
                            GameState state = states.get(action.id);
                            byte[] out = gson.toJson(state).getBytes();
                            DatagramPacket packet = new DatagramPacket(out, out.length, receivePacket.getAddress(), receivePacket.getPort());
                            try {
                                serverSocket.send(packet);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }));
                    }
                    else if(action.key == 'w' || action.key == 's'){
                        workers.get(action.id).update(action);
                    }
                    //String response = "Сообщение получено: " + receivedMessage;
                    //byte[] sendData = response.getBytes();
                    //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    //serverSocket.send(sendPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

