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

public class RegController {
    @FXML
    Button accept_btn, auth_btn;
    @FXML
    Label status_code;
    @FXML
    TextField login, password, sec_pass;

    public void onButtonAccept() {
        accept_btn.setDisable(true);
        auth_btn.setDisable(true);
        login.setDisable(true);
        password.setDisable(true);
        sec_pass.setDisable(true);
        if (Objects.equals(password.getText(), sec_pass.getText()) && !login.getText().isEmpty() &&
                !password.getText().isEmpty() && !sec_pass.getText().isEmpty()) {
            new Thread(() -> {
                String response = apiClient.postReg(login.getText(), password.getText());
                System.out.println(response);
                Platform.runLater(() -> {
                    if (Objects.equals(response, "200")) {
                        status_code.setText("Регистрация прошла успешно!");
                        status_code.setTextFill(Paint.valueOf("GREEN"));
                        status_code.setVisible(true);
                    } else if (Objects.equals(response, "422")) {
                        status_code.setText("Пользователь с таким именем уже существует!");
                        status_code.setTextFill(Paint.valueOf("RED"));
                        status_code.setVisible(true);
                    } else {
                        status_code.setText("Status code: " + response);
                        status_code.setTextFill(Paint.valueOf("RED"));
                        status_code.setVisible(true);
                    }
                    accept_btn.setDisable(false);
                    auth_btn.setDisable(false);
                    login.setDisable(false);
                    password.setDisable(false);
                    sec_pass.setDisable(false);
                });
            }).start();
        } else if (!Objects.equals(password.getText(), sec_pass.getText())) {
            Platform.runLater(() -> {
                status_code.setText("Пароли не совпадают!");
                status_code.setTextFill(Paint.valueOf("RED"));
                status_code.setVisible(true);
            });
        } else if (login.getText().isEmpty() || password.getText().isEmpty() || sec_pass.getText().isEmpty()) {
            Platform.runLater(() -> {
                status_code.setText("Все поля должны быть заполнены!");
                status_code.setTextFill(Paint.valueOf("RED"));
                status_code.setVisible(true);
            });
        } else {
            Platform.runLater(() -> {
                status_code.setText("Something wrong...");
                status_code.setTextFill(Paint.valueOf("RED"));
                status_code.setVisible(true);
            });
        }
    }

    public void onButtonToAuth() {
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("auth.fxml"));
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
    }

}
