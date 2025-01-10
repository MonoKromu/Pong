package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.transport.apiClient;

import java.util.Objects;

public class CreateRoomController {
    @FXML
    Button back_btn, create_btn;
    @FXML
    Label err_lab;
    @FXML
    TextField name_label;


    public void onButtonCreate() {
        new Thread(() -> {
            err_lab.setVisible(false);
            if (!Objects.equals(name_label.getText(), "") && !Objects.equals(name_label.getText(), " ")) {
                Platform.runLater(() -> {
                    String response = apiClient.putRoom(name_label.getText(), State.currentUser.login);
                    if (Objects.equals(response, "200")) {
                        err_lab.setText("Комната создана");
                        err_lab.setTextFill(Paint.valueOf("GREEN"));
                        err_lab.setVisible(true);
                    } else {
                        err_lab.setTextFill(Paint.valueOf("BLUE"));
                        err_lab.setText("Status code: " + response);
                        err_lab.setVisible(true);
                    }
                });
            } else {
                err_lab.setTextFill(Paint.valueOf("RED"));
                err_lab.setText("Введите название комнаты");
                err_lab.setVisible(true);
            }
        }).start();
    }

    public void onButtonBack() {
        Stage stage = (Stage) back_btn.getScene().getWindow();
        stage.close();
    }
}
