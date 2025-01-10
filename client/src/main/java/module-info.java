module ru.mono.pong {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;


    opens ru.mono.pong to javafx.fxml;
    opens ru.mono.pong.transport to com.google.gson;
    exports ru.mono.pong;
}