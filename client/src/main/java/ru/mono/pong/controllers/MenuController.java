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

import java.io.IOException;

public class MenuController {
    @FXML
    Label points_label;
    @FXML
    Button play_btn, rating_btn, exit_btn, profile_btn;

    public void initialize() {
        Platform.runLater(() -> {
            profile_btn.setText(State.currentUser.login);
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

    public void onButtonExit() {
        Platform.exit();
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
}
