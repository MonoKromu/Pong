package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    Label points_label;
    @FXML
    Button play_btn, rating_btn, exit_btn, account_btn;

    public void onButtonPlay(){

    }

    public void onButtonRating() {
        Stage stage = (Stage) rating_btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("rating.fxml"));
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

    }
}
