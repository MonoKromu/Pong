package ru.enoughLev;

public class User {
    public String Nickname;
    public int Points;
    public String Password;

    public User(String nick, int points){
        this.Nickname = nick;
        this.Points = points;
    }

    public User(String nick, int points, String password){
        this.Nickname = nick;
        this.Points = points;
        this.Password = password;
    }


}
