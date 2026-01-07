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

import com.github.lukesky19.skyTools.core.integration.impl.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utility methods related to the build tool.
 */
public class PluginUtils {
    /**
     * Constructor
     * @throws RuntimeException if used.
     */
    public PluginUtils() throws RuntimeException {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Get a {@link List} of {@link Location}s for the two positions.
     * @param worldGuardHook A {@link WorldGuardHook} instance.
     * @param disallowedRegions The region names not allowed.
     * @param position1 The first position.
     * @param position2 The second position.
     * @return A {@link List} of {@link Location}s.
     */
    public static @NotNull List<Location> getLocationsInArea(
            @NotNull WorldGuardHook worldGuardHook,
            @NotNull List<String> disallowedRegions,
            @NotNull Location position1,
            @NotNull Location position2) {
        if(!position1.getWorld().getName().equals(position2.getWorld().getName())) return new ArrayList<>();
        World world = position1.getWorld();

        int minX = Math.min(position1.getBlockX(), position2.getBlockX());
        int minY = Math.min(position1.getBlockY(), position2.getBlockY());
        int minZ = Math.min(position1.getBlockZ(), position2.getBlockZ());
        int maxX = Math.max(position1.getBlockX(), position2.getBlockX());
        int maxY = Math.max(position1.getBlockY(), position2.getBlockY());
        int maxZ = Math.max(position1.getBlockZ(), position2.getBlockZ());

        List<Location> locationList = new ArrayList<>();

        for(int y = minY; y <= maxY; y++) {
            for(int x = minX; x <= maxX; x++) {
                for(int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);

                    if(isLocationDisallowed(worldGuardHook, disallowedRegions, location)) continue;

                    locationList.add(location);
                }
            }
        }

        return locationList;
    }

    /**
     * Gets a list of Locations to spawn particles at to create a hollow cube.
     * @param corner1 The corner of the first location.
     * @param corner2 The corner of the second location.
     * @param particleDistance The distance between particles.
     * @return A List of Locations
     */
    public static @NotNull List<Location> getHollowCube(@NotNull Location corner1, @NotNull Location corner2, double particleDistance) {
        List<Location> particleLocations = new ArrayList<>();

        // If the corners are not in the same world or the world is null, return an empty list.
        World world = corner1.getWorld();
        if (world == null || !corner1.getWorld().equals(corner2.getWorld())) {
            return particleLocations;
        }

        // Find the minimum X, Y, and Z coordinates.
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());

        // Find the maximum X, Y, and Z, coordinates.
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        // Calculate the Locations along the X-axis.
        for(double x = minX; x <= maxX; x += particleDistance) {
            particleLocations.add(new Location(world, x, minY, minZ));
            particleLocations.add(new Location(world, x, maxY, minZ));
            particleLocations.add(new Location(world, x, minY, maxZ));
            particleLocations.add(new Location(world, x, maxY, maxZ));
        }

        // Calculate the Locations along the Y-axis.
        for(double y = minY; y <= maxY; y += particleDistance) {
            particleLocations.add(new Location(world, minX, y, minZ));
            particleLocations.add(new Location(world, maxX, y, minZ));
            particleLocations.add(new Location(world, minX, y, maxZ));
            particleLocations.add(new Location(world, maxX, y, maxZ));
        }

        // Calculate the Locations along the Z-axis.
        for(double z = minZ; z <= maxZ; z += particleDistance) {
            particleLocations.add(new Location(world, minX, minY, z));
            particleLocations.add(new Location(world, maxX, minY, z));
            particleLocations.add(new Location(world, minX, maxY, z));
            particleLocations.add(new Location(world, maxX, maxY, z));
        }

        return particleLocations;
    }

    /**
     * Is a location not allowed based on the regions it is in?
     * @param worldGuardHook A {@link WorldGuardHook} instance.
     * @param disallowedWorlds A {@link List} of disallowed world names as a {@link String}.
     * @param disallowedRegions A {@link List} of disallowed region names as a {@link String}.
     * @param location The {@link Location}.
     * @return true if disallowed, false if allowed.
     */
    public static boolean isLocationDisallowed(
            @NotNull WorldGuardHook worldGuardHook,
            @NotNull List<String> disallowedWorlds,
            @NotNull List<String> disallowedRegions,
            @NotNull Location location) {
        if(disallowedWorlds.contains(location.getWorld().getName())) return true;

        if(worldGuardHook.isHooked()) {
            List<String> effectiveRegions = worldGuardHook.getRegionNames(location);

            for(String regionName : disallowedRegions) {
                if(effectiveRegions.contains(regionName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Is a location not allowed based on the regions it is in?
     * @param worldGuardHook A {@link WorldGuardHook} instance.
     * @param disallowedRegions A {@link List} of disallowed region names as a {@link String}.
     * @param location The {@link Location}.
     * @return true if disallowed, false if allowed.
     */
    private static boolean isLocationDisallowed(
            @NotNull WorldGuardHook worldGuardHook,
            @NotNull List<String> disallowedRegions,
            @NotNull Location location) {
        if(worldGuardHook.isHooked()) {
            List<String> effectiveRegions = worldGuardHook.getRegionNames(location);

            for(String regionName : disallowedRegions) {
                if(effectiveRegions.contains(regionName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
