package ru.mono.pong.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import ru.mono.pong.State;
import ru.mono.pong.transport.Action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameController {
    @FXML
    Circle ball;
    @FXML
    Rectangle field, player1, player2;
    @FXML
    Pane pane;

    double ballAngle;
    double ballSpeed;
    double ballSpeedX;
    double ballSpeedY;
    double centerX;
    double centerY;

    static final String serverAddress = "95.181.27.100"; // Адрес сервера
    static final int PORT = 9876;              // Порт сервера
    private static final DatagramSocket clientSocket;
    static {
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    @FXML
    public void initialize(){
        player1.setUserData(0);
        player2.setUserData(0);
        centerX = ball.getCenterX();
        centerY = ball.getCenterY();

        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::onKeyPressed);
                newScene.setOnKeyReleased(e -> {
                    Action action = new Action(1, 1, 'q');
                    onKeyReleased(e);
                });
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private final double FPS = 120;
            private final double TIME_PER_FRAME = 1e9/FPS;
            private long last;
            @Override
            public void handle(long now) {
                if((double) (now - last) >= TIME_PER_FRAME){
                    last = now;
                    moveBall();
                    movePlayers();
                }
            }
        };
        reset();
        timer.start();
    }

    private void reset(){
        double randomValue;
        do {
            randomValue = Math.random(); // Генерация числа от 0.1 до 1.0
        } while (isInExcludedRange(randomValue));

        double angle = randomValue*Math.PI*2; //Math.random();
        ball.setCenterY(centerY);
        ball.setCenterX(centerX);
        changeBallSpeed(angle, 5);
    }

    private static boolean isInExcludedRange(double number) {
        return (number >= 0.2 && number <= 0.3) || (number >= 0.7 && number <= 0.8);
    }

    private void moveBall(){
        ball.setCenterX(ball.getCenterX()+ballSpeedX);
        ball.setCenterY(ball.getCenterY()+ballSpeedY);
        Bounds bounds = ball.getBoundsInParent();
        if(bounds.getMinY()<0 || bounds.getMaxY() > field.getHeight()){
            changeBallSpeed(Math.PI*2-ballAngle, ballSpeed+0.5);
        }
        for(Rectangle r : new Rectangle[]{player1, player2}){
            Shape intersection = Shape.intersect(ball,r);
            if(intersection.getBoundsInLocal().getWidth()>0){
                double angleMult = (r.getBoundsInParent().getCenterY() - bounds.getCenterY()) / r.getHeight() * 0.5;
                System.out.println(angleMult);
                changeBallSpeed((Math.PI-ballAngle) - (Math.PI-ballAngle)*angleMult, ballSpeed+0.2);
            }
        }
        if(bounds.getMinX()<0 || bounds.getMaxX() > field.getWidth()){
            reset();
        }
    }

    private void changeBallSpeed(double angle, double speed){
        while (angle<0 || angle>Math.PI*2){
            if(angle<0) angle+=Math.PI*2;
            if(angle>Math.PI*2) angle-=Math.PI*2;
        }
        ballSpeed = speed;
        ballAngle = angle;
        ballSpeedX = Math.cos(ballAngle)*ballSpeed;
        ballSpeedY = Math.sin(ballAngle)*ballSpeed;
    }

    private void movePlayers(){
        for(Rectangle p : new Rectangle[]{player1,player2}){
            Bounds bounds = p.getBoundsInParent();
            int move = (int)p.getUserData();
            if(move == 1 && bounds.getMinY()>10) p.setY(p.getY()-5);
            if(move == -1 && bounds.getMaxY() < field.getHeight()-10) p.setY(p.getY()+5);
        }
    }

    private void onKeyReleased(KeyEvent e) {
        KeyCode key = e.getCode();
        switch(key){
            case UP, DOWN: {
                sendKeyData(key.toString());
                System.out.println(key);
                player2.setUserData(0);
            }
                break;
            case W, S: {
                sendKeyData(key.toString());
                System.out.println(key);
                player1.setUserData(0);
            }
                break;
        }
    }

    private static void sendKeyData(String key) {
        try {
            String message = key;
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(serverAddress); // Измените на нужный адрес
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
            clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String receiveData() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Ответ от сервера: " + receivedMessage);
        return receivedMessage;
    }

    private void onKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        switch(code){
            case UP: player2.setUserData(1);
                break;
            case DOWN: player2.setUserData(-1);
                break;
            case W: player1.setUserData(1);
                break;
            case S: player1.setUserData(-1);
                break;
        }
    }
}