package ru.mono.pong.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mono.pong.Main;

import java.io.IOException;

public class SceneManager {
    public static void loadScene(Stage stage, String fxml, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);
    }
}
