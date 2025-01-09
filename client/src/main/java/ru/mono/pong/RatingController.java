package ru.mono.pong;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import ru.mono.pong.transport.User;
import ru.mono.pong.transport.apiClient;

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
                    Label nick = new Label(user.Nickname);
                    Label points = new Label(String.valueOf(user.Points));
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
        Platform.exit();
    }
}
