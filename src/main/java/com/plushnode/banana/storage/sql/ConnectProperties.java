package com.plushnode.banana.storage.sql;

public class ConnectProperties {
    private String engine;
    private String database;
    private String host;
    private int port;
    private String user;
    private String password;

    public String getEngine() {
        return engine;
    }

    public ConnectProperties setEngine(String engine) {
        this.engine = engine;
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public ConnectProperties setDatabase(String database) {
        this.database = database;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ConnectProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ConnectProperties setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ConnectProperties setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ConnectProperties setPassword(String password) {
        this.password = password;
        return this;
    }
}
