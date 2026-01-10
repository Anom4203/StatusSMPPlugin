package com.example.orestatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatusManager {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerStatus> statuses = new HashMap<>();

    public StatusManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setStatus(UUID uuid, PlayerStatus status) {
        if (status == null) statuses.remove(uuid);
        else statuses.put(uuid, status);
        save(uuid);
    }

    public void setStatus(org.bukkit.entity.Player player, PlayerStatus status) {
        setStatus(player.getUniqueId(), status);
    }

    public PlayerStatus getStatus(UUID uuid) {
        return statuses.get(uuid);
    }

    public PlayerStatus getStatus(org.bukkit.entity.Player p) {
        return statuses.get(p.getUniqueId());
    }

    public void loadAll() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("statuses");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                UUID u = UUID.fromString(key);
                String s = cfg.getString("statuses." + key);
                if (s != null) statuses.put(u, PlayerStatus.fromString(s));
            } catch (Exception ignored) {}
        }
    }

    public void save(UUID uuid) {
        if (uuid == null) return;
        FileConfiguration cfg = plugin.getConfig();
        if (statuses.containsKey(uuid)) cfg.set("statuses." + uuid.toString(), statuses.get(uuid).name());
        else cfg.set("statuses." + uuid.toString(), null);
        plugin.saveConfig();
    }

    public void saveAll() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("statuses", null);
        for (Map.Entry<UUID, PlayerStatus> e : statuses.entrySet()) {
            cfg.set("statuses." + e.getKey().toString(), e.getValue().name());
        }
        plugin.saveConfig();
    }
}
