package dtos;

public class User {
    public String login;
    public String password;
    public String newPassword;
    public int points;

    public User(String login, int points) {
        this.login = login;
        this.points = points;
    }

    public User(String login, String password, int points) {
        this.login = login;
        this.password = password;
        this.points = points;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return login + " " + password + " " + points;
    }
}