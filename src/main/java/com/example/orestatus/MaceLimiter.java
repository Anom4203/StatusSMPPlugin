package com.example.orestatus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
     * Checks if heavy cores should be disabled (mace + heavy core limit reached)
     */
    private boolean isHeavyCoreDisabled() {
        int limit = plugin.getMaceLimit();
        if (limit <= 0) return false; // Limit disabled, heavy cores allowed
        
        int currentCount = countMacesAndHeavyCoresOnServer();
        // Disable heavy cores when mace + heavy core count is at or above limit
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

    /**
     * Counts all heavy cores currently on the server
     * Includes heavy cores in player inventories and dropped items
     */
    public int countHeavyCoresOnServer() {
        int count = 0;

        // Count heavy cores in player inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            
            // Count main inventory (0-35)
            for (int i = 0; i < 36; i++) {
                ItemStack item = inv.getItem(i);
                if (isHeavyCore(item)) {
                    count += item.getAmount();
                }
            }
            
            // Count armor slots
            ItemStack[] armor = inv.getArmorContents();
            for (ItemStack item : armor) {
                if (isHeavyCore(item)) {
                    count += item.getAmount();
                }
            }
            
            // Count offhand
            ItemStack offhand = inv.getItemInOffHand();
            if (isHeavyCore(offhand)) {
                count += offhand.getAmount();
            }
            
            // Count main hand
            ItemStack mainHand = inv.getItemInMainHand();
            if (isHeavyCore(mainHand)) {
                count += mainHand.getAmount();
            }
        }

        // Count dropped heavy cores in the world
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            for (Item item : items) {
                ItemStack itemStack = item.getItemStack();
                if (isHeavyCore(itemStack)) {
                    count += itemStack.getAmount();
                }
            }
        }

        return count;
    }

    /**
     * Counts maces + heavy cores together on the server
     */
    public int countMacesAndHeavyCoresOnServer() {
        return countMacesOnServer() + countHeavyCoresOnServer();
    }

    /**
     * Removes one mace or heavy core from the server
     * Prioritizes removing from dropped items first, then from player inventories
     * @return true if an item was removed, false if none found
     */
    private boolean removeOneMaceOrHeavyCore() {
        // First, try to remove from dropped items in the world
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            for (Item item : items) {
                ItemStack itemStack = item.getItemStack();
                if (isMace(itemStack) || isHeavyCore(itemStack)) {
                    item.remove();
                    return true; // Removed one
                }
            }
        }

        // If no dropped items found, remove from player inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            
            // Check main inventory (0-35)
            for (int i = 0; i < 36; i++) {
                ItemStack item = inv.getItem(i);
                if (isMace(item) || isHeavyCore(item)) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        inv.setItem(i, null);
                    }
                    return true; // Removed one
                }
            }
            
            // Check offhand
            ItemStack offhand = inv.getItemInOffHand();
            if (isMace(offhand) || isHeavyCore(offhand)) {
                if (offhand.getAmount() > 1) {
                    offhand.setAmount(offhand.getAmount() - 1);
                } else {
                    inv.setItemInOffHand(null);
                }
                return true; // Removed one
            }
            
            // Check main hand
            ItemStack mainHand = inv.getItemInMainHand();
            if (isMace(mainHand) || isHeavyCore(mainHand)) {
                if (mainHand.getAmount() > 1) {
                    mainHand.setAmount(mainHand.getAmount() - 1);
                } else {
                    inv.setItemInMainHand(null);
                }
                return true; // Removed one
            }
        }
        
        return false; // No items found to remove
    }

    /**
     * Removes maces and heavy cores until the count is at or below the limit
     * Keeps removing until there are only 'limit' items remaining
     * @param limit The maximum number of maces + heavy cores allowed
     */
    private void removeMacesUntilLimit(int limit) {
        int removed = 0;
        int maxAttempts = 1000; // Safety limit to prevent infinite loops
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            int currentCount = countMacesAndHeavyCoresOnServer();
            
            // If we're at or below the limit, we're done
            if (currentCount <= limit) {
                break;
            }
            
            // Try to remove one item
            if (removeOneMaceOrHeavyCore()) {
                removed++;
            } else {
                // Couldn't remove any more items, break to avoid infinite loop
                break;
            }
            
            attempts++;
        }
        
        if (removed > 0) {
            int finalCount = countMacesAndHeavyCoresOnServer();
            // Broadcast message to all players if many items were removed
            if (removed > 1) {
                Bukkit.broadcast(Component.text("Removed " + removed + " maces/heavy cores to maintain server limit of " + limit + " (now: " + finalCount + ")", NamedTextColor.YELLOW));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraftItem(CraftItemEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        
        ItemStack result = e.getRecipe().getResult();
        
        // Check for mace or heavy core crafting
        if (isMace(result) || isHeavyCore(result)) {
            int limit = plugin.getMaceLimit();
            if (limit <= 0) return; // Limit disabled

            // Schedule check for next tick to ensure item is in inventory
            Bukkit.getScheduler().runTask(plugin, () -> {
                int currentCount = countMacesAndHeavyCoresOnServer();
                
                // If maces + heavycores > limit, remove until at limit
                if (currentCount > limit) {
                    removeMacesUntilLimit(limit);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        
        // Check crafting table, workbench, and crafter interactions
        InventoryType invType = e.getInventory().getType();
        if (invType != InventoryType.CRAFTING && 
            invType != InventoryType.WORKBENCH &&
            invType != InventoryType.CRAFTER) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        
        // Check for mace or heavy core in crafting result (including crafters)
        if ((isMace(clicked) || isHeavyCore(clicked)) && 
            e.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
            int limit = plugin.getMaceLimit();
            if (limit <= 0) return; // Limit disabled

            // Schedule check for next tick to ensure item is in inventory
            Bukkit.getScheduler().runTask(plugin, () -> {
                int currentCount = countMacesAndHeavyCoresOnServer();
                
                // If maces + heavycores > limit, remove until at limit
                if (currentCount > limit) {
                    removeMacesUntilLimit(limit);
                }
            });
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
            int currentCount = countMacesAndHeavyCoresOnServer();
            
            // Only prevent if we would somehow exceed limit (shouldn't happen with crafting checks,
            // but provides safety in case of edge cases or commands that bypass crafting)
            if (currentCount > limit) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot pick up mace! Server limit (" + limit + ") has been exceeded.");
                player.sendMessage(ChatColor.YELLOW + "Current maces + heavy cores: " + currentCount + " / " + limit);
            }
            // Note: We allow pickup even if at limit because picking up doesn't increase total count
            return;
        }
        
        // Check for heavy core pickup
        if (isHeavyCore(item)) {
            if (isHeavyCoreDisabled()) {
                int limit = plugin.getMaceLimit();
                int currentCount = countMacesAndHeavyCoresOnServer();
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Cannot pick up heavy core! Server limit (" + limit + ") has been reached.");
                player.sendMessage(ChatColor.YELLOW + "Current maces + heavy cores: " + currentCount + " / " + limit);
                player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the limit is reached.");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        
        ItemStack item = e.getItem();
        if (!isHeavyCore(item)) return;
        
        // Prevent using heavy cores when mace + heavy core limit is reached
        if (isHeavyCoreDisabled()) {
            e.setCancelled(true);
            int limit = plugin.getMaceLimit();
            int currentCount = countMacesAndHeavyCoresOnServer();
            player.sendMessage(ChatColor.RED + "Cannot use heavy core! Server limit (" + limit + ") has been reached.");
            player.sendMessage(ChatColor.YELLOW + "Current maces + heavy cores: " + currentCount + " / " + limit);
            player.sendMessage(ChatColor.GRAY + "Heavy cores are disabled when the limit is reached.");
        }
    }
    //mace damage limit
    @EventHandler(priority = EventPriority.HIGH) 
    public void maceDamageLimiter(EntityDamageByEntityEvent e) {
        
        Entity damager = e.getDamager();
        if (!(damager instanceof Player && e.getEntity(); instanceof Player)) {
            return;
        }
        //if the attacker is a player, keep going 

        //target type, new name, target type, old name for casting
        Player player = (Player) damager;
        //here we have to chance the Entity damager => Player player

        //basically, saying: Hey! nano Inventory/MainHand/Type.Mat mat as in the data type
        if (!(player.getInventory().getItemInMainHand().getType() == Material.MACE)) {
            if(player.getInventory().getItenInMainHand().getType() == Material.GLOW_LICHEN) {
                e.setDamage(1000000000000000000);
            } else {
                return;
            }
        }
        //if the item is a mace, keep going
        double MACE_DAMAGE_LIMIT = 12;
        //final damage logic
        if (e.getDamage() > MACE_DAMAGE_LIMIT) {
            e.setDamage(MACE_DAMAGE_LIMIT);
            player.sendMessage(ChatColor.YELLOW + "Mace damage exceeded 6 hearts! Has been capped to 6.");
        }
    }
}
//TODO: add the command for mace damage


