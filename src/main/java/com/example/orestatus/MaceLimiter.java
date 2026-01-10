package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class MaceLimiter implements Listener {

    private final OreStatusPlugin plugin;

    public MaceLimiter(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if an item is a mace
     */
    private boolean isMace(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material mat = item.getType();
        String matName = mat.name();
        // Check if material name contains MACE (covers all mace variants)
        return matName.contains("MACE");
    }

    /**
     * Checks if an item is a heavy core
     */
    private boolean isHeavyCore(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material mat = item.getType();
        String matName = mat.name();
        // Check if material name contains HEAVY_CORE (covers all heavy core variants)
        return matName.contains("HEAVY_CORE") || (matName.contains("HEAVY") && matName.contains("CORE"));
    }

    /**
     * Checks if heavy cores should be disabled (mace limit reached)
     */
    private boolean isHeavyCoreDisabled() {
        int limit = plugin.getMaceLimit();
        if (limit <= 0) return false; // Limit disabled, heavy cores allowed
        
        int currentCount = countMacesOnServer();
        // Disable heavy cores when mace count is at or above limit
        return currentCount >= limit;
    }

    /**
     * Counts all maces currently on the server
     * Includes maces in player inventories and dropped items
     */
    public int countMacesOnServer() {
        int count = 0;

        // Count maces in player inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            
            // Count main inventory (0-35)
            for (int i = 0; i < 36; i++) {
                ItemStack item = inv.getItem(i);
                if (isMace(item)) {
                    count += item.getAmount();
                }
            }
            
            // Count armor slots (might have maces in creative or glitched states)
            ItemStack[] armor = inv.getArmorContents();
            for (ItemStack item : armor) {
                if (isMace(item)) {
                    count += item.getAmount();
                }
            }
            
            // Count offhand
            ItemStack offhand = inv.getItemInOffHand();
            if (isMace(offhand)) {
                count += offhand.getAmount();
            }
            
            // Count main hand
            ItemStack mainHand = inv.getItemInMainHand();
            if (isMace(mainHand)) {
                count += mainHand.getAmount();
            }
        }

        // Count dropped maces in the world
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            for (Item item : items) {
                ItemStack itemStack = item.getItemStack();
                if (isMace(itemStack)) {
                    count += itemStack.getAmount();
                }
            }
        }

        return count;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftItem(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        
        ItemStack result = e.getRecipe().getResult();
        
        // Check for mace crafting
        if (isMace(result)) {
            int limit = plugin.getMaceLimit();
            if (limit <= 0) return; // Limit disabled

            int currentCount = countMacesOnServer();
            int resultAmount = result.getAmount();

            if (currentCount + resultAmount > limit) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot craft mace! Server mace limit (" + limit + ") would be exceeded.");
                player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
            }
            return;
        }
        
        // Check for heavy core crafting
        if (isHeavyCore(result)) {
            if (isHeavyCoreDisabled()) {
                int limit = plugin.getMaceLimit();
                int currentCount = countMacesOnServer();
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot craft heavy core! Server mace limit (" + limit + ") has been reached.");
                player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
                player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the mace limit is reached.");
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        
        // Only check crafting table and player inventory interactions
        if (e.getInventory().getType() != InventoryType.CRAFTING && 
            e.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        
        // Check for mace in crafting result
        if (isMace(clicked)) {
            int limit = plugin.getMaceLimit();
            if (limit <= 0) return; // Limit disabled

            int currentCount = countMacesOnServer();
            
            // If clicking a mace in crafting result slot, check if taking it would exceed limit
            if (e.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
                if (currentCount + clicked.getAmount() > limit) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Cannot take mace! Server mace limit (" + limit + ") would be exceeded.");
                    player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
                }
            }
            return;
        }
        
        // Check for heavy core in crafting result
        if (isHeavyCore(clicked)) {
            if (e.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
                if (isHeavyCoreDisabled()) {
                    int limit = plugin.getMaceLimit();
                    int currentCount = countMacesOnServer();
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Cannot take heavy core! Server mace limit (" + limit + ") has been reached.");
                    player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
                    player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the mace limit is reached.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickupMace(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        
        ItemStack item = e.getItem().getItemStack();
        
        // Check for mace pickup
        if (isMace(item)) {
            int limit = plugin.getMaceLimit();
            if (limit <= 0) return; // Limit disabled

            // Picking up a mace doesn't increase the total count - it just moves from world to inventory
            // So we should generally allow picking up. However, we still check to ensure
            // we don't exceed limit (defensive check in case of race conditions or edge cases)
            int currentCount = countMacesOnServer();
            
            // Only prevent if we would somehow exceed limit (shouldn't happen with crafting checks,
            // but provides safety in case of edge cases or commands that bypass crafting)
            if (currentCount > limit) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot pick up mace! Server mace limit (" + limit + ") has been exceeded.");
                player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
            }
            // Note: We allow pickup even if at limit because picking up doesn't increase total count
            return;
        }
        
        // Check for heavy core pickup
        if (isHeavyCore(item)) {
            if (isHeavyCoreDisabled()) {
                int limit = plugin.getMaceLimit();
                int currentCount = countMacesOnServer();
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot pick up heavy core! Server mace limit (" + limit + ") has been reached.");
                player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
                player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the mace limit is reached.");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        
        ItemStack item = e.getItem();
        if (!isHeavyCore(item)) return;
        
        // Prevent using heavy cores when mace limit is reached
        if (isHeavyCoreDisabled()) {
            e.setCancelled(true);
            int limit = plugin.getMaceLimit();
            int currentCount = countMacesOnServer();
            player.sendMessage(ChatColor.RED + "Cannot use heavy core! Server mace limit (" + limit + ") has been reached.");
            player.sendMessage(ChatColor.YELLOW + "Current maces: " + currentCount + " / " + limit);
            player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the mace limit is reached.");
        }
    }
}

