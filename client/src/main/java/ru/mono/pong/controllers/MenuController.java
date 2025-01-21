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
        Stage stage = (Stage) profile_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("rooms.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1024, 768);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Game rooms");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void onButtonRating() {
        Stage stage = (Stage) rating_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("rating.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1024, 768);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Players rating");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void onButtonToAuth() {
        Stage stage = (Stage) changeUser_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("auth.fxml"));
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

    public void onButtonAccount() {
        Stage stage = (Stage) profile_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("profile.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1024, 768);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Your profile");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void onButtonExit() {
        Platform.exit();
    }
}
