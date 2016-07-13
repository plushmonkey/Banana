package com.plushnode.banana.commands;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.controllers.PunishmentController;
import com.plushnode.banana.models.Action;
import com.plushnode.banana.models.Punishment;
import com.plushnode.banana.storage.PunishmentQuery;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;

// Displays active punishments for a player/ip
public class CheckCommand extends Command {
    private BananaPlugin plugin;

    public CheckCommand(BananaPlugin plugin) {
        super("check");
        this.plugin = plugin;
    }

    // This probably needs pagination since it will display warnings as well.
    private void sendPunishments(CommandSender sender, List<Punishment> punishments, ComponentBuilder builder) {
        for (Punishment punishment : punishments) {
            builder.append("\n");
            String reason = punishment.getReason();
            if (reason.length() == 0)
                reason = "No reason specified";
            builder.append(punishment.getAction().toString() + ": ").color(ChatColor.RED).bold(true)
                    .append(reason).color(ChatColor.GOLD).bold(false)
                    .append(" - ").color(ChatColor.DARK_GRAY)
                    .append(punishment.getExecutor()).color(ChatColor.DARK_AQUA);
        }

        sender.sendMessage(builder.create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) return;

        // todo: uuid cache lookup
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
        if (target == null) return;
        String ipAddress = target.getAddress().getAddress().getHostAddress();
        UUID uuid = target.getUniqueId();

        PunishmentController controller = plugin.getPunishmentController();

        PunishmentQuery playerQuery = new PunishmentQuery("PlayerPunishment")
                .uuid(uuid)
                .action(Action.BAN)
                .action(Action.MUTE)
                .action(Action.WARN);
        PunishmentQuery ipQuery = new PunishmentQuery("IPPunishment")
                .ipAddress(ipAddress)
                .action(Action.BAN)
                .action(Action.MUTE)
                .action(Action.WARN);

        List<Punishment> playerPunishments = controller.getPunishments(playerQuery);
        List<Punishment> ipPunishments = controller.getPunishments(ipQuery);

        if (playerPunishments.size() > 0) {
            ComponentBuilder builder = new ComponentBuilder("Player punishments:").color(ChatColor.YELLOW);

            sendPunishments(sender, playerPunishments, builder);
        }

        if (ipPunishments.size() > 0) {
            ComponentBuilder builder = new ComponentBuilder("IP punishments:").color(ChatColor.YELLOW);

            sendPunishments(sender, ipPunishments, builder);
        }

        if (playerPunishments.size() == 0 && ipPunishments.size() == 0) {
            sender.sendMessage(new ComponentBuilder("No active punishments for ").color(ChatColor.GOLD).append(target.getName()).color(ChatColor.DARK_AQUA).create());
        }
    }
}
