package dtos;

import java.net.InetAddress;

public class Room {
    public int id;
    public String name;
    public User host;
    public InetAddress hostIP;
    public User guest;
    public InetAddress guestIP;
    public boolean gameStarted = false;
}
