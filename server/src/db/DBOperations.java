package db;

import dtos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class DBOperations {
    private static final Logger logger = LoggerFactory.getLogger(DBOperations.class);
    private final static String baseURI = "jdbc:mysql://%s:3306/new_test_db";
    private final static String DB_URL = baseURI.formatted("95.181.27.102");
    private final static String LOGIN = "work_user";
    private final static String PASSWORD = "qwe123qwe";

    /// Получение списка пользователей для вывода в "Таблица рейтинга" (ник, очки)
    public static ArrayList<User> getRating() {
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT login, points FROM users_table ORDER BY points DESC")) {
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

    // Получение (ника, пароля, очков) для авторизации
    public static User getUser(String login, String password) {
        logger.info("SELECTING USER: " + login);
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users_table WHERE login LIKE '%s' and password LIKE '%s';".formatted(login, password))) {
            if (rs.next()) {
                logger.info("USER FOUND: " + login);
                return new User(rs.getString(2), rs.getString(3), Integer.parseInt(rs.getString(4)));
            } else {
                logger.info("USER NOT FOUND: " + login);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// Добавление (пользователя)
    public static boolean postUser(String login, String password) {
        logger.info("INSERTING USER: " + login);
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement()) {
            if (getUser(login, password) == null) {
                stmt.executeUpdate("INSERT INTO `users_table` (`login`, `password`, `points`) VALUES ('%s', '%s', 0);".formatted(login, password));
                logger.info("USER INSERTED");
                return true;
            } else {
                logger.info("USER NOT INSERTED");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean putPassword(String login, String oldPass, String newPass) {
        logger.info("CHECK PASSWORD FOR USER {}", login);
        try (Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users_table WHERE login LIKE '%s' and password LIKE '%s';".formatted(login, oldPass))) {
            if (rs.next()) {
                logger.info("CHANGE PASSWORD FOR USER {}", login);
                stmt.executeUpdate("UPDATE users_table SET password = '%s' WHERE login LIKE '%s';".formatted(newPass, login));
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /// Прибавление (очков)
    public static boolean putUserPoints(String login) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE users_table SET points = points + 10 WHERE login LIKE '%s';".formatted(login));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
