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
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages interfacing with WorldGuard.
 */
public class WorldGuardHook implements Hook {
    private final @NonNull SkyPlugin skyPlugin;
    private @Nullable WorldGuard worldGuard;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     */
    public WorldGuardHook(@NonNull SkyPlugin skyPlugin) {
        this.skyPlugin = skyPlugin;
    }

    @Override
    public void initialize() {
        if(skyPlugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            worldGuard = WorldGuard.getInstance();
        }
    }

    @Override
    public boolean isHooked() {
        return worldGuard != null;
    }

    /**
     * Get a {@link List} of {@link String}s for the region names the location is in.
     * @param location The {@link Location}.
     * @return A {@link List} of {@link String}s.
     */
    public @NonNull List<String> getRegionNames(@NonNull Location location) {
        if(worldGuard == null) return new ArrayList<>();

        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);

        return query.getApplicableRegions(wgLocation).getRegions().stream().map(ProtectedRegion::getId).toList();
    }

    /**
     * Can the player place blocks at location?
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if they can, false if not.
     */
    public boolean canPlayerPlaceBlocks(@NonNull Player player, @NonNull Location location) {
        if(worldGuard == null) return true;

        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());

        if(worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return true;
        }

        StateFlag.@Nullable State state = query.queryState(wgLocation, wgPlayer, Flags.BLOCK_PLACE);

        return state == null || state.equals(StateFlag.State.ALLOW);
    }

    /**
     * Can the player interact with entities at the location?
     * @param player The {@link Player}.
     * @param location The {@link Location}.
     * @return true if the player can, or false if not.
     */
    public boolean canPlayerInteractEntity(@NonNull Player player, @NonNull Location location) {
        if(worldGuard == null) return true;

        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());

        if(worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return true;
        }

        StateFlag.@Nullable State state = query.queryState(wgLocation, wgPlayer, Flags.INTERACT);

        return state == null || state.equals(StateFlag.State.ALLOW);
    }
}
