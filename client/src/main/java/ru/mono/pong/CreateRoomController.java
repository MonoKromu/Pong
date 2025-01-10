package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.util.Objects;

public class CreateRoomController {
    @FXML
    Button back_btn, start_btn;

    @FXML
    Label err_lab;
    @FXML
    TextField name_label;


    public void onButtonStart() {
        new Thread(() -> {
            boolean response = apiClient.putRoom(name_label.getText(), State.currentUser.login);
            err_lab.setVisible(false);
            if (!Objects.equals(name_label.getText(), null)) {
                Platform.runLater(() -> {
                    if (response) {
                        Stage stage = (Stage) start_btn.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game.fxml"));
                        Scene scene;
                        try {
                            scene = new Scene(fxmlLoader.load(), 1024, 768);
                        } catch (
                                IOException e) {
                            throw new RuntimeException(e);
                        }
                        stage.setTitle("Pong Masters");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } else {
                        err_lab.setText("Something wrong...");
                        err_lab.setVisible(true);
                    }
                });
            }
            err_lab.setText("Введите название комнаты");
            err_lab.setVisible(true);
        }).start();
    }

    public void onButtonBack() {
        Stage stage = (Stage) back_btn.getScene().getWindow();
        stage.close();
    }

}
