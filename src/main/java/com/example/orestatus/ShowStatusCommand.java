package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ShowStatusCommand implements CommandExecutor {

    private final OreStatusPlugin plugin;

    public ShowStatusCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(ChatColor.RED + "Console must specify a player.");
                return true;
            }
            target = p;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
        }

        PlayerStatus status = plugin.getStatusManager().getStatus(target);
        if (status == null) {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " has no tier assigned.");
        } else {
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has tier: " + status.name());
        }
        return true;
    }
}
