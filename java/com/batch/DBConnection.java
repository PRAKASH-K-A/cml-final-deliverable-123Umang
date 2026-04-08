package com.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/trading_system";
    private static final String USER = "root";
    private static final String PASS = "Umang21";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
