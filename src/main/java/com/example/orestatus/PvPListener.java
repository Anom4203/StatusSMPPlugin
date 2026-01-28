package com.example.orestatus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        if (!(victim.getKiller() instanceof Player killer)) return;

        PlayerStatus victimStatus = plugin.getStatusManager().getStatus(victim);
        PlayerStatus killerStatus = plugin.getStatusManager().getStatus(killer);

        // Safety
        if (victimStatus == null || killerStatus == null) return;
        //i should crash the server if that happens, lol
        // SPECIAL CASE: Iron kills Netherite → switch
        if (killerStatus == PlayerStatus.IRON && victimStatus == PlayerStatus.NETHERITE) {
            plugin.getStatusManager().setStatus(killer, PlayerStatus.NETHERITE);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.DIAMOND);
        }
        // NORMAL CASE: Killer is lower → swap 1 tier
        else if (killerStatus.ordinal() < victimStatus.ordinal()) {

            int newKiller = killerStatus.ordinal() + 1;
            int newVictim = victimStatus.ordinal() - 1;

            plugin.getStatusManager().setStatus(killer, PlayerStatus.values()[newKiller]);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.values()[newVictim]);
        }

        // Inventory checks
        if (plugin.getRestrictionListener() != null) {
            plugin.getRestrictionListener().checkInventory(killer);
            plugin.getRestrictionListener().checkInventory(victim);
        }

        // Custom death messages
        if (!killer.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            int random = (int)(Math.random() * 10);
            String v = victim.getName();
            String k = killer.getName();

            String[] messages = {
                    ChatColor.MAGIC + v + " was ratioed by " + k + "!",
                    ChatColor.DARK_AQUA + v + " was penetrated by " + k + "!",
                    ChatColor.DARK_GREEN + v + " was cooked up by " + k + "!",
                    ChatColor.RED + v + " was stabbed by " + k + "!",
                    ChatColor.BLACK + v + " was doomed to death by " + k + "!",
                    ChatColor.AQUA + v + " was deleted by " + k + "!",
                    ChatColor.LIGHT_PURPLE + v + " was emancipated by " + k + "!",
                    ChatColor.DARK_RED + v + " was sent to limbo by " + k + "!",
                    ChatColor.DARK_PURPLE + v + " was given an invitation to hell by " + k + "!",
                    ChatColor.GREEN + v + " was eaten by " + k + "!"
            };

            plugin.getServer().broadcastMessage(messages[random]);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNonPvPDeath(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (victim.getKiller() instanceof Player) return;

        if (!victim.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            Bukkit.broadcast(Component.text(
                    victim.getName() + " has a skill issue!",
                    NamedTextColor.LIGHT_PURPLE
            ));
        }
    }
}