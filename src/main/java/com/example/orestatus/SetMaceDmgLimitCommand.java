package com.example.orestatus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;    

public class SetMaceDmgLimitCommand implements CommandExecutor{
    private final OreStatusPlugin plugin;

public SetMaceDmgLimitCommand(OreStatusPlugin plugin) {
    this.plugin = plugin;
    }

@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!sender.isOp()) {
        sender.sendMessage(ChatColor.RED + "No exploiting!");
        return true;
    }

     if (args.length != 1) {
        sender.sendMessage(ChatColor.RED + "bro, ITS ONE ARGUMENT");
    }

     try {
        Double dmgCap = Double.parseDouble(args[0]);
        if (dmgCap < 0) {
            sender.sendMessage(ChatColor.RED + "The limit can't be negative");
            return true;
        }

         plugin.setMaceDamageCap(dmgCap);
        sender.sendMessage(ChatColor.MAGIC + "Mace dmg cap set to " + dmgCap);

         return true;
    } catch (NumberFormatException e) {
        sender.sendMessage(ChatColor.RED + "Dude, the limit has to be a double");
        return true;
    }
}
}