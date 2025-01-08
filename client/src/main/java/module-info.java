module ru.mono.pong {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ru.mono.pong to javafx.fxml;
    exports ru.mono.pong;
}