package com.plushnode.banana;

import com.plushnode.banana.commands.*;
import com.plushnode.banana.controllers.PunishmentController;
import com.plushnode.banana.listeners.PlayerListener;
import com.plushnode.banana.storage.BananaDatabase;
import com.plushnode.banana.util.PlayerCache;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BananaPlugin extends Plugin {
    private Configuration config = null;
    private PunishmentController controller;
    private PlayerCache cache;
    private List<String> mutedCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        reloadConfig();

        // todo: uuid cache
        /*this.cache = new PlayerCache();
        if (this.cache.connect()) {
            this.cache.createTables();
        } else {
            getLogger().severe("Could not connect to player cache database.");
        }*/

        BananaDatabase database = new BananaDatabase(this);
        if (database.connect()) {
            database.createTables();
            this.controller = new PunishmentController(database);
        } else {
            getLogger().severe("Could not connect to punishment database.");
        }

        this.getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        this.getProxy().getPluginManager().registerCommand(this, new BanCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new MuteCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new BananaCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new CheckCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new HistoryCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new UnmuteCommand(this));
    }

    public Configuration getConfig() {
        return config;
    }

    public PunishmentController getPunishmentController() {
        return controller;
    }

    public PlayerCache getCache() {
        return cache;
    }

    public List<String> getMutedCommands() {
        return mutedCommands;
    }

    public boolean reloadConfig() {
        getDataFolder().mkdirs();

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            this.mutedCommands = new ArrayList<>(this.config.getStringList("punishment.mute.commands"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}