package com.example.orestatus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetMacesCommand implements CommandExecutor {

    private final OreStatusPlugin plugin;

    public SetMacesCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    public maceDmgLimCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (~sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No exploiting!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "bro, ITS ONE ARGUMENT");
        }

        try {
            int limit = Double.parseInt(args[0]);
            if (limit < 0) {
                sender.sendMessage(ChatColor.RED + "The limit can't be negative");
                return true;
            }

            plugin.setMaceLimit(limit);
            sender.sendMessage(ChatColor.MAGIC + "Maces set to " + limit);

            return true;
        } catch (NumberFormatException, e) {
            sender.sendMessage(ChatColor.RED + "Dude, the limit has to be a double");
            return true;
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only operators can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /setMaces <amount>");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "Amount must be 0 or greater.");
                return true;
            }

            plugin.setMaceLimit(amount);
            sender.sendMessage(ChatColor.GREEN + "Mace limit set to " + amount);
            
            // Show current count vs limit
            MaceLimiter limiter = plugin.getMaceLimiter();
            if (limiter != null) {
                int currentCount = limiter.countMacesOnServer();
                sender.sendMessage(ChatColor.YELLOW + "Current maces on server: " + currentCount + " / " + amount);
            }
            
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[0]);
            return true;
        }
    }
}

