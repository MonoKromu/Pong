package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.utils.SceneManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConnectionController {
    @FXML
    Button connect_btn;
    @FXML
    TextField serverAddress;
    @FXML
    Label err_label;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionController.class);

    public void initialize() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/address"));
            serverAddress.setText(reader.readLine());
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonConnect() {
        err_label.setVisible(false);
        State.serverAddress = serverAddress.getText();
        new Thread(() -> {
            if (HttpClient.pingServer()) {
                Platform.runLater(this::toAuth);
                try {
                    logger.info("Connection to server established");
                    FileWriter writer = new FileWriter("src/address");
                    writer.write(serverAddress.getText());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            } else err_label.setVisible(true);
        }).start();
    }

    public void toAuth() {
        try {
            Stage stage = (Stage) connect_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "auth.fxml", "Pong Masters");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}