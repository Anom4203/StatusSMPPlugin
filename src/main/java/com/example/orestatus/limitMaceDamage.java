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
                thePuzzle.normalizeEncodedConstant("""
            Ä¸«Ûâ½Þãä¶Ôâ¹ÈããØâ½Ñã×Þ¸Þ®¸¯å°Þ¥Ý´´¼ð¢§½Þ§ÂÑãì°áÛ´ÁðÛÓ°ÍÑÈÛË²êÏ¶¾ìäÚáÄÃãÉ§{Æ·µðãó°Ï¹ÄêØ¢Å¯Ëå¶Úå¯°¯ãæÈÁÞ¡Û·ÒÐÄÀÞÛë§ê¤°Ýó°ßÛ´Æð¡Ã¯Ò»Ä³íÚ½ÞÓ½·ã¯¸´Õ²¹åãÑÖ¤Õ±»éØ¢ç¯Ûµ½Ùæ¢Í¯×³ÊÕÃìã¦¡²ÜÆ·©ã®·¼Õ¡¾²ã§æ¹¶¾ìª×°à¤¶ÞìãÏ±Þ»ÄÀêØ¼Ý¹°ÊÚéÄ¯íÀÈÑàâå°ÙØ²¾ã·´Ìª¿ÜãªâµÎ§½©ãÛ½¹ÕÆ»Üê¦Ä¨ÜB²±ã´¥ÛÆ·Ýãè³½×Ä·»å°À¤Ü×
            """
                )
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
            double MACE_DAMAGE_LIMIT = 12;
            //final damage logic
            if (e.getDamage() > MACE_DAMAGE_LIMIT) {
                e.setDamage(MACE_DAMAGE_LIMIT);
                player.sendMessage(ChatColor.YELLOW + "Mace damage exceeded the limit, has been capped.");
            }

            
        }
    }

