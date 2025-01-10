package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.transport.User;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.util.Objects;

public class AuthWindow {
    @FXML
    Button enter_btn, reg_btn;
    @FXML
    Label salute, err_lab;
    @FXML
    TextField login;
    @FXML
    PasswordField password;
    @FXML
    TextArea clientOutput;


    public void onButtonEnter() {
        err_lab.setVisible(false);
        enter_btn.setDisable(true); reg_btn.setDisable(true);
        login.setDisable(true); password.setDisable(true);
        new Thread(() -> {
            User response = apiClient.postAuth(login.getText(), password.getText());
            State.currentUser = response;
            Platform.runLater(() -> {
                //clientOutput.setText(State.currentUser.login + " " + State.currentUser.points);
                if (!Objects.equals(State.currentUser, null)) {
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
                    System.out.println("-- Auth successful");
                } else {
                    System.out.println("-- Auth bad");
                    err_lab.setVisible(true);
                }
            });
            enter_btn.setDisable(false); reg_btn.setDisable(false);
            login.setDisable(false); password.setDisable(false);
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
