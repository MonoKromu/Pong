package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.HashManager;
import ru.mono.pong.utils.SceneManager;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    @FXML
    Button accept_btn, back_btn;
    @FXML
    Label status;
    @FXML
    TextField login;
    @FXML
    PasswordField password, sec_pass;

    private void setInputsDisability(Boolean disability) {
        accept_btn.setDisable(disability);
        back_btn.setDisable(disability);
        login.setDisable(disability);
        password.setDisable(disability);
        sec_pass.setDisable(disability);
    }

    private boolean checkCredentials() {
        return !login.getText().isEmpty() && !password.getText().isEmpty() && Objects.equals(password.getText(), sec_pass.getText());
    }

    private void showStatus(String message, String color) {
        status.setText(message);
        status.setTextFill(Paint.valueOf(color));
        status.setVisible(true);
    }

    public void onButtonAccept() {
        setInputsDisability(true);
        if (checkCredentials()) {
            new Thread(() -> {
                String hashed = HashManager.sha256Hash(password.getText());
                boolean registerSuccessful = HttpClient.postReg(login.getText(), hashed); //password.getText());
                Platform.runLater(() -> {
                    if (registerSuccessful) {
                        showStatus("Регистрация прошла успешно.", "GREEN");
                    } else {
                        showStatus("Пользователь с таким именем уже существует или потеряно соединение с сервером.", "RED");
                    }
                    setInputsDisability(false);
                });
            }).start();

        } else if (login.getText().isEmpty() || password.getText().isEmpty() || sec_pass.getText().isEmpty()) {
            Platform.runLater(() -> showStatus("Не все поля заполнены.", "RED"));
        } else if (!Objects.equals(password.getText(), sec_pass.getText())) {
            Platform.runLater(() -> showStatus("Пароли не совпадают.", "RED"));
        } else {
            Platform.runLater(() -> showStatus("Something wrong...", "RED"));
        }
    }

    public void onButtonBack() {
        try {
            Stage stage = (Stage) back_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "auth.fxml", "Pong Masters");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
