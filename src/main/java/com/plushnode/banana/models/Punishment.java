package com.plushnode.banana.models;

import com.plushnode.banana.storage.sql.DatabaseObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Punishment extends DatabaseObject {
    private PunishmentTargetType targetType;
    private String target;
    private Action action;
    private Timestamp timestamp;
    private Timestamp expiration;
    private String executor;
    private String reason;

    public Punishment(PunishmentTargetType targetType, String target, Action action, Timestamp timestamp, Timestamp expiration, String executor, String reason) {
        this.targetType = targetType;
        this.target = target;
        this.action = action;
        this.timestamp = timestamp;
        this.expiration = expiration;
        this.executor = executor;
        this.reason = reason;
    }

    public PunishmentTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(PunishmentTargetType targetType) {
        this.targetType = targetType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public void setExpiration(Timestamp expiration) {
        this.expiration = expiration;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void save() {
        if (this.getId() != 0)
            update();
        else
            insert();
    }

    private void update() {
        String sql;

        if (getTargetType() == PunishmentTargetType.IPADDRESS) {
            sql = "UPDATE " + getTable() + " SET ip=?, action=?, timestamp=?, expiration=?, executor=?, reason=? WHERE id=?";
        } else {
            sql = "UPDATE " + getTable() + " SET uuid=?, action=?, timestamp=?, expiration=?, executor=?, reason=? WHERE id=?";
        }

        PreparedStatement statement = getDatabase().prepare(sql);

        try {
            statement.setString(1, getTarget());
            statement.setInt(2, getAction().ordinal());
            statement.setTimestamp(3, getTimestamp());
            statement.setTimestamp(4, getExpiration());
            statement.setString(5, getExecutor());
            statement.setString(6, getReason());
            statement.setInt(7, getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void insert() {
        String sql = "INSERT INTO " + getTable() + " (uuid, action, timestamp, expiration, executor, reason) VALUES (?, ?, ?, ?, ?, ?)";

        if (getTargetType() == PunishmentTargetType.IPADDRESS)
            sql = "INSERT INTO " + getTable() + " (ip, action, timestamp, expiration, executor, reason) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = getDatabase().prepare(sql);

        try {
            statement.setString(1, getTarget());
            statement.setInt(2, getAction().ordinal());
            statement.setTimestamp(3, getTimestamp());
            statement.setTimestamp(4, getExpiration());
            statement.setString(5, getExecutor());
            statement.setString(6, getReason());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete() {
        if (getId() == 0) return;

        String sql = "DELETE FROM " + getTable() + " WHERE id=?";

        PreparedStatement statement = getDatabase().prepare(sql);
        try {
            statement.setInt(1, getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
