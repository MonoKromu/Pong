package ru.enoughLev;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseOperations data = new DatabaseOperations();
        System.out.println();

        data.getRating();
        System.out.println();

        data.getUser("lev", "qwe");
        //data.putPassword("lev", "qwe", "123");
        //data.postUser("nayd", "123", "123");
        //data.putUserPoints("lev");
        //data.getRating();
    }

}