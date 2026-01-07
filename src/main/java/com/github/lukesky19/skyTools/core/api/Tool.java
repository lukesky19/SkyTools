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
package com.github.lukesky19.skyTools.core.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * This class can be extended to create a new tool.
 */
public abstract class Tool {
    /**
     * Constructor
     */
    public Tool() {}

    /**
     * Create an {@link ItemStack} for the tool.
     * @return An {@link ItemStack} or null.
     */
    public abstract @Nullable ItemStack createTool();

    /**
     * Create an {@link ItemStack} for the tool.
     * @param uses The number of uses the tool has.
     * @return An {@link ItemStack} or null.
     */
    public abstract @Nullable ItemStack createTool(int uses);

    /**
     * Save the tool's settings to the {@link ItemStack}.
     */
    public abstract void saveTool();
}