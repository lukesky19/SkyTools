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
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages interfacing with ItemsAdder.
 */
public class ItemsAdderHook implements Hook {
    private final @NonNull SkyPlugin plugin;
    private boolean hooked = false;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     */
    public ItemsAdderHook(@NonNull SkyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("ItemsAdder");
        hooked = plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    /**
     * Get the namespaced id for the entity provided (if a custom entity).
     * @param entity The {@link Entity}.
     * @return The namespaced id as a string (namespace:id) or null.
     */
    public @Nullable String getCustomEntityNamespaceId(@NonNull Entity entity) {
        if(!hooked) return null;

        CustomEntity customEntity = CustomEntity.byAlreadySpawned(entity);
        if(customEntity == null) return null;

        return customEntity.getNamespacedID();
    }

    /**
     * Attempt to spawn a custom entity at the location using the namespace id and entity name provided.
     * @param location The {@link Location} to spawn the entity at.
     * @param namespacedId The namespaced id as a {@link String}.
     * @return The {@link CustomEntity} or null.
     */
    public @Nullable CustomEntity spawnCustomEntity(
            @NonNull Location location,
            @NonNull String namespacedId) {
        if(!hooked) return null;

        return CustomEntity.spawn(namespacedId, location);
    }

    /**
     * Get an {@link ItemStack} from the namespaced id.
     * @param namespacedId The namespaced id as a {@link String}.
     * @return An {@link ItemStack} or null.
     */
    public @Nullable ItemStack getCustomItem(@NonNull String namespacedId) {
        if(!hooked) return null;

        CustomStack customStack = CustomStack.getInstance(namespacedId);
        if(customStack == null) return null;

        return customStack.getItemStack();
    }
}
