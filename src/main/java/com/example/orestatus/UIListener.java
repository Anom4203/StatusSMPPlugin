package com.example.orestatus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UIListener implements Listener {

    private final OreStatusPlugin plugin;

    public UIListener(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Only process clicks in the top inventory (the GUI), not player inventory
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory == null) return;
        
        // Check if this inventory is registered in our map (more reliable than holder)
        UUID targetUUID = plugin.getStatusGuiTargets().get(topInventory);
        if (targetUUID == null) {
            // Fallback: try holder method
            InventoryHolder holder = topInventory.getHolder();
            if (holder instanceof StatusInventoryHolder statusHolder) {
                targetUUID = statusHolder.getTargetPlayerUUID();
            } else {
                return;
            }
        }

        // Cancel all clicks in this inventory
        e.setCancelled(true);
        
        // Only process clicks in the top inventory (GUI), ignore clicks in player inventory
        if (e.getClickedInventory() != topInventory) {
            return;
        }
        
        if (!(e.getWhoClicked() instanceof Player operator)) return;
        
        // Only operators can use this UI
        if (!operator.isOp()) {
            operator.sendMessage(Component.text("Only operators can use this UI.", NamedTextColor.RED));
            operator.closeInventory();
            return;
        }

        // Get the clicked item
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // Get target player from UUID
        Player target = plugin.getServer().getPlayer(targetUUID);
        if (target == null) {
            operator.sendMessage(Component.text("Target player is no longer online.", NamedTextColor.RED));
            operator.closeInventory();
            // Clean up the map
            plugin.getStatusGuiTargets().remove(topInventory);
            return;
        }

        String targetName = target.getName();

        // Clean up the map when closing
        plugin.getStatusGuiTargets().remove(topInventory);
        
        if (clicked.getType() == Material.IRON_INGOT) {
            plugin.getStatusManager().setStatus(target, PlayerStatus.IRON);
            operator.sendMessage(Component.text(targetName + "'s tier set to IRON.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("Your tier has been set to IRON by " + operator.getName() + ".", NamedTextColor.GREEN));
            operator.closeInventory();
        } else if (clicked.getType() == Material.DIAMOND) {
            plugin.getStatusManager().setStatus(target, PlayerStatus.DIAMOND);
            operator.sendMessage(Component.text(targetName + "'s tier set to DIAMOND.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("Your tier has been set to DIAMOND by " + operator.getName() + ".", NamedTextColor.GREEN));
            operator.closeInventory();
        } else if (clicked.getType() == Material.NETHERITE_INGOT) {
            plugin.getStatusManager().setStatus(target, PlayerStatus.NETHERITE);
            operator.sendMessage(Component.text(targetName + "'s tier set to NETHERITE.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("Your tier has been set to NETHERITE by " + operator.getName() + ".", NamedTextColor.GREEN));
            operator.closeInventory();
        } else if (clicked.getType() == Material.BARRIER) {
            plugin.getStatusManager().setStatus(target, null);
            operator.sendMessage(Component.text(targetName + "'s tier has been cleared.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("Your tier has been cleared by " + operator.getName() + ".", NamedTextColor.GREEN));
            operator.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // Clean up the map when inventory is closed
        Inventory inventory = e.getInventory();
        plugin.getStatusGuiTargets().remove(inventory);
    }
}
