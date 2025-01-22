import com.google.gson.Gson;
import db.DBOperations;
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
    private static final int sendPort = 8003;
    private static HashMap<Integer, Worker> workers = new HashMap<>();
    private static HashMap<Integer, GameState> states = new HashMap<>();
    public static DatagramSocket receiveSocket;
    public static DatagramSocket sendSocket;

    public static void init() {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                receiveSocket = new DatagramSocket(receivePort);
                sendSocket = new DatagramSocket(sendPort + 5);
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
                            DatagramPacket packet1 = new DatagramPacket(out, out.length, room.hostIP, sendPort+2);
                            DatagramPacket packet2 = new DatagramPacket(out, out.length, room.guestIP, sendPort+2);
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
                    else if(action.key == 'e'){
                        logger.info("Received ESCAPE");
                        Room room = CustomState.rooms.get(action.id);
                        if(room!=null){
                            if(room.gameStarted){
                                logger.info("Game was started, player {} left", action.player);
                                Worker worker = workers.get(action.id);
                                worker.gameEnded = true;
                                worker.state.isGameOver = true;
                                if(action.player == 1){
                                    worker.state.winner = 2;
                                    DBOperations.putUserPoints(room.guest.login);
                                }
                                else if(action.player == 2){
                                    worker.state.winner = 1;
                                    DBOperations.putUserPoints(room.host.login);
                                }
                                worker.send.run();
                            }
                            else{
                                logger.info("Room was empty");
                                GameState state = new GameState();
                                state.isGameOver = true;
                                state.plank2 = 350;
                                state.plank1 = 350;
                                state.ballY = 400;
                                state. ballX = 512;
                                byte[] out = gson.toJson(state).getBytes();
                                sendSocket.send(new DatagramPacket(out, out.length, room.hostIP, sendPort+2));
                            }
                            CustomState.rooms.remove(action.id);
                        }
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

