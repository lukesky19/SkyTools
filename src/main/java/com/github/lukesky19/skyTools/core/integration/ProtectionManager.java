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
package com.github.lukesky19.skyTools.core.integration;

import com.github.lukesky19.skyTools.core.integration.impl.BentoBoxHook;
import com.github.lukesky19.skyTools.core.integration.impl.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to check if a player can access an area.
 */
public class ProtectionManager {
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param hookManager A {@link HookManager} instance.
     */
    public ProtectionManager(@NotNull HookManager hookManager) {
        this.hookManager = hookManager;
    }

    /**
     * Can the player place blocks at the location?
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if they can, or false if not.
     */
    public boolean canPlaceBlocks(@NotNull Player player, @NotNull Location location) {
        BentoBoxHook bentoBoxHook = hookManager.getHook(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = hookManager.getHook(WorldGuardHook.class);

        if(bentoBoxHook.isHooked()) {
            if(!bentoBoxHook.canPlayerPlaceBlocks(player, location)) return false;
        }

        if(worldGuardHook.isHooked()) {
            return worldGuardHook.canPlayerPlaceBlocks(player, location);
        }

        return true;
    }

    /**
     * Can the player interact with entities at the location?
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if they can, or false if not.
     */
    public boolean canInteractWithEntities(@NotNull Player player, @NotNull Location location) {
        BentoBoxHook bentoBoxHook = hookManager.getHook(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = hookManager.getHook(WorldGuardHook.class);

        if(bentoBoxHook.isHooked()) {
            if(!bentoBoxHook.canPlayerInteractEntity(player, location)) {
                return false;
            }
        }

        if(worldGuardHook.isHooked()) {
            return worldGuardHook.canPlayerInteractEntity(player, location);
        }

        return true;
    }
}
