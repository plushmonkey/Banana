package com.plushnode.banana.util;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.storage.sql.ConnectProperties;
import com.plushnode.banana.storage.sql.Database;

import java.io.File;

// Store player UUIDs in sqlite database so the API doesn't need to be accessed while player is offline
public class PlayerCache extends Database {
    public PlayerCache(BananaPlugin plugin) {
        super(new ConnectProperties().setEngine("sqlite").setDatabase(new File(plugin.getDataFolder(), "uuidcache.db").getPath()));
    }

    @Override
    public void createTables() {
        update("CREATE TABLE IF NOT EXISTS CacheUUID(id INT AUTO_INCREMENT, name VARCHAR(20), uuid VARCHAR(36), PRIMARY KEY(id))");
        update("CREATE TABLE IF NOT EXISTS CacheIP(id INT AUTO_INCREMENT, uuid_id int, ip VARCHAR(46), PRIMARY KEY(id), FOREIGN KEY(uuid_id) REFERENCES CacheUUID(id) ON DELETE CASCADE)");
    }
}
