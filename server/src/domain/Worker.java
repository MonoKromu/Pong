package domain;

import dtos.Action;
import dtos.GameState;
import endpoints.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private Runnable send;
    private GameState state;

    public int plank1, plank2;
    public int plank1Points, plank2Points;
    public boolean lastWinner1;
    public double ballX, ballY, ballAngle, ballSpeed, ballSpeedX, ballSpeedY;
    public final int MAX_POINTS = 10;
    public boolean gameEnded = false;

    public final int SCREEN_WIDTH = 1024;
    public final int SCREEN_HEIGHT = 768;
    public final int PLANK1_X = 23;
    public final int PLANK2_X = SCREEN_WIDTH - 23;
    public final int PLANK_HEIGHT = 90;

    public final int MOVE_STEP = 5;
    public final int BASE_SPEED = 5;
    public final double ACC_STEP = 0.2;

    public final double BOTTOM_LIMIT = -Math.PI / 4;
    public final double TOP_LIMIT = Math.PI / 4;

    public Worker(GameState state, Runnable send) {
        this.send = send;
        this.state = state;
    }

    public void start() {
        reset();
        new Thread(() -> {
            while(!gameEnded){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                moveBall();
                updateState();
                send.run();
            }
        }).start();
    }

    public void update(Action a) {
        if (a.id == 1) {
            if (a.key == 'w') plank1 += MOVE_STEP;
            else if (a.key == 's') plank1 -= MOVE_STEP;
        } else if (a.id == 2) {
            if (a.key == 'w') plank2 += MOVE_STEP;
            else if (a.key == 's') plank2 -= MOVE_STEP;
        }
    }

    private void reset() {
        double angle = (Math.random() * (TOP_LIMIT - BOTTOM_LIMIT) + BOTTOM_LIMIT);
        if (lastWinner1) angle = Math.PI - angle;

        ballY = (int) ((double) SCREEN_HEIGHT / 2);
        ballX = (int) ((double) SCREEN_WIDTH / 2);
        changeBallSpeed(angle, BASE_SPEED);
    }

    private void endRound(int plank) {
        if (plank == 1) {
            plank1Points += 1;
            lastWinner1 = true;
        } else {
            plank2Points += 1;
            lastWinner1 = false;
        }

        if (plank1Points == MAX_POINTS || plank2Points == MAX_POINTS){
            logger.info("сосал");
            gameEnded = true;
        }
        else reset();
    }

    private void changeBallSpeed(double angle, double speed) {
        ballSpeed = speed;
        ballAngle = angle;
        ballSpeedX = Math.cos(ballAngle) * ballSpeed;
        ballSpeedY = Math.sin(ballAngle) * ballSpeed;
    }

    private void moveBall() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;
        if (ballY >= SCREEN_HEIGHT || ballY <= 0) {
            changeBallSpeed(Math.PI * 2 - ballAngle, ballSpeed);
        } else if (ballX >= SCREEN_WIDTH) {
            endRound(1);
        } else if (ballX <= 0) {
            endRound(2);
        } else if (ballX <= PLANK1_X && (ballY <= plank1 && ballY >= plank1 + PLANK_HEIGHT))
            changeBallSpeed(Math.PI - ballAngle, ballSpeed + ACC_STEP);
        else if (ballX >= PLANK2_X && (ballY <= plank2 && ballY >= plank2 + PLANK_HEIGHT))
            changeBallSpeed(Math.PI - ballAngle, ballSpeed + ACC_STEP);
    }

    private void updateState(){
        state.ballX = ballX;
        state.ballY = ballY;
        state.plank1 = plank1;
        state.plank2 = plank2;
        state.plank1Points = plank1Points;
        state.plank2Points = plank2Points;
    }
}
