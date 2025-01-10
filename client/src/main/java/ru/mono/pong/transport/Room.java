package ru.mono.pong.transport;

public class Room {
    public String name;
    public User host;
    public User guest;
    public int id;

    public Room(String name, User host, int id){
        this.name = name;
        this.host = host;
        this.id = id;
    }

    public Room(String name, User host) {
        this.name = name;
        this.host = host;
    }

    public Room(User guest, int id) {
        this.guest = guest;
        this.id = id;
    }

}

