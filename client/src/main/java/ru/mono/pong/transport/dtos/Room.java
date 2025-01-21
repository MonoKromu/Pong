package ru.mono.pong.transport.dtos;

public class Room {
    public int id;
    public String name;
    public User host;
    public User guest;

    public Room(String name, User host) {
        this.name = name;
        this.host = host;
    }

    public Room(int id, User guest) {
        this.id = id;
        this.guest = guest;
    }

}

