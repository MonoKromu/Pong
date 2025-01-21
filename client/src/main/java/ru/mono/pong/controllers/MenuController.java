package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ru.mono.pong.Main;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.SceneManager;

import java.io.IOException;
import java.util.Objects;

public class MenuController {
    @FXML
    Label points_label;
    @FXML
    Button play_btn, rating_btn, exit_btn, profile_btn, changeUser_btn;

    public void initialize() {
        Platform.runLater(() -> {
            State.currentUser = HttpClient.postAuth(State.currentUser.login, AuthController.hashedPassword);
            profile_btn.setText(Objects.requireNonNull(State.currentUser).login);
            points_label.setText(String.valueOf(State.currentUser.points));
        });
    }

    public void onButtonPlay() {
        try {
            Stage stage = (Stage) play_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "rooms.fxml", "Game Rooms");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonRating() {
        try {
            Stage stage = (Stage) rating_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "rating.fxml", "Players Rating");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonToAuth() {
        try {
            Stage stage = (Stage) changeUser_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "auth.fxml", "Pong Masters");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonAccount() {
        try {
            Stage stage = (Stage) profile_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "profile.fxml", "Your Profile");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonExit() {
        Platform.exit();
    }
}
