package ru.mono.pong;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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

    @FXML
    public void initialize(){
        player1.setUserData(0);
        player2.setUserData(0);
        centerX = ball.getCenterX();
        centerY = ball.getCenterY();

        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::onKeyPressed);
                newScene.setOnKeyReleased(this::onKeyReleased);
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
        KeyCode code = e.getCode();
        switch(code){
            case UP, DOWN: player2.setUserData(0);
                break;
            case W, S: player1.setUserData(0);
                break;
        }
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