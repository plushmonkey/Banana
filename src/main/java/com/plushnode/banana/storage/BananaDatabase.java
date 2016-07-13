package com.plushnode.banana.storage;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.storage.sql.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BananaDatabase extends Database {
    public BananaDatabase(BananaPlugin plugin) {
        super(BananaPropertiesLoader.load(plugin));
    }

    @Override
    public void createTables() {
        String commonColumns = "action TINYINT, timestamp TIMESTAMP, expiration TIMESTAMP, executor VARCHAR(20), reason VARCHAR(255)";
        String autoInc = "AUTO_INCREMENT";
        if (getEngine().equalsIgnoreCase("sqlite"))
            autoInc = "";

        // todo: Should I just store active and history in same table?

        // Active punishments table
        update("CREATE TABLE IF NOT EXISTS PlayerPunishment(id INTEGER " + autoInc + " PRIMARY KEY, uuid VARCHAR(36), " + commonColumns + ")");
        update("CREATE TABLE IF NOT EXISTS IPPunishment(id INTEGER " + autoInc + " PRIMARY KEY, ip VARCHAR(46), " + commonColumns + ")");

        // History table
        update("CREATE TABLE IF NOT EXISTS PlayerHistory(id INTEGER " + autoInc + " PRIMARY KEY, uuid VARCHAR(36), " + commonColumns + ")");
        update("CREATE TABLE IF NOT EXISTS IPHistory(id INTEGER " + autoInc + " PRIMARY KEY, ip VARCHAR(46), " + commonColumns + ")");

        if (getEngine().equalsIgnoreCase("sqlite")) {
            update("CREATE INDEX IF NOT EXISTS uuid ON PlayerPunishment(uuid)");
            update("CREATE INDEX IF NOT EXISTS ip ON IPPunishment(ip)");

            update("CREATE INDEX IF NOT EXISTS uuid ON PlayerHistory(uuid)");
            update("CREATE INDEX IF NOT EXISTS ip ON IPHistory(ip)");
        } else if (getEngine().equalsIgnoreCase("mysql")){
            PreparedStatement statement = this.prepare("SELECT COUNT(1) IndexIsThere FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name='PlayerPunishment' AND index_name='uuid'");
            try {
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    int hasIndex = result.getInt(1);
                    if (hasIndex == 0) {
                        update("CREATE INDEX uuid ON PlayerPunishment(uuid)");
                        update("CREATE INDEX ip ON IPPunishment(ip)");

                        update("CREATE INDEX uuid ON PlayerHistory(uuid)");
                        update("CREATE INDEX ip ON IPHistory(ip)");
                    }
                }
            } catch (SQLException e) {

            }
        }
    }
}
