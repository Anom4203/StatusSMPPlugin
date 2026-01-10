package com.example.orestatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpearPermissionManager {
    private final JavaPlugin plugin;
    private final Map<UUID, Boolean> spearPermissions = new HashMap<>();

    public SpearPermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets whether a player can use spear features (other than lunge)
     * @param uuid Player UUID
     * @param allowed true = all spear features enabled, false = only lunge enabled
     */
    public void setSpearAllowed(UUID uuid, Boolean allowed) {
        if (allowed == null) {
            spearPermissions.remove(uuid);
        } else {
            spearPermissions.put(uuid, allowed);
        }
        save(uuid);
    }

    /**
     * Sets whether a player can use spear features (other than lunge)
     * @param player Player
     * @param allowed true = all spear features enabled, false = only lunge enabled
     */
    public void setSpearAllowed(org.bukkit.entity.Player player, Boolean allowed) {
        setSpearAllowed(player.getUniqueId(), allowed);
    }

    /**
     * Gets whether a player can use spear features (other than lunge)
     * @param uuid Player UUID
     * @return true if all features allowed, false if only lunge allowed, null if not set (defaults to false - only lunge)
     */
    public Boolean isSpearAllowed(UUID uuid) {
        return spearPermissions.get(uuid);
    }

    /**
     * Gets whether a player can use spear features (other than lunge)
     * @param player Player
     * @return true if all features allowed, false if only lunge allowed, null if not set (defaults to false - only lunge)
     */
    public Boolean isSpearAllowed(org.bukkit.entity.Player player) {
        return isSpearAllowed(player.getUniqueId());
    }

    /**
     * Checks if player can use spear features (other than lunge)
     * Defaults to false if not explicitly set (only lunge enabled by default)
     */
    public boolean canUseSpearFeatures(org.bukkit.entity.Player player) {
        Boolean allowed = isSpearAllowed(player);
        return allowed != null && allowed; // Default to false (only lunge) if not set
    }

    public void loadAll() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("spear-permissions");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                UUID u = UUID.fromString(key);
                Boolean allowed = cfg.getBoolean("spear-permissions." + key);
                spearPermissions.put(u, allowed);
            } catch (Exception ignored) {}
        }
    }

    public void save(UUID uuid) {
        if (uuid == null) return;
        FileConfiguration cfg = plugin.getConfig();
        if (spearPermissions.containsKey(uuid)) {
            cfg.set("spear-permissions." + uuid.toString(), spearPermissions.get(uuid));
        } else {
            cfg.set("spear-permissions." + uuid.toString(), null);
        }
        plugin.saveConfig();
    }

    public void saveAll() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("spear-permissions", null);
        for (Map.Entry<UUID, Boolean> e : spearPermissions.entrySet()) {
            cfg.set("spear-permissions." + e.getKey().toString(), e.getValue());
        }
        plugin.saveConfig();
    }
}

