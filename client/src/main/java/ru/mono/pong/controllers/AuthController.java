package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.HashManager;
import ru.mono.pong.utils.SceneManager;

import java.io.*;
import java.util.Objects;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @FXML
    Button enter_btn, reg_btn;
    @FXML
    Label err_lab;
    @FXML
    TextField login;
    @FXML
    PasswordField password;

    public static String hashedPassword;


    public void onButtonEnter() {
        waiting();
        new Thread(() -> {
            hashedPassword = HashManager.sha256Hash(password.getText());
            State.currentUser = HttpClient.postAuth(login.getText(), hashedPassword);
            Platform.runLater(() -> {
                if (!Objects.equals(State.currentUser, null)) {
                    logger.info("Authentication successful");
                    try {
                        Stage stage = (Stage) reg_btn.getScene().getWindow();
                        SceneManager.loadScene(stage, "menu.fxml", "Menu");
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.info("Authentication bad");
                    err_lab.setVisible(true);
                }
            });
            nextStep();
        }).start();
    }

    public void nextStep() {
        enter_btn.setDisable(false);
        reg_btn.setDisable(false);
        login.setDisable(false);
        password.setDisable(false);
    }

    public void waiting() {
        err_lab.setVisible(false);
        enter_btn.setDisable(true);
        reg_btn.setDisable(true);
        login.setDisable(true);
        password.setDisable(true);
    }

    public void onButtonReg() {
        try {
            Stage stage = (Stage) reg_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "reg.fxml", "Registration");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
