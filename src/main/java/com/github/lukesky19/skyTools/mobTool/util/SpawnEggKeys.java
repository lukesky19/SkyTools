/*
    SkyTools adds new unique tools to the game.
    Copyright (C) 2026 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skyTools.mobTool.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

/**
 * This enum contains the {@link NamespacedKey}s used for the custom spawn eggs created by the mob capture tool.
 */
public enum SpawnEggKeys {
    /**
     * This is for the key that identifies a spawn egg from the mob capture tool.
     */
    CUSTOM_SPAWN_EGG,
    /**
     * This is for the key that identifies whether the living entity's AI status {@link LivingEntity#hasAI()} before being captured.
     */
    AI_STATUS,
    /**
     * This is for the key that identifies whether the mob's aware status {@link Mob#isAware()} before being captured.
     */
    AWARE_STATUS,
    /**
     * The amount of mobs captured.
     */
    ROSESTACKER_STACK_SIZE,
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
    SpawnEggKeys() {
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
