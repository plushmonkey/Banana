package com.plushnode.banana.storage;

import com.plushnode.banana.models.Action;
import com.plushnode.banana.storage.sql.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Gets a ResultSet for a punishment that matches certain actions
// Can be either uuid or IP lookup. IP lookup will override uuid lookup.
public class PunishmentQuery {
    private String table;
    private UUID uuid;
    private String ipAddress;
    private List<Action> actions;
    private String server = "";
    private PreparedStatement statement = null;

    public PunishmentQuery(String table) {
        this.actions = new ArrayList<>();
        this.table = table;
    }

    public PunishmentQuery ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getTable() {
        return table;
    }

    public PunishmentQuery uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public PunishmentQuery server(String server) {
        this.server = server;
        return this;
    }

    public PunishmentQuery action(Action action) {
        this.actions.add(action);
        return this;
    }

    public ResultSet execute(Database database) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(table).append(" WHERE ");

        if (server.length() > 0)
            sb.append(" server=? AND ");

        if (ipAddress != null && ipAddress.length() > 0) {
            sb.append("ip=?");
        } else {
            sb.append("uuid=?");
        }

        for (int i = 0; i < actions.size(); ++i) {
            if (i == 0) {
                sb.append(" AND ");
                if (actions.size() > 1)
                    sb.append("(");
                sb.append("action=?");
            } else {
                sb.append(" OR action=?");
            }
        }

        if (actions.size() > 1) {
            sb.append(")");
        }

        sb.append(" ORDER BY id DESC");

        this.statement = database.prepare(sb.toString());
        int currentPos = 1;
        try {
            if (this.server.length() > 0)
                statement.setString(currentPos++, this.server);

            if (ipAddress != null && ipAddress.length() > 0)
                statement.setString(currentPos, ipAddress);
            else
                statement.setString(currentPos, uuid.toString());

            for (int i = 0; i < actions.size(); ++i)
                statement.setInt(currentPos + 1 + i, actions.get(i).ordinal());

            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void close() {
        if (this.statement != null) {
            try {
                this.statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
