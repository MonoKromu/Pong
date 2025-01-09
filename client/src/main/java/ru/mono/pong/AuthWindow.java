package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.util.Objects;

public class AuthWindow {
    @FXML
    Button test_btn, reg_btn;
    @FXML
    Label salute;
    @FXML
    TextField login, password;
    @FXML
    TextArea clientOutput;


    public void onButtonEnter() {
        new Thread(() -> {
            String response = apiClient.postAuth(login.getText(), password.getText());
            Platform.runLater(() -> {
                clientOutput.setText(response);
                if (Objects.equals(response, "200")) {
                    Stage stage = (Stage) reg_btn.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("menu.fxml"));
                    Scene scene;
                    try {
                        scene = new Scene(fxmlLoader.load(), 1024, 768);
                    } catch (
                            IOException e) {
                        throw new RuntimeException(e);
                    }
                    stage.setTitle("Menu");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                }
            });
        }).start();
    }

    public void onButtonReg() {
        Stage stage = (Stage) reg_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("reg.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1024, 768);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
