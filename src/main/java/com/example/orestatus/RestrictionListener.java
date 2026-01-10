package com.example.orestatus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RestrictionListener implements Listener {

    private final OreStatusPlugin plugin;
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private static final long MESSAGE_COOLDOWN = 10 * 60 * 1000; 

    public RestrictionListener(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }
    
    //console spam cooldown

    private boolean canSendMessage(Player p) {
        UUID playerUUID = p.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        if (!lastMessageTime.containsKey(playerUUID)) {
            lastMessageTime.put(playerUUID, currentTime);
            return true;
        }
        
        long lastTime = lastMessageTime.get(playerUUID);
        if (currentTime - lastTime >= MESSAGE_COOLDOWN) {
            lastMessageTime.put(playerUUID, currentTime);
            return true;
        }
        
        return false;
    }
    
    /**
     * Logs a message only if cooldown allows it
     */
    private void logMessage(Player p, String msg) {
        if (!canSendMessage(p)) {
            return; // Still on cooldown, don't log
        }
        
        plugin.getLogger().warning(msg);
        try (PrintWriter out = new PrintWriter(new FileWriter(plugin.getDataFolder() + "/statusDumpStack.txt", true))) {
            out.println(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        checkItem(e.getPlayer(), e.getItem());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Check inventory when player joins
        checkInventory(e.getPlayer());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        ItemStack item = e.getItem().getItemStack();
        if (isItemIllegal(p, item)) {
            e.setCancelled(true);
            String msg = p.getName() + " tried to pick up " + item.getType().name() + " above their tier!";
            logMessage(p, msg);
        }
    }

    private void checkItem(Player p, ItemStack item) {
        if (item == null) return;
        if (isItemIllegal(p, item)) {
            Material mat = item.getType();
            String msg = p.getName() + " tried to use " + mat.name() + " above their tier!";
            logMessage(p, msg);
        }
    }

    private boolean isItemIllegal(Player p, ItemStack item) {
        if (item == null) return false;
        Material mat = item.getType();
        if (!(mat.toString().contains("SWORD") || mat.toString().contains("AXE") || mat.toString().contains("HELMET")
                || mat.toString().contains("CHESTPLATE") || mat.toString().contains("LEGGINGS") || mat.toString().contains("BOOTS"))) return false;

        PlayerStatus status = plugin.getStatusManager().getStatus(p);
        if (status == null) return false;

        return switch (status) {
            case IRON -> (mat.toString().contains("DIAMOND") && !mat.toString().contains("SWORD") && !mat.toString().contains("AXE")) || mat.toString().contains("NETHERITE");
            case DIAMOND -> mat.toString().contains("NETHERITE");
            default -> false;
        };
    }

    /**
     * Checks the player's entire inventory for illegal items and removes them completely
     */
    public void checkInventory(Player p) {
        PlayerInventory inv = p.getInventory();
        boolean foundIllegal = false;
        
        // Check main inventory (0-35)
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && isItemIllegal(p, item)) {
                Material itemType = item.getType();
                // Completely remove the item - clear the slot
                inv.clear(i);
                foundIllegal = true;
                String msg = p.getName() + " had illegal item " + itemType.name() + " removed from inventory!";
                logMessage(p, msg);
            }
        }
        
        // Check armor slots (helmet, chestplate, leggings, boots)
        ItemStack[] armor = inv.getArmorContents();
        boolean armorChanged = false;
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && isItemIllegal(p, armor[i])) {
                Material armorType = armor[i].getType();
                // Completely remove the armor piece
                armor[i] = new ItemStack(Material.AIR);
                armorChanged = true;
                foundIllegal = true;
                String msg = p.getName() + " had illegal armor " + armorType.name() + " removed!";
                logMessage(p, msg);
            }
        }
        if (armorChanged) {
            inv.setArmorContents(armor);
        }
        
        // Check offhand
        ItemStack offhand = inv.getItemInOffHand();
        if (offhand != null && isItemIllegal(p, offhand)) {
            Material offhandType = offhand.getType();
            // Completely remove from offhand
            inv.setItemInOffHand(new ItemStack(Material.AIR));
            foundIllegal = true;
            String msg = p.getName() + " had illegal item " + offhandType.name() + " removed from offhand!";
            logMessage(p, msg);
        }
        
        // Also check the item in hand (main hand)
        ItemStack handItem = inv.getItemInMainHand();
        if (handItem != null && isItemIllegal(p, handItem)) {
            Material handType = handItem.getType();
            // Completely remove from main hand
            inv.setItemInMainHand(new ItemStack(Material.AIR));
            foundIllegal = true;
            String msg = p.getName() + " had illegal item " + handType.name() + " removed from hand!";
            logMessage(p, msg);
        }
        
        if (foundIllegal) {
            // Force inventory update to ensure items are completely gone
            p.updateInventory();
            // Schedule another update next tick to ensure removal is persistent
            plugin.getServer().getScheduler().runTask(plugin, () -> p.updateInventory());
        }
    }
}
