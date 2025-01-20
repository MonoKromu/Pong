package ru.mono.pong;

import ru.mono.pong.transport.dtos.GameState;
import ru.mono.pong.transport.dtos.User;

public class State {
    public static String serverAddress;
    public static User currentUser;
    public static int currentRoomId;
    public static int currentPlayerId;
    public static GameState currentGameState = new GameState();
}
