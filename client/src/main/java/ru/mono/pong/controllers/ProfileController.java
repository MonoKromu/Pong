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
import ru.mono.pong.exceptions.BadNewPasswordException;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.SceneManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import ru.mono.pong.utils.PasswordManager;

public class ProfileController {
    @FXML
    PasswordField old_pass_lab, new_pass_lab, sec_new_pass_lab;
    @FXML
    Button accept_btn, menu_btn;
    @FXML
    Label change_lab, login_lab, points_lab;

    public void initialize() {
        Platform.runLater(() -> {
            State.currentUser = HttpClient.postAuth(State.currentUser.login, AuthController.hashedPassword);
            login_lab.setText(Objects.requireNonNull(State.currentUser).login);
            points_lab.setText(String.valueOf(State.currentUser.points));
        });
    }

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
        Platform.runLater(() -> {
            if(!PasswordManager.containsLetters(new_pass_lab.getText())) try {
                throw new BadNewPasswordException(
                        "Password must contain lowercase and uppercase letters", change_lab);
            } catch (BadNewPasswordException e) {
                throw new RuntimeException(e);
            }
            if(!PasswordManager.containsNumber(new_pass_lab.getText())) try {
                throw new BadNewPasswordException(
                        "Password must contain numbers", change_lab);
            } catch (BadNewPasswordException e) {
                throw new RuntimeException(e);
            }

            accept_btn.setDisable(true);
            old_pass_lab.setDisable(true);
            menu_btn.setDisable(true);
            new_pass_lab.setDisable(true);
            sec_new_pass_lab.setDisable(true);
            change_lab.setVisible(false);
            if (Objects.equals(old_pass_lab.getText(), new_pass_lab.getText()) || Objects.equals(old_pass_lab.getText(), sec_new_pass_lab.getText())) {
                change_lab.setText("Пароли не должны совпадать!");
                change_lab.setTextFill(Paint.valueOf("RED"));
                change_lab.setVisible(true);
            } else if (Objects.equals(new_pass_lab.getText(), sec_new_pass_lab.getText())) {
                String hashedOldPass;
                String hashedNewPass;
                try {
                    hashedOldPass = sha256Hash(old_pass_lab.getText());
                    hashedNewPass = sha256Hash(new_pass_lab.getText());
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                boolean response = HttpClient.putPassword(State.currentUser.login, hashedOldPass, hashedNewPass);
                if (response) {
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
        });
    }

    public void onButtonToMenu() {
        try {
            Stage stage = (Stage) menu_btn.getScene().getWindow();
            SceneManager.loadScene(stage, "menu.fxml", "Menu");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
