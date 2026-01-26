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

        // Special rule: Iron kills Netherite => swap
        if (killerStatus == PlayerStatus.IRON && victimStatus == PlayerStatus.NETHERITE) {
            plugin.getStatusManager().setStatus(killer, PlayerStatus.NETHERITE);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.IRON);

            if (plugin.getRestrictionListener() != null) {
                plugin.getRestrictionListener().checkInventory(killer);
                plugin.getRestrictionListener().checkInventory(victim);
            }

            return;
        }

        // Normal rule: only if killer is LOWER tier than victim
        if (killerStatus.ordinal() < victimStatus.ordinal()) {

            // Downgrade victim by 1 tier
            int newVictim = Math.max(victimStatus.ordinal() - 1, 0);
            plugin.getStatusManager().setStatus(victim, PlayerStatus.values()[newVictim]);

            // Upgrade killer by 1 tier
            int max = PlayerStatus.values().length - 1;
            int newKiller = Math.min(killerStatus.ordinal() + 1, max);
            plugin.getStatusManager().setStatus(killer, PlayerStatus.values()[newKiller]);

            if (plugin.getRestrictionListener() != null) {
                plugin.getRestrictionListener().checkInventory(victim);
                plugin.getRestrictionListener().checkInventory(killer);
            }
        }

        // Custom death messages
        if (!killer.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            int random = (int) (Math.random() * 10) + 1;
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
                case 1 -> Bukkit.broadcastMessage(message);
                case 2 -> Bukkit.broadcastMessage(message2);
                case 3 -> Bukkit.broadcastMessage(message3);
                case 4 -> Bukkit.broadcastMessage(message4);
                case 5 -> Bukkit.broadcastMessage(message5);
                case 6 -> Bukkit.broadcastMessage(message6);
                case 7 -> Bukkit.broadcastMessage(message7);
                case 8 -> Bukkit.broadcastMessage(message8);
                case 9 -> Bukkit.broadcastMessage(message9);
                case 10 -> Bukkit.broadcastMessage(message10);
            }
        } else {
            Bukkit.broadcast(Component.text(
                    "Please use '/gamerule showDeathMessages false' to enable custom death messages",
                    NamedTextColor.YELLOW
            ));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNonPvPDeath(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (event.getEntity().getKiller() instanceof Player) return;

        if (!victim.getWorld().getGameRuleValue(org.bukkit.GameRule.SHOW_DEATH_MESSAGES)) {
            Bukkit.broadcast(Component.text(
                    victim.getName() + " has a skill issue!",
                    NamedTextColor.LIGHT_PURPLE
            ));
        } else {
            Bukkit.broadcast(Component.text(
                    "Please use '/gamerule showDeathMessages false' to enable custom death messages",
                    NamedTextColor.YELLOW
            ));
        }
    }
}
