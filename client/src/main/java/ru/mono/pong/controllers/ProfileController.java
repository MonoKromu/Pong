package ru.mono.pong.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.mono.pong.State;
import ru.mono.pong.exceptions.BadNewPasswordException;
import ru.mono.pong.transport.HttpClient;
import ru.mono.pong.utils.HashManager;
import ru.mono.pong.utils.SceneManager;

import java.io.IOException;
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
            State.currentUser = HttpClient.postAuth(State.currentUser.login, State.currentUser.password);
            login_lab.setText(Objects.requireNonNull(State.currentUser).login);
            points_lab.setText(String.valueOf(State.currentUser.points));
        });
    }

    public void setDisable() {
        accept_btn.setDisable(true);
        old_pass_lab.setDisable(true);
        menu_btn.setDisable(true);
        new_pass_lab.setDisable(true);
        sec_new_pass_lab.setDisable(true);
        change_lab.setVisible(false);
    }

    public void unsetDisable() {
        accept_btn.setDisable(false);
        old_pass_lab.setDisable(false);
        menu_btn.setDisable(false);
        new_pass_lab.setDisable(false);
        sec_new_pass_lab.setDisable(false);
    }

    private void showStatus(String message, String color, Boolean visible) {
        change_lab.setText(message);
        change_lab.setTextFill(Paint.valueOf(color));
        change_lab.setVisible(visible);
    }

    public void onButtonChangePass() {
        Platform.runLater(() -> {
            setDisable();

            if (!PasswordManager.containsLetters(new_pass_lab.getText())) try {
                throw new BadNewPasswordException(
                        "Password must contain lowercase and uppercase letters", change_lab);
            } catch (BadNewPasswordException e) {
                throw new RuntimeException(e);
            }
            if (!PasswordManager.containsNumber(new_pass_lab.getText())) try {
                throw new BadNewPasswordException(
                        "Password must contain numbers", change_lab);
            } catch (BadNewPasswordException e) {
                throw new RuntimeException(e);
            }

            if (Objects.equals(old_pass_lab.getText(), new_pass_lab.getText()) || Objects.equals(old_pass_lab.getText(), sec_new_pass_lab.getText())) {
                showStatus("Пароли не должны совпадать!", "RED", true);
            } else if (Objects.equals(new_pass_lab.getText(), sec_new_pass_lab.getText())) {
                String hashedOldPass;
                String hashedNewPass;
                hashedOldPass = HashManager.sha256Hash(old_pass_lab.getText());
                hashedNewPass = HashManager.sha256Hash(new_pass_lab.getText());
                boolean response = HttpClient.putPassword(State.currentUser.login, hashedOldPass, hashedNewPass);
                if (response) {
                    State.currentUser.password = hashedNewPass;
                    showStatus("Пароль успешно изменен!", "GREEN", true);
                } else {
                    showStatus("Пароль неверный!", "RED", true);
                }
            } else {
                showStatus("Новые пароли не совпадают!", "RED", true);
            }
            unsetDisable();
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
