package com.plushnode.banana.commands;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.controllers.PunishmentController;
import com.plushnode.banana.models.Action;
import com.plushnode.banana.models.Punishment;
import com.plushnode.banana.storage.PunishmentQuery;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnmuteCommand extends Command {
    private BananaPlugin plugin;

    public UnmuteCommand(BananaPlugin plugin) {
        super("unmute");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) return;

        ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
        if (target == null) return;

        UUID uuid = target.getUniqueId();
        String ipAddress = target.getAddress().getAddress().getHostAddress();

        PunishmentController controller = plugin.getPunishmentController();
        PunishmentQuery playerQuery = new PunishmentQuery("PlayerPunishment")
                .uuid(uuid)
                .action(Action.MUTE);

        PunishmentQuery ipQuery = new PunishmentQuery("IPPunishment")
                .ipAddress(ipAddress)
                .action(Action.MUTE);

        List<Punishment> playerPunishments = controller.getPunishments(playerQuery);
        List<Punishment> ipPunishments = controller.getPunishments(ipQuery);
        List<Punishment> punishments = Stream.concat(playerPunishments.stream(), ipPunishments.stream()).filter(Punishment::isActive).collect(Collectors.toList());

        for (Punishment punishment : punishments) {
            punishment.remove(sender.getName());
            punishment.save();
        }
    }
}
