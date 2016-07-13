package com.plushnode.banana.controllers;

import com.plushnode.banana.models.Action;
import com.plushnode.banana.models.Punishment;
import com.plushnode.banana.models.PunishmentTargetType;
import com.plushnode.banana.storage.PunishmentQuery;
import com.plushnode.banana.storage.sql.Database;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PunishmentController {
    private Database database;

    public PunishmentController(Database database) {
        this.database = database;
    }

    public Punishment punish(UUID uuid, Action action, long length, String executor, String reason) {
        Punishment punishment = new Punishment(PunishmentTargetType.PLAYER, uuid.toString(), action,
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + length), executor, reason, Punishment.GLOBAL_SERVER);
        punishment.setTable("PlayerPunishment");
        punishment.setDatabase(database);
        return punishment;
    }

    public Punishment punish(String ipAddress, Action action, long length, String executor, String reason) {
        Punishment punishment = new Punishment(PunishmentTargetType.IPADDRESS, ipAddress, action,
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + length), executor, reason, Punishment.GLOBAL_SERVER);
        punishment.setTable("IPPunishment");
        punishment.setDatabase(database);
        return punishment;
    }

    public Punishment punish(UUID uuid, Action action, long length, String executor, String reason, String server) {
        Punishment punishment = new Punishment(PunishmentTargetType.PLAYER, uuid.toString(), action,
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + length), executor, reason, server);
        punishment.setTable("PlayerPunishment");
        punishment.setDatabase(database);
        return punishment;
    }

    public Punishment punish(String ipAddress, Action action, long length, String executor, String reason, String server) {
        Punishment punishment = new Punishment(PunishmentTargetType.IPADDRESS, ipAddress, action,
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + length), executor, reason, server);
        punishment.setTable("IPPunishment");
        punishment.setDatabase(database);
        return punishment;
    }

    public Optional<String> isPunished(UUID uuid, Action action) {
        PunishmentQuery query = new PunishmentQuery("PlayerPunishment")
                .action(action)
                .uuid(uuid);

        return isPunished(query);
    }

    public Optional<String> isPunished(String ipAddress, Action action) {
        PunishmentQuery query = new PunishmentQuery("IPPunishment")
                .action(action)
                .ipAddress(ipAddress);

        return isPunished(query);
    }

    public Optional<String> isPunished(PunishmentQuery query) {
        List<Punishment> punishments = getPunishments(query);

        for (Punishment punishment : punishments) {
            if (Punishment.isActive(punishment))
                return Optional.of(punishment.getReason());
        }

        return Optional.empty();
    }

    public List<Punishment> getPunishments(PunishmentQuery query) {
        List<Punishment> punishments = new ArrayList<>();
        ResultSet result = query.execute(database);
        PunishmentTargetType targetType = (query.getIpAddress() != null && query.getIpAddress().length() > 0) ? PunishmentTargetType.IPADDRESS : PunishmentTargetType.PLAYER;

        try {
            while (result.next()) {
                int id = result.getInt(1);
                String target = result.getString(2);
                Action action = Action.values()[result.getInt("action")];
                Timestamp timestamp = result.getTimestamp("timestamp");
                Timestamp expiration = result.getTimestamp("expiration");
                String executor = result.getString("executor");
                String reason = result.getString("reason");
                String server = result.getString("server");
                String removedBy = result.getString("removedby");
                Timestamp removedAt = result.getTimestamp("removedat");

                Punishment punishment = new Punishment(targetType, target, action, timestamp, expiration, executor, reason, server);

                if (removedBy != null && !removedBy.isEmpty())
                    punishment.remove(removedBy, removedAt);
                punishment.setDatabase(database);
                punishment.setId(id);
                punishment.setTable(query.getTable());

                punishments.add(punishment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                result.close();
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return punishments;
    }
}
