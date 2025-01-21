package ru.mono.pong.exceptions;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class BadNewPasswordException extends Exception {
    HashMap<String, String> messageRu = new HashMap<>();
    {
        messageRu.put("Password must contain lowercase and uppercase letters",
                "Пароль должен содержать строчные и заглавные буквы");
        messageRu.put("Password must contain numbers",
                "Пароль должен содержать цифры");
    }

    public BadNewPasswordException(String message, Label label){
        super(message);
        String m = messageRu.getOrDefault(message, "Незивестная ошибка");
        label.setText(m);
        label.setTextFill(Color.RED);
        label.setVisible(true);
    }
}
