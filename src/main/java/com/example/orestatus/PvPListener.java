package com.example.orestatus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPListener implements Listener {
    private final OreStatusPlugin plugin;

    public PvPListener(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        
        // Only proceed if this is a PvP death (killed by another player)
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        PlayerStatus victimStatus = plugin.getStatusManager().getStatus(victim);
        PlayerStatus killerStatus = plugin.getStatusManager().getStatus(killer);
        
        // Iron kills Netherite => swap
        if (killerStatus == PlayerStatus.IRON && victimStatus == PlayerStatus.NETHERITE) {
            plugin.getStatusManager().setStatus(killer, PlayerStatus.NETHERITE);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.IRON);
            // Check inventories after status change
            if (killer.isOnline() && plugin.getRestrictionListener() != null) {
                plugin.getRestrictionListener().checkInventory(killer);
            }
            if (victim.isOnline() && plugin.getRestrictionListener() != null) {
                plugin.getRestrictionListener().checkInventory(victim);
            }
            return;
        }

        // Only downgrade if the killer is LOWER tier than the victim
        if (killerStatus.ordinal() < victimStatus.ordinal()) {
            int newVictim = Math.max(victimStatus.ordinal() - 1, 0);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.values()[newVictim]);
            // Check inventory after status change
            if (victim.isOnline() && plugin.getRestrictionListener() != null) {
                plugin.getRestrictionListener().checkInventory(victim);
            }
        }

        // Upgrade killer normally (unless already netherite)
        int max = PlayerStatus.values().length - 1;
        int newKiller = Math.min(killerStatus.ordinal() + 1, max);
        plugin.getStatusManager().setStatus(killer, PlayerStatus.values()[newKiller]);
        // Check inventory after status change
        if (killer.isOnline() && plugin.getRestrictionListener() != null) {
            plugin.getRestrictionListener().checkInventory(killer);
        }

        // Send custom death message only for PvP deaths if death messages are disabled
        if (!killer.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            int random = (int)(Math.random() * 10) + 1; // Random number 1-10
            String victimName = victim.getName();
            String killerName = killer.getName();
            String message = ChatColor.MAGIC + victimName + " was ratioed by " + killerName + "!";
            String message2 = ChatColor.DARK_AQUA + victimName + " was penetrated by " + killerName + "!";
            String message3 = ChatColor.DARK_GREEN + victimName + " was cooked up by " + killerName + "!";
            String message4 = ChatColor.RED + victimName + " was stabbed by " + killerName + "!";
            String message5 = ChatColor.BLACK + victimName + " was doomed to death by " + killerName + "!";
            String message6 = ChatColor.AQUA + victimName + " was deleted by " + killerName + "!";
            String message7 = ChatColor.LIGHT_PURPLE + victimName + " was emancipated by " + killerName + "!";
            String message8 = ChatColor.DARK_RED + victimName + " was sent to limbo by " + killerName + "!";
            String message9 = ChatColor.DARK_PURPLE + victimName + " was givin an invitation to hell by " + killerName + "!";
            String message10 = ChatColor.GREEN + victimName + " was eaten by " + killerName + "!";

            switch (random) {
                case 1: 
                plugin.getServer().broadcastMessage(message);
                break;
                case 2:
                plugin.getServer().broadcastMessage(message2);
                break;
                case 3:
                plugin.getServer().broadcastMessage(message3);
                break;
                case 4:
                plugin.getServer().broadcastMessage(message4);
                break;
                case 5:
                plugin.getServer().broadcastMessage(message5);
                break;
                case 6:
                plugin.getServer().broadcastMessage(message6);
                break;
                case 7:
                plugin.getServer().broadcastMessage(message7);
                break;
                case 8:
                plugin.getServer().broadcastMessage(message8);
                break;
                case 9:
                plugin.getServer().broadcastMessage(message9);
                break;
                case 10:
                plugin.getServer().broadcastMessage(message10);
                break;
                default:
                plugin.getServer().broadcastMessage(message);
            }
            
        } 
        if (killer.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
        Bukkit.broadcast(Component.text("Please use '/gamerule showDeathMessages false' to enable custom death messages", NamedTextColor.YELLOW));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNonPvPDeath(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        
        // check for pvp death
        if (event.getEntity().getKiller() instanceof Player) return;
        
        // Send default death message for non PvP deaths if death messages are disabled
        if (!victim.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            String victimName = victim.getName();
            Bukkit.broadcast(Component.text(victimName + " has a skill issue!", NamedTextColor.LIGHT_PURPLE));
        }
        if (victim.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            Bukkit.broadcast(Component.text("Please use '/gamerule showDeathMessages false' to enable custom death messages", NamedTextColor.YELLOW));
        }
    }
}
