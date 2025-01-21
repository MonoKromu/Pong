package ru.mono.pong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import ru.mono.pong.transport.UdpClient;
import ru.mono.pong.transport.dtos.Action;

import java.io.IOException;
import java.util.Objects;



public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("connect.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/pong.png"))));
        stage.setTitle("Pong Masters");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        launch();
    }
}