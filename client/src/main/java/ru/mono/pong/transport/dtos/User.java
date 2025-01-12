package ru.mono.pong.transport.dtos;

public class User {
    public String login;
    public String password;
    public String newPassword;
    public int points;

    public User(String login) {
        this.login = login;
    }

    public User(String login, int points) {
        this.login = login;
        this.points = points;
    }

    public User(String login, int points, String password) {
        this.login = login;
        this.points = points;
        this.password = password;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, String newPassword) {
        this.login = login;
        this.password = password;
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return login + " " + password + " " + points;
    }
}
