package com.plushnode.banana.storage.sql;

import java.sql.*;
import java.util.Properties;

public abstract class Database {
    private Connection connection = null;
    private ConnectProperties connectProperties;

    public Database(ConnectProperties properties) {
        this.connectProperties = properties;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public boolean connect() {
        String host = connectProperties.getHost();
        int port = connectProperties.getPort();
        String database = connectProperties.getDatabase();
        String engine = connectProperties.getEngine();

        try {
            if (engine.equalsIgnoreCase("sqlite")) {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                this.connection = DriverManager.getConnection("jdbc:" + engine + ":" + database);
            } else {
                Properties properties = new Properties();
                properties.put("user", connectProperties.getUser());
                properties.put("password", connectProperties.getPassword());
                this.connection = DriverManager.getConnection("jdbc:" + engine + "://" + host + ":" + port + "/" + database + "?autoReconnect=true", properties);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getEngine() {
        return this.connectProperties.getEngine();
    }

    public PreparedStatement prepare(String sql) {
        return prepare(sql, false);
    }

    public PreparedStatement prepare(String sql, boolean returnGeneratedKeys) {
        if (!isConnected()) return null;

        try {
            PreparedStatement statement;
            if (returnGeneratedKeys)
                statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            else
                statement = connection.prepareStatement(sql);

            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Return true if success, false is exception was thrown
    public boolean update(String sql) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public abstract void createTables();
}
