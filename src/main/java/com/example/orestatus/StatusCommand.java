package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {

    private final OreStatusPlugin plugin;

    public StatusCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only operators can use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /setStatus <player|self> <iron|diamond|netherite|clear>");
            return true;
        }

        Player target;
        String statusStr;

        if (args.length == 1) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(ChatColor.RED + "Console must specify a player.");
                return true;
            }
            target = p;
            statusStr = args[0];
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            statusStr = args[1];
        }

        if (statusStr.equalsIgnoreCase("clear")) {
            plugin.getStatusManager().setStatus(target, null);
            sender.sendMessage(ChatColor.GREEN + "Cleared status for " + target.getName());
            
            // Check inventory for illegal items after status clear (no restrictions when cleared)
            if (target.isOnline()) {
                RestrictionListener restrictionListener = plugin.getRestrictionListener();
                if (restrictionListener != null) {
                    restrictionListener.checkInventory(target);
                }
            }
            
            return true;
        }

        PlayerStatus status = PlayerStatus.fromString(statusStr);
        if (status == null) {
            sender.sendMessage(ChatColor.RED + "Invalid status: iron, diamond, netherite");
            return true;
        }

        plugin.getStatusManager().setStatus(target, status);
        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + " to " + status.name());
        
        // Check inventory for illegal items after status change
        if (target.isOnline()) {
            RestrictionListener restrictionListener = plugin.getRestrictionListener();
            if (restrictionListener != null) {
                restrictionListener.checkInventory(target);
            }
        }
        
        return true;
    }
}
