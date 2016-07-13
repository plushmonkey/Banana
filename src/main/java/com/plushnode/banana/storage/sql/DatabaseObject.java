package com.plushnode.banana.storage.sql;

public abstract class DatabaseObject {
    private Database database;
    private int id;
    private String table;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public abstract void save();
    public abstract void delete();

}
