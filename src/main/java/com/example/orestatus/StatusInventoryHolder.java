package com.example.orestatus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class StatusInventoryHolder implements InventoryHolder {
    private final UUID targetPlayerUUID;
    private final String targetPlayerName;

    public StatusInventoryHolder(UUID targetPlayerUUID, String targetPlayerName) {
        this.targetPlayerUUID = targetPlayerUUID;
        this.targetPlayerName = targetPlayerName;
    }

    public UUID getTargetPlayerUUID() {
        return targetPlayerUUID;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }

    @Override
    public Inventory getInventory() {
        return null; // Not used, we create the inventory separately
    }
}

