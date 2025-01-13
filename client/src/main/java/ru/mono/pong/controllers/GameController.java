package ru.mono.pong.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.State;
import ru.mono.pong.transport.UdpClient;
import ru.mono.pong.transport.dtos.Action;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    @FXML
    Circle ball;
    @FXML
    Rectangle field, plank1, plank2;
    @FXML
    Pane pane;
    @FXML
    Text plank1Points, plank2Points;

    char keyPressed;
    UdpClient udp;

    public void initialize() {
        udp = new UdpClient(this::update);
        logger.info(String.valueOf(State.currentPlayerId));
        if (State.currentPlayerId == 2) udp.start();
        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::onKeyPressed);
                newScene.setOnKeyReleased(this::onKeyReleased);
            }
        });
        start();
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(20);
                    switch (keyPressed) {
                        case 'w', 's':
                            udp.sendAction(new Action(State.currentRoomId, State.currentPlayerId, keyPressed));
                            break;
                        default:
                            //udp.sendAction(new Action(State.gameId, State.playerId, keyPressed));
                            break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void update() {
        ball.setLayoutX(State.currentGameState.ballX);
        ball.setLayoutY(State.currentGameState.ballY);

        plank1.setY(State.currentGameState.plank1);
        plank2.setY(State.currentGameState.plank2);

        plank1Points.setText(String.valueOf(State.currentGameState.plank1Points));
        plank2Points.setText(String.valueOf(State.currentGameState.plank2Points));
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        logger.info("Key pressed: " + code);
        switch (code) {
            case W -> keyPressed = 'w';
            case S -> keyPressed = 's';
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case W, S -> keyPressed = 'a';
        }
    }
}