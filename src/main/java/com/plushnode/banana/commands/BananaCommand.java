package com.plushnode.banana.commands;

import com.plushnode.banana.BananaPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class BananaCommand extends Command {
    private BananaPlugin plugin;

    public BananaCommand(BananaPlugin plugin) {
        super("banana");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ComponentBuilder builder = new ComponentBuilder("Banana").color(ChatColor.YELLOW).append(" commands:\n").color(ChatColor.GOLD);
            builder.append("/banana").color(ChatColor.YELLOW).append(" reload").color(ChatColor.GOLD);
            sender.sendMessage(builder.create());
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            boolean result = plugin.reloadConfig();

            ComponentBuilder builder = new ComponentBuilder("Banana").color(ChatColor.YELLOW).append(" reload result: ").color(ChatColor.GOLD);

            if (result)
                builder.append("success").color(ChatColor.GREEN);
            else
                builder.append("fail").color(ChatColor.RED).bold(true);

            sender.sendMessage(builder.create());
        }
    }
}
