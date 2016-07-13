package com.plushnode.banana.storage;

import com.plushnode.banana.models.Action;
import com.plushnode.banana.storage.sql.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentQuery {
    private String table;
    private UUID uuid;
    private String ipAddress;
    private List<Action> actions;
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

    public PunishmentQuery action(Action action) {
        this.actions.add(action);
        return this;
    }

    public ResultSet execute(Database database) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(table).append(" WHERE ");

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

        this.statement = database.prepare(sb.toString());
        try {
            if (ipAddress != null && ipAddress.length() > 0)
                statement.setString(1, ipAddress);
            else
                statement.setString(1, uuid.toString());

            for (int i = 0; i < actions.size(); ++i)
                statement.setInt(2 + i, actions.get(i).ordinal());

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
