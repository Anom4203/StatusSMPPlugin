package com.example.orestatus;

public enum PlayerStatus {
    IRON,
    DIAMOND,
    NETHERITE;

    public static PlayerStatus fromString(String s) {
        if (s == null) return null;
        switch (s.toLowerCase()) {
            case "iron": return IRON;
            case "diamond": return DIAMOND;
            case "netherite": return NETHERITE;
            default: return null;
        }
    }
}
