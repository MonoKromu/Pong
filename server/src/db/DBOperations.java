package db;

import dtos.User;

import java.sql.*;
import java.util.ArrayList;

public class DBOperations {

    private final static String baseURI = "jdbc:mysql://%s:3306/new_test_db";
    private final static String DB_URL = baseURI.formatted("95.181.27.102");
    private final static String LOGIN = "work_user";
    private final static String PASSWORD = "qwe123qwe";

    /// Получение списка пользователей для вывода в "Таблица рейтинга" (ник, очки)
    public static ArrayList<User> getRating() {
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Nickname, Points FROM users_table ORDER BY Points DESC")) {
            ArrayList<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(rs.getString(1), Integer.parseInt(rs.getString(2))));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// Получение (ника, очков, пароля) для авторизации
    public static User getUser(String nick, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users_table WHERE Nickname LIKE '%s' and Password LIKE '%s';".formatted(nick, password))) {
            if (rs.next()) {
                User user = new User(rs.getString(2), Integer.parseInt(rs.getString(4)), rs.getString(3));
                System.out.println(user.Nickname + " \t" + user.Password + " \t" + user.Points);
                return user;
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean putPassword(String nick, String oldPass, String newPass) {
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement()) {

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
    public static boolean postUser(String nick, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement()) {
            if (getUser(nick, password) == null) {
                stmt.executeUpdate("INSERT INTO `users_table` (`Nickname`, `Password`) VALUES ('%s', '%s');".formatted(nick, password));
                return true;
            }
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// Прибавление (очков)
    public static boolean putUserPoints(String nick) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE users_table SET Points = Points + 10 WHERE Nickname LIKE '%s';".formatted(nick));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
