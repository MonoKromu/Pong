package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.Main;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.HashManager;
import ru.mono.pong.utils.SceneManager;

import java.io.*;
import java.util.Objects;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @FXML
    Button enter_btn, reg_btn, connect_btn;
    @FXML
    Label err_lab, errorConnect;
    @FXML
    TextField login;
    @FXML
    PasswordField password;
    @FXML
    TextField serverAddress;
    public static String hashedPassword;



    public void onButtonEnter() {
        err_lab.setVisible(false);
        enter_btn.setDisable(true);
        reg_btn.setDisable(true);
        login.setDisable(true);
        password.setDisable(true);
        new Thread(() -> {
            hashedPassword = HashManager.sha256Hash(password.getText());
            State.currentUser = HttpClient.postAuth(login.getText(), hashedPassword);
            Platform.runLater(() -> {
                if (!Objects.equals(State.currentUser, null)) {
                    logger.info("Auth successful");
                    try {
                        Stage stage = (Stage) reg_btn.getScene().getWindow();
                        SceneManager.loadScene(stage, "menu.fxml", "Menu");
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.info("Auth bad");
                    err_lab.setVisible(true);
                }
            });
            enter_btn.setDisable(false);
            reg_btn.setDisable(false);
            login.setDisable(false);
            password.setDisable(false);
        }).start();
    }

    public void onButtonReg() {
        try {
            Stage stage = (Stage) reg_btn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("reg.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setTitle("Registration");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
