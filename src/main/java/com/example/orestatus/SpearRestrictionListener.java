package com.example.orestatus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpearRestrictionListener implements Listener {

    private final OreStatusPlugin plugin;
    // Track when players start holding right-click with a spear (timestamp in milliseconds)
    private final Map<UUID, Long> spearHoldStartTimes = new HashMap<>();
    private static final long DAMAGE_BLOCK_MS = 250; // 5 ticks = 250ms (at 20 TPS)

    public SpearRestrictionListener(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if an item is a spear
     */
    private boolean isSpear(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material mat = item.getType();
        String matName = mat.name();
        // Check if material name contains SPEAR (covers all spear variants)
        return matName.contains("SPEAR");
    }

    /**
     * Checks if a player has a spear in their hand
     */
    private boolean isHoldingSpear(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        return isSpear(mainHand) || isSpear(offHand);
    }

    /**
     * Track right-click interactions with spears
     * When player right-clicks with a spear, mark the timestamp
     * This allows lunge mobility but blocks damage within 5 ticks
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (isSpear(item)) {
            // Track when player starts holding right-click with spear
            long currentTime = System.currentTimeMillis();
            spearHoldStartTimes.put(player.getUniqueId(), currentTime);
            
            // Clean up after 5 ticks have passed
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    Long startTime = spearHoldStartTimes.get(uuid);
                    if (startTime != null && startTime == currentTime) {
                        spearHoldStartTimes.remove(uuid);
                    }
                }
            }.runTaskLater(plugin, 6); // 6 ticks to ensure cleanup after 5 ticks
        }
    }

    /**
     * Prevents damage with spears if player has held a spear within 5 ticks
     * This blocks the damage from holding right-click while keeping lunge mobility
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;
        
        // Check if player is holding a spear
        if (!isHoldingSpear(attacker)) return;
        
        UUID playerUUID = attacker.getUniqueId();
        Long holdStartTime = spearHoldStartTimes.get(playerUUID);
        
        if (holdStartTime != null) {
            // Check if damage is within 5 ticks (250ms) of holding spear
            long timeSinceHold = System.currentTimeMillis() - holdStartTime;
            
            if (timeSinceHold <= DAMAGE_BLOCK_MS) {
                // Block damage - player is holding right-click with spear
                // This prevents damage from holding right-click while allowing lunge mobility
                e.setCancelled(true);
                return;
            }
        }
        
        // Check if attacker has spear features enabled
        if (!plugin.getSpearPermissionManager().canUseSpearFeatures(attacker)) {
            // Block regular melee attacks with spear
            e.setCancelled(true);
            attacker.sendMessage(ChatColor.RED + "You can only use lunge with spears!");
        }
    }
}

