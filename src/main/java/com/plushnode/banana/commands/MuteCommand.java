package com.plushnode.banana.commands;

import com.plushnode.banana.BananaPlugin;
import com.plushnode.banana.models.Action;
import com.plushnode.banana.models.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class MuteCommand extends Command {
    private BananaPlugin plugin;

    public MuteCommand(BananaPlugin plugin) {
        super("mute");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) return;

        ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
        if (target == null) return;
        String reason = "";
        if (args.length > 1)
            reason = args[1];

        UUID uuid = target.getUniqueId();
        String ipAddress = target.getAddress().getAddress().getHostAddress();
        Punishment punishment = plugin.getPunishmentController().punish(uuid, Action.MUTE, 15000, sender.getName(), reason);
        punishment.save();
    }
}
