package ru.mono.pong.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.Main;
import ru.mono.pong.State;
import ru.mono.pong.transport.UdpClient;
import ru.mono.pong.transport.dtos.Action;
import ru.mono.pong.transport.dtos.GameState;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    @FXML
    Circle ball;
    @FXML
    Rectangle field, plank1, plank2;
    @FXML
    Pane pane;
    @FXML
    Text plank1Points, plank2Points, zet;

    ArrayList<KeyCode> queue = new ArrayList<>();
    ArrayList<KeyCode> cheat = new ArrayList<>();


    char keyPressed;
    UdpClient udp;

    public void initialize() {
        cheat.addLast(KeyCode.LEFT);
        cheat.addLast(KeyCode.LEFT);
        cheat.addLast(KeyCode.UP);
        cheat.addLast(KeyCode.UP);
        cheat.addLast(KeyCode.RIGHT);
        cheat.addLast(KeyCode.RIGHT);
        cheat.addLast(KeyCode.DOWN);
        cheat.addLast(KeyCode.DOWN);
        cheat.addLast(KeyCode.Z);

        udp = new UdpClient(this::update, State.currentPlayerId == 2);
        logger.info(String.valueOf("You are " + State.currentPlayerId + " player"));
        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::onKeyPressed);
                newScene.setOnKeyReleased(this::onKeyReleased);
            }
        });
        sendActions();
    }

    public void sendActions() {
        new Thread(() -> {
            try {
                while (!State.currentGameState.isGameOver) {
                    Thread.sleep(20);
                    switch (keyPressed) {
                        case 'w', 's':
                            udp.sendAction(new Action(State.currentRoomId, State.currentPlayerId, keyPressed));
                            break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                udp.close();
            }
        }).start();
    }

    private void update() {
        ball.setLayoutX(State.currentGameState.ballX);
        ball.setLayoutY(State.currentGameState.ballY);

        plank1.setY(State.currentGameState.plank1);
        plank2.setY(State.currentGameState.plank2);

        plank1Points.setText(String.valueOf(State.currentGameState.plank1Points));
        plank2Points.setText(String.valueOf(State.currentGameState.plank2Points));
    }

    public void escapeToRooms() {
        udp.close();
        Stage stage = (Stage) pane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("rooms.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1024, 768);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Game rooms");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        logger.info("Key pressed: {}", code);
        switch (code) {
            case W -> keyPressed = 'w';
            case S -> keyPressed = 's';
            case ESCAPE -> escapeToRooms();
        }
        queue.addLast(code);
        if(queue.size() > 9) queue.removeFirst();
        if(cheat.equals(queue)) zet.setVisible(true);
        System.out.println(queue);
        System.out.println(cheat);
    }

    @FXML
    private void onKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case W, S -> keyPressed = 'a';
        }
    }


}