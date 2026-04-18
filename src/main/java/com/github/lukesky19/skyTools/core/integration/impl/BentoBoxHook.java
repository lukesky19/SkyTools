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
package com.github.lukesky19.skyTools.core.integration.impl;

import com.github.lukesky19.skyTools.core.integration.Hook;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

/**
 * This class manages interfacing with BentoBox.
 */
public class BentoBoxHook implements Hook {
    private final @NonNull SkyPlugin skyPlugin;
    private @Nullable BentoBox bentoBox;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     */
    public BentoBoxHook(@NonNull SkyPlugin skyPlugin) {
        this.skyPlugin = skyPlugin;
    }

    @Override
    public void initialize() {
        if(skyPlugin.getServer().getPluginManager().isPluginEnabled("BentoBox")) {
            bentoBox = BentoBox.getInstance();
        }
    }

    @Override
    public boolean isHooked() {
        return bentoBox != null;
    }

    /**
     * Can the player place blocks at location?
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if they can, false if not.
     */
    public boolean canPlayerPlaceBlocks(@NonNull Player player, @NonNull Location location) {
        if(bentoBox == null) return true;

        Optional<Island> optionalIsland = bentoBox.getIslands().getIslandAt(location);
        if(optionalIsland.isEmpty()) return true;

        Island island = optionalIsland.orElseThrow();
        User user = bentoBox.getPlayers().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.PLACE_BLOCKS);
    }

    /**
     * Can the player hurt with entities at the location?
     * There isn't a single "interact with entities" flag, the closest flags are for hurting entities.
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if the player can, or false if not.
     */
    public boolean canPlayerInteractEntity(@NonNull Player player, @NonNull Location location) {
        if(bentoBox == null) return true;

        Optional<Island> optionalIsland = bentoBox.getIslands().getIslandAt(location);
        if(optionalIsland.isEmpty()) return true;

        Island island = optionalIsland.orElseThrow();
        User user = bentoBox.getPlayers().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.HURT_ANIMALS)
                && island.isAllowed(user, Flags.HURT_MONSTERS)
                && island.isAllowed(user, Flags.HURT_TAMED_ANIMALS)
                && island.isAllowed(user, Flags.HURT_VILLAGERS);
    }
}
