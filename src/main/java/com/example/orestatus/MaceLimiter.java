package com.example.orestatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class MaceLimiter implements Listener {

    private final OreStatusPlugin plugin;

    public MaceLimiter(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }


    private boolean isMace(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getType().name().contains("MACE");
    }

    private boolean isHeavyCore(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        String name = item.getType().name();
        return name.contains("HEAVY_CORE") || (name.contains("HEAVY") && name.contains("CORE"));
    }


    public int countMacesAndHeavyCoresOnServer() {
        int count = 0;

        // Player inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();

            for (ItemStack item : inv.getContents()) {
                if (isMace(item) || isHeavyCore(item)) {
                    count += item.getAmount();
                }
            }

            for (ItemStack item : inv.getArmorContents()) {
                if (isMace(item) || isHeavyCore(item)) {
                    count += item.getAmount();
                }
            }
        }

        // Dropped items
        for (World world : Bukkit.getWorlds()) {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            for (Item item : items) {
                ItemStack stack = item.getItemStack();
                if (isMace(stack) || isHeavyCore(stack)) {
                    count += stack.getAmount();
                }
            }
        }

        return count;
    }

    private boolean limitReached() {
        int limit = plugin.getMaceLimit();
        if (limit <= 0) return false;
        return countMacesAndHeavyCoresOnServer() >= limit;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack result = e.getRecipe().getResult();
        if (!isMace(result) && !isHeavyCore(result)) return;

        if (limitReached()) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Server limit for maces/heavy cores has been reached.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        Inventory inv = e.getInventory();
        if (inv.getType() != InventoryType.CRAFTING &&
            inv.getType() != InventoryType.WORKBENCH &&
            inv.getType() != InventoryType.CRAFTER) {
            return;
        }

        if (e.getSlotType() != InventoryType.SlotType.RESULT) return;

        ItemStack item = e.getCurrentItem();
        if (!isMace(item) && !isHeavyCore(item)) return;

        if (limitReached()) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Server limit for maces/heavy cores has been reached.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        ItemStack item = e.getItem().getItemStack();
        if (!isMace(item) && !isHeavyCore(item)) return;

        if (limitReached()) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cannot pick up item — server mace limit reached.");
        }
    }
}
