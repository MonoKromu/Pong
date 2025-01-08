package ru.enoughLev;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseOperations {

    private String link = "localhost";
    private final String DB_URL = "jdbc:mysql://%s:3306/new_test_db".formatted(link);
    private final String USER = "work_user";
    private final String PASS = "qwe123qwe";

    /// Получение списка пользователей для вывода в "Таблица рейтинга" (ник, очки)
    public ArrayList<User> getRating() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users_table");
            ArrayList<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getString(2), Integer.parseInt(rs.getString(4))));
            }
            for (var user : users) {
                System.out.println(user.Nickname + " \t" + user.Points);
            }
            rs.close();
            stmt.close();
            conn.close();
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// Получение (ника, очков, пароля) для хуй знает
    public User getUser(String nick, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users_table WHERE Nickname LIKE '%s' and Password LIKE '%s';".formatted(nick, password));
            rs.next();
            User user = new User(rs.getString(2), Integer.parseInt(rs.getString(4)), rs.getString(3));
            System.out.println(user.Nickname + " \t"+ user.Password + " \t" + user.Points);
            rs.close();
            stmt.close();
            conn.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// Изменение (пароля)
    public boolean putPassword(String nick, String oldPass, String newPass) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            if (getUser(nick, oldPass) != null) {
                stmt.executeUpdate("UPDATE `users_table` SET `Password` = '%s' WHERE Nickname LIKE '%s';".formatted(newPass, nick));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// Добавление (пользователя)
    public boolean postUser(String nick, String password, String secondPass) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            if (Objects.equals(password, secondPass)) {
                stmt.executeUpdate("INSERT INTO `users_table` (`Nickname`, `Password`) VALUES ('%s', '%s');".formatted(nick, password));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// Прибавление (очков)
    public boolean putUserPoints(String nick) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE users_table SET Points = Points + 10 WHERE Nickname LIKE '%s';".formatted(nick));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}