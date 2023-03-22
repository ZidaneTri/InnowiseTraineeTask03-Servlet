package com.innowise.task03.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private final String DRIVER_CLASS_NAME = "org.h2.Driver";
    private final String CONNECTION_URL = "jdbc:h2:~/InnowiseTest;AUTO_SERVER=TRUE";
    private final String LOGIN = "admin";
    private final String PASSWORD = "adminpass";

    private static ConnectionFactory instance = null;

    private ConnectionFactory() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        conn = DriverManager.getConnection(CONNECTION_URL, LOGIN, PASSWORD);
        return conn;
    }

    public static ConnectionFactory getInstance() {
        ConnectionFactory localInstance = instance;
        if (localInstance == null) {
            synchronized (ConnectionFactory.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ConnectionFactory();
                }
            }
        }
        return localInstance;
    }
}
