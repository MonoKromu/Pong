package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.Main;
import ru.mono.pong.State;
import ru.mono.pong.transport.apiClient;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class ProfileController {
    @FXML
    PasswordField old_pass_lab, new_pass_lab, sec_new_pass_lab;
    @FXML
    Button accept_btn, menu_btn;
    @FXML
    Label change_lab;

    public static String sha256Hash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes());
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void onButtonChangePass() {
        new Thread(() -> {
            accept_btn.setDisable(true);
            old_pass_lab.setDisable(true);
            menu_btn.setDisable(true);
            new_pass_lab.setDisable(true);
            sec_new_pass_lab.setDisable(true);
            change_lab.setVisible(false);
            if (Objects.equals(new_pass_lab.getText(), sec_new_pass_lab.getText())) {
                String hashedOldPass;
                String hashedNewPass;
                try {
                    hashedNewPass = sha256Hash(old_pass_lab.getText());
                    hashedOldPass = sha256Hash(old_pass_lab.getText());
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                boolean response = apiClient.putPassword(State.currentUser.login, hashedNewPass, hashedOldPass);
                if (response) {
                    System.out.println("-- Change pass successful");
                    change_lab.setText("Пароль изменен!");
                    change_lab.setTextFill(Paint.valueOf("GREEN"));
                    change_lab.setVisible(true);
                } else {
                    change_lab.setTextFill(Paint.valueOf("RED"));
                    change_lab.setText("Пароль неверный!");
                    change_lab.setVisible(true);
                }
            } else {
                change_lab.setTextFill(Paint.valueOf("RED"));
                change_lab.setText("Новые пароли не совпадают!");
                change_lab.setVisible(true);
            }
            accept_btn.setDisable(false);
            old_pass_lab.setDisable(false);
            menu_btn.setDisable(false);
            new_pass_lab.setDisable(false);
            sec_new_pass_lab.setDisable(false);
        }).start();
    }

    public void onButtonToMenu() {
        Platform.runLater(() -> {
            Stage stage = (Stage) menu_btn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
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
        });
    }
}
