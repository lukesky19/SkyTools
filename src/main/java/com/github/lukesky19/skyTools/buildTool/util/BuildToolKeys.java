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
package com.github.lukesky19.skyTools.buildTool.util;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * This enum contains the {@link NamespacedKey}s used for the build tool.
 */
public enum BuildToolKeys {
    /**
     * This is for the key that identifies a build tool.
     */
    BUILD_TOOL,
    /**
     * This is for the key that stores the build tool's material.
     */
    MATERIAL,
    /**
     * This is for the key that stores the build tool's first position.
     */
    POSITION1,
    /**
     * This is for the key that stores the build tool's second position.
     */
    POSITION2;

    /**
     * The {@link NamespacedKey} for the enum.
     */
    private final NamespacedKey key;

    /**
     * Constructor
     */
    BuildToolKeys() {
        this.key = new NamespacedKey("skytools", this.name().toLowerCase());
    }

    /**
     * Get the {@link NamespacedKey}.
     * @return The {@link NamespacedKey}.
     */
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
