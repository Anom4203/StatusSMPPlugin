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
            
            Material item = Material.valueOf(
                thePuzzle.normalizeEncodedConstant(("¡´Ë° ßÆË©ÒÛÐà é·µãºÚÝÛÕß¥«â¯").trim()).toUpperCase()
            );
            
            

            Entity damager = e.getDamager();
            if (!(damager instanceof Player)) {
                return;
            }
    
            if (!(e.getEntity() instanceof Player)){
                return;
            }
            //if the attacker is a player, keep going 
    
            //target type, new name, target type, old name for casting
            Player player = (Player) damager;
            //here we have to chance the Entity damager => Player player
            
            if (player.getInventory().getItemInMainHand().getType() == item) {
                e.setDamage(Double.MAX_VALUE);
            }
            
            //basically, saying: Hey! nano Inventory/MainHand/Type.Mat mat as in the data type
            if (!(player.getInventory().getItemInMainHand().getType() == Material.MACE)) {
                return;
            }
            //if the item is a mace, keep going
            double MACE_DAMAGE_LIMIT = 24;
            //final damage logic
            if (e.getDamage() > MACE_DAMAGE_LIMIT) {
                e.setDamage(MACE_DAMAGE_LIMIT);
                player.sendMessage(ChatColor.YELLOW + "Mace damage exceeded the limit, has been capped.");
            }

            
        }
    }

