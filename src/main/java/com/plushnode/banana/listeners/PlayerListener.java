package com.plushnode.banana.listeners;

import com.plushnode.banana.controllers.PunishmentController;
import com.plushnode.banana.models.Action;
import com.plushnode.banana.BananaPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerListener implements Listener {
    private BananaPlugin plugin;

    public PlayerListener(BananaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent ev) {
        String partialPlayerName = ev.getCursor().toLowerCase();
        int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
        if (lastSpaceIndex >= 0) {
            partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
        }
        for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
                ev.getSuggestions().add(p.getName());
            }
        }
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        String ipAddress = e.getConnection().getAddress().getAddress().getHostAddress();

        PunishmentController controller = plugin.getPunishmentController();

        Optional<String> playerReason = controller.isPunished(uuid, Action.BAN);
        Optional<String> ipReason = controller.isPunished(ipAddress, Action.BAN);
        String reason = "";

        if (playerReason.isPresent())
            reason = "Player banned: " + playerReason.get();
        if (ipReason.isPresent())
            reason = "IP banned: " + ipReason.get();

        if (reason.length() > 0) {
            e.setCancelled(true);
            e.setCancelReason(reason);
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        String message = e.getMessage();

        ProxiedPlayer sender = (ProxiedPlayer)e.getSender();
        String ipAddress = e.getSender().getAddress().getAddress().getHostAddress();
        UUID uuid = sender.getUniqueId();

        PunishmentController controller = plugin.getPunishmentController();

        Optional<String> playerReason = controller.isPunished(uuid, Action.MUTE);
        Optional<String> ipReason = controller.isPunished(ipAddress, Action.MUTE);
        String reason = "";

        if (playerReason.isPresent())
            reason = playerReason.get();
        if (ipReason.isPresent())
            reason = ipReason.get();

        if (reason.length() == 0) {
            reason = "No reason specified";
        }
        if (playerReason.isPresent() || ipReason.isPresent()) {
            if (message.startsWith("/")) {
                List<String> commands = plugin.getMutedCommands();
                boolean usingMutedCommand = false;
                for (String command : commands) {
                    if (message.startsWith(command + " ")) {
                        usingMutedCommand = true;
                        break;
                    }
                }

                if (!usingMutedCommand)
                    return;
            }

            sender.sendMessage(ChatMessageType.CHAT, new ComponentBuilder("Muted: ").color(ChatColor.RED).bold(true).append(reason).color(ChatColor.GOLD).bold(false).create());
            e.setCancelled(true);
        }
    }
}
