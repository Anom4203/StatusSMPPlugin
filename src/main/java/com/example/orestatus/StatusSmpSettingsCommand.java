package com.example.orestatus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class StatusSmpSettingsCommand implements CommandExecutor {

    private final OreStatusPlugin plugin;

    public StatusSmpSettingsCommand(OreStatusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Only operators can open this UI.", NamedTextColor.RED));
            return true;
        }
        if (!(sender instanceof Player operator)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        // Require a player argument
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /statusSmpSettings <player>", NamedTextColor.RED));
            sender.sendMessage(Component.text("Select a player whose status you want to change.", NamedTextColor.YELLOW));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[0] + "' not found or is offline.", NamedTextColor.RED));
            return true;
        }

        // Create inventory holder with target player info
        StatusInventoryHolder holder = new StatusInventoryHolder(target.getUniqueId(), target.getName());
        String guiTitle = "Status Settings: " + target.getName();
        Inventory gui = Bukkit.createInventory(holder, 9*3, Component.text(guiTitle));
        
        // Store target player UUID in plugin's map for reliable lookup
        plugin.getStatusGuiTargets().put(gui, target.getUniqueId());

        // Get current status to show in GUI
        PlayerStatus currentStatus = plugin.getStatusManager().getStatus(target);
        String currentStatusText = currentStatus != null ? currentStatus.name() : "None";

        // Create Iron tier item with name and lore
        ItemStack iron = new ItemStack(Material.IRON_INGOT);
        ItemMeta ironMeta = iron.getItemMeta();
        ironMeta.displayName(Component.text("Iron Tier", NamedTextColor.GRAY));
        ironMeta.lore(Arrays.asList(
            Component.text("Click to set " + target.getName() + "'s tier to IRON", NamedTextColor.YELLOW),
            Component.text("Lowest tier - Basic items allowed", NamedTextColor.GRAY),
            Component.text("Current: " + currentStatusText, NamedTextColor.DARK_GRAY)
        ));
        iron.setItemMeta(ironMeta);
        gui.setItem(10, iron);

        // Create Diamond tier item with name and lore
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta diamondMeta = diamond.getItemMeta();
        diamondMeta.displayName(Component.text("Diamond Tier", NamedTextColor.AQUA));
        diamondMeta.lore(Arrays.asList(
            Component.text("Click to set " + target.getName() + "'s tier to DIAMOND", NamedTextColor.YELLOW),
            Component.text("Mid tier - Advanced items allowed", NamedTextColor.GRAY),
            Component.text("Current: " + currentStatusText, NamedTextColor.DARK_GRAY)
        ));
        diamond.setItemMeta(diamondMeta);
        gui.setItem(12, diamond);

        // Create Netherite tier item with name and lore
        ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta netheriteMeta = netherite.getItemMeta();
        netheriteMeta.displayName(Component.text("Netherite Tier", NamedTextColor.DARK_PURPLE));
        netheriteMeta.lore(Arrays.asList(
            Component.text("Click to set " + target.getName() + "'s tier to NETHERITE", NamedTextColor.YELLOW),
            Component.text("Highest tier - All items allowed", NamedTextColor.GRAY),
            Component.text("Current: " + currentStatusText, NamedTextColor.DARK_GRAY)
        ));
        netherite.setItemMeta(netheriteMeta);
        gui.setItem(14, netherite);

        // Create Clear status item
        ItemStack clear = new ItemStack(Material.BARRIER);
        ItemMeta clearMeta = clear.getItemMeta();
        clearMeta.displayName(Component.text("Clear Status", NamedTextColor.RED));
        clearMeta.lore(Arrays.asList(
            Component.text("Click to remove " + target.getName() + "'s tier", NamedTextColor.YELLOW),
            Component.text("They will have no tier restrictions", NamedTextColor.GRAY),
            Component.text("Current: " + currentStatusText, NamedTextColor.DARK_GRAY)
        ));
        clear.setItemMeta(clearMeta);
        gui.setItem(16, clear);

        operator.openInventory(gui);
        return true;
    }
}
