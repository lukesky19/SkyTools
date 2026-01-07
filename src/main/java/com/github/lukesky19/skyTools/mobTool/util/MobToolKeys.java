package com.github.lukesky19.skyTools.mobTool.util;

import org.bukkit.NamespacedKey;

/**
 * This enum contains the {@link NamespacedKey}s used for the mob capture tool.
 */
public enum MobToolKeys {
    /**
     * This is for the key that identifies a mob capture tool.
     */
    MOB_CAPTURE_TOOL,
    /**
     * This is for the key that identifies how many uses a mob capture tool has.
     */
    USES,
    /**
     * This is for the key that identifies a special namespaced id to use with ItemsAdder.
     */
    ITEMSADDER_NAMESPACED_ID;

    /**
     * The {@link NamespacedKey} for the enum.
     */
    private final NamespacedKey key;

    /**
     * Constructor
     */
    MobToolKeys() {
        this.key = new NamespacedKey("skytools", this.name().toLowerCase());
    }

    /**
     * Get the {@link NamespacedKey}.
     * @return The {@link NamespacedKey}.
     */
    public NamespacedKey getKey() {
        return key;
    }
}
