package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mono.pong.Main;
import ru.mono.pong.State;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.transport.dtos.Room;
import ru.mono.pong.utils.SceneManager;

import java.io.IOException;
import java.util.ArrayList;

import static ru.mono.pong.State.currentUser;

public class RoomsController {
    private static final Logger logger = LoggerFactory.getLogger(RoomsController.class);
    @FXML
    Button refresh_btn;
    @FXML
    static Button create_btn;
    @FXML
    Button toMenu_btn;
    @FXML
    GridPane rooms_grid;

    private CreateRoomController child;

    public void initialize() {
        Platform.runLater(this::onButtonRefresh);
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setClose();
        }).start();
    }

    public void setClose() {
        Stage stage = (Stage) toMenu_btn.getScene().getWindow();
        stage.setOnCloseRequest(_ -> {
        });
    }

    public void onButtonRefresh() {
        new Thread(() -> {
            ArrayList<Room> rooms = HttpClient.getRooms();
            int i = 0;
            for (var room : rooms) {
                addRoom(room.name, room.host.login, i, room.id);
                i++;
            }
        }).start();
    }

    public void addRoom(String roomName, String host, int row, int id) {
        Platform.runLater(() -> {
            Label labelRoomName = new Label(roomName);
            labelRoomName.setFont(new Font(18));
            labelRoomName.setPadding(new Insets(0, 0, 0, 10));
            labelRoomName.setMinHeight(45);
            labelRoomName.setMinWidth(197);
            labelRoomName.setPrefHeight(45);
            labelRoomName.setPrefWidth(197);
            Label labelLogin = new Label(host);
            labelLogin.setFont(new Font(18));
            labelLogin.setMinHeight(45);
            labelLogin.setMinWidth(197);
            labelLogin.setPrefHeight(45);
            labelLogin.setPrefWidth(197);
            Button joinButton = new Button("Присоединиться");
            joinButton.setFont(new Font(14));
            joinButton.setMinHeight(29);
            joinButton.setMinWidth(163);
            joinButton.setPrefHeight(29);
            joinButton.setPrefWidth(163);
            joinButton.setOnAction(_ -> joinRoom(id));
            rooms_grid.add(labelRoomName, 0, row);
            rooms_grid.add(labelLogin, 1, row);
            rooms_grid.add(joinButton, 2, row);
        });
    }

    public void joinRoom(int id) {
        new Thread(() -> {
            boolean response = HttpClient.putRoom(id, currentUser);
            if (response) {
                logger.info("Join to room {}", State.currentRoomId);
                Platform.runLater(this::startGame);
            }
        }).start();
    }

    public void startGame() {
        try {
            Stage stage = (Stage) create_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "game.fxml", "Pong Masters");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonCreate() {
        Stage createWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("create-room.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 500, 300);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        createWindow.initModality(Modality.APPLICATION_MODAL);
        createWindow.setTitle("Rooms");
        createWindow.setScene(scene);
        createWindow.setResizable(false);

        child = fxmlLoader.getController();
        child.setPapa(this);

        createWindow.showAndWait();
    }

    public void switchToGame() {
        try {
            Stage stage = (Stage) toMenu_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "game.fxml", "Pong Masters");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonToMenu() {
        try {
            Stage stage = (Stage) toMenu_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "menu.fxml", "Menu");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
