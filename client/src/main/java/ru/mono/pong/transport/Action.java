package ru.mono.pong.transport;

public class Action {
    public int id;
    public int player;
    public char key;

    public Action(int id, int player, char key){
        this.id = id;
        this.player = player;
        this.key = key;
    }
}
