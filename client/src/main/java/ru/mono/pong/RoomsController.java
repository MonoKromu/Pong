package ru.mono.pong;

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
import ru.mono.pong.transport.Room;
import ru.mono.pong.transport.User;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.util.ArrayList;

import static ru.mono.pong.State.currentUser;

public class RoomsController {
    @FXML
    Button refresh_btn, create_btn, toMenu_btn;
    @FXML
    GridPane rooms_grid;

    public void initialize() {
        //Platform.runLater(this::onButtonRefresh);
    }

    public void onButtonRefresh() {
        new Thread(() -> {
            //ArrayList<Room> rooms = apiClient.getRooms();
            User user1 = new User("lev");
            User user2 = new User("Ivan");
            Room room1 = new Room("Комната", user1, 1);
            Room room2 = new Room("Second", user2, 2);
            ArrayList<Room> rooms = new ArrayList<Room>();
            rooms.add(room1);
            rooms.add(room2);
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
            boolean response = apiClient.postGame(currentUser, id);
            if (response){
                
            }
        }).start();
        System.out.printf("\nEntering room %s", id);
    }

    public void onButtonCreate() {
        Stage createWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("create-room.fxml"));
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
        createWindow.showAndWait();

    }

    public void onButtonToMenu() {
        Stage stage = (Stage) toMenu_btn.getScene().getWindow();
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
