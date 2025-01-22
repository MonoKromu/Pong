package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;

import java.util.Objects;


public class CreateRoomController {
    @FXML
    Button back_btn, create_btn;
    @FXML
    Label err_lab;
    @FXML
    TextField name_label;

    private RoomsController papa;

    public void setPapa(RoomsController papa) {
        this.papa = papa;
    }

    public void showStatus(String message, String color, Boolean visible) {
        err_lab.setText(message);
        err_lab.setTextFill(Paint.valueOf(color));
        err_lab.setVisible(visible);
    }

    public void onButtonCreate() {
        new Thread(() -> {
            err_lab.setVisible(false);
            if (!Objects.equals(name_label.getText(), "") && !Objects.equals(name_label.getText(), " ")) {
                Platform.runLater(() -> {
                    String response = HttpClient.postRoom(name_label.getText(), State.currentUser.login);
                    if (Objects.equals(response, "200")) {
                        showStatus("Комната создана", "GREEN", true);
                        onButtonBack();
                        goToGame();
                    } else showStatus("Status code: " + response, "BLUE", true);
                });
            } else showStatus("Введите название комнаты", "RED", true);
        }).start();
    }

    private void goToGame() {
        papa.switchToGame();
    }

    public void onButtonBack() {
        Stage stage = (Stage) back_btn.getScene().getWindow();
        stage.close();
    }
}
