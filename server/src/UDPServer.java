import com.google.gson.Gson;
import domain.Worker;
import dtos.Action;
import dtos.GameState;
import dtos.Room;
import endpoints.CustomState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private static final int receivePort = 8001;
    private static final int sendPort = 8002;
    private static HashMap<Integer, Worker> workers = new HashMap<>();
    private static HashMap<Integer, GameState> states = new HashMap<>();
    public static DatagramSocket receiveSocket;
    public static DatagramSocket sendSocket;

    public static void init() {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                receiveSocket = new DatagramSocket(receivePort);
                sendSocket = new DatagramSocket(sendPort);
                logger.info("Server started on port {}", receivePort);

                byte[] receiveData = new byte[512];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    // ожидание запроса
                    receiveSocket.receive(receivePacket);

                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Action action = gson.fromJson(receivedMessage, Action.class);
                    if(action.key=='n') {
                        logger.info("Player joined room");
                        states.put(action.id, new GameState());
                        workers.put(action.id, new Worker(states.get(action.id), action.id, () -> {
                            GameState state = states.get(action.id);
                            byte[] out = gson.toJson(state).getBytes();
                            Room room = CustomState.rooms.get(action.id);
                            DatagramPacket packet1 = new DatagramPacket(out, out.length, room.hostIP, sendPort);
                            DatagramPacket packet2 = new DatagramPacket(out, out.length, room.guestIP, sendPort);
                            try {
                                //logger.info("Sending gamestate to players {} {}", room.hostIP, room.guestIP);
                                sendSocket.send(packet1);
                                sendSocket.send(packet2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }));
                        CustomState.rooms.get(action.id).gameStarted = true;
                        workers.get(action.id).start();
                    }
                    else if(action.key == 'w' || action.key == 's'){
                        logger.info("Received action {}", new String(receivePacket.getData()));
                        if(workers.get(action.id)!=null) workers.get(action.id).update(action);
                    }
                    //String response = "Сообщение получено: " + receivedMessage;
                    //byte[] sendData = response.getBytes();
                    //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    //receiveSocket.send(sendPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

