package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
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
import ru.mono.pong.utils.SceneManager;

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
    @FXML
    Label winner_label, loser_label;

    ArrayList<KeyCode> queue = new ArrayList<>();
    ArrayList<KeyCode> cheat = new ArrayList<>();


    char keyPressed;
    UdpClient udp;

    public void initialize() {
        State.currentGameState.isGameOver = false;
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
        logger.info("You are " + State.currentPlayerId + " player");
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
                        case 'w', 's', 'e':
                            udp.sendAction(new Action(State.currentRoomId, State.currentPlayerId, keyPressed));
                            break;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (State.currentGameState.winner == State.currentPlayerId) {
                    winner_label.setVisible(true);
                } else loser_label.setVisible(true);
                System.out.println("Game is over - Finally");
                State.currentGameState.isGameOver = true;
                udp.close();
                Platform.runLater(this::backToMenu);
            }
        }).start();
    }

    public void backToMenu() {
        try {
            Stage stage = (Stage) pane.getScene().getWindow();
            SceneManager.loadScene(stage, "rooms.fxml", "Rooms");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void update() {
        ball.setLayoutX(State.currentGameState.ballX);
        ball.setLayoutY(State.currentGameState.ballY);

        plank1.setLayoutY(State.currentGameState.plank1);
        plank2.setLayoutY(State.currentGameState.plank2);

        plank1Points.setText(String.valueOf(State.currentGameState.plank1Points));
        plank2Points.setText(String.valueOf(State.currentGameState.plank2Points));
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        logger.info("Key pressed: {}", code);
        switch (code) {
            case W -> keyPressed = 'w';
            case S -> keyPressed = 's';
            case ESCAPE -> {
                keyPressed = 'e';
                System.out.println("Game is over - ESCAPE");
                //State.currentGameState.isGameOver = true;
            }
        }
        queue.addLast(code);
        if (queue.size() > 9) queue.removeFirst();
        if (cheat.equals(queue)) {
            ball.setFill(Paint.valueOf("BLACK"));
            plank1.setFill(Paint.valueOf("BLUE"));
            plank2.setFill(Paint.valueOf("RED"));
            plank1Points.setFill(Paint.valueOf("BLACK"));
            plank2Points.setFill(Paint.valueOf("BLACK"));
            field.setVisible(false);
            zet.setVisible(true);
        }
        System.out.println(queue);
        System.out.println(cheat);
    }

    @FXML
    private void onKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case W, S, ESCAPE -> keyPressed = 'a';
        }
    }


}