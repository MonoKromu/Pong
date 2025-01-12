package ru.mono.pong.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ru.mono.pong.State;
import ru.mono.pong.transport.UdpClient;
import ru.mono.pong.transport.dtos.Action;
import ru.mono.pong.transport.dtos.GameState;

public class GameController {
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
        start();
    }

    public void start() {
        new Thread(() -> {
            try {
                Thread.sleep(20);
                switch (keyPressed) {
                    case 'w', 's':
                        udp.sendAction(new Action(State.gameId, State.playerId, keyPressed));
                        break;
                    default:
                        //udp.sendAction(new Action(State.gameId, State.playerId, keyPressed));
                        break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void update() {
        ball.setCenterX(GameState.ballX);
        ball.setCenterY(GameState.ballY);

        plank1.setY(GameState.plank1);
        plank2.setY(GameState.plank2);

        plank1Points.setText(String.valueOf(GameState.plank1Points));
        plank2Points.setText(String.valueOf(GameState.plank2Points));
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
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