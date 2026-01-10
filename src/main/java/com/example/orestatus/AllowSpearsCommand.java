package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllowSpearsCommand implements CommandExecutor {

    private final OreStatusPlugin plugin;

    public AllowSpearsCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only operators can use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /allowSpears <player> <true|false>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        String boolStr = args[1].toLowerCase();
        boolean allowed;
        if (boolStr.equals("true")) {
            allowed = true;
        } else if (boolStr.equals("false")) {
            allowed = false;
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid value. Use 'true' or 'false'.");
            return true;
        }

        plugin.getSpearPermissionManager().setSpearAllowed(target, allowed);
        
        if (allowed) {
            sender.sendMessage(ChatColor.GREEN + "Enabled all spear features for " + target.getName());
            target.sendMessage(ChatColor.GREEN + "All spear features are now enabled for you!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Disabled spear features (except lunge) for " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "Spear features (except lunge) have been disabled for you.");
        }
        
        return true;
    }
}

