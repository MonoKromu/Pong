package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.mono.pong.transport.User;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.util.ArrayList;

public class RatingController {
    @FXML
    Button refresh_btn, exit_btn;
    @FXML
    GridPane rating_table;

    public void onButtonRefresh() {
        new Thread(() -> {
            ArrayList<User> users = apiClient.getRating();
            Platform.runLater(() -> {
                int i = 1;
                for (var user : users) {
                    Label increment = new Label(String.valueOf(i));
                    GridPane.setHalignment(increment, HPos.CENTER);
                    Label nick = new Label(user.login);
                    Label points = new Label(String.valueOf(user.points));
                    increment.setFont(new Font(20));
                    nick.setFont(new Font(20));
                    nick.setPadding(new Insets(0, 0, 0, 5));
                    points.setFont(new Font(20));
                    points.setPadding(new Insets(0, 0, 0, 5));
                    rating_table.add(increment, 0, i);
                    rating_table.add(nick, 1, i);
                    rating_table.add(points, 2, i);
                    i++;
                }
            });
        }).start();

    }

    public void onButtonExit() {
        Stage stage = (Stage) exit_btn.getScene().getWindow();
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
}
