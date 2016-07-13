package com.plushnode.banana.storage;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.storage.sql.ConnectProperties;
import net.md_5.bungee.config.Configuration;

import java.io.File;

public class BananaPropertiesLoader {
    public static ConnectProperties load(BananaPlugin plugin) throws IllegalArgumentException {
        Configuration config = plugin.getConfig();
        ConnectProperties properties = new ConnectProperties();

        String engine = config.getString("storage.engine");

        properties.setEngine(engine);

        if (engine.equalsIgnoreCase("sqlite")) {
            String store = config.getString("storage.sqlite.store");
            File storeFile = new File(plugin.getDataFolder(), store);

            properties.setDatabase(storeFile.getPath());
        } else if (engine.equalsIgnoreCase("mysql")) {
            properties.setHost(config.getString("storage.mysql.host"));
            properties.setPort(config.getInt("storage.mysql.port"));
            properties.setUser(config.getString("storage.mysql.username"));
            properties.setPassword(config.getString("storage.mysql.password"));
            properties.setDatabase(config.getString("storage.mysql.database"));
        } else {
            throw new IllegalArgumentException("Bad storage engine specified.");
        }

        return properties;
    }
}
