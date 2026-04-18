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
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.nms.NMSAdapter;
import dev.rosewood.rosestacker.nms.NMSHandler;
import dev.rosewood.rosestacker.nms.spawner.SpawnerType;
import dev.rosewood.rosestacker.stack.StackedEntity;
import dev.rosewood.rosestacker.utils.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages interfacing with RoseStacker.
 */
public class RoseStackerHook implements Hook {
    private final @NonNull SkyPlugin plugin;
    private @Nullable RoseStackerAPI roseStackerAPI;

    /**
     * Constructor
     * @param plugin A {@link JavaPlugin} instance.
     */
    public RoseStackerHook(@NonNull SkyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to get the {@link RoseStackerAPI} from RoseStacker.
     */
    @Override
    public void initialize() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("RoseStacker");
        if(plugin != null && plugin.isEnabled()) {
            roseStackerAPI = RoseStackerAPI.getInstance();
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return roseStackerAPI != null;
    }

    /**
     * Is the {@link ItemStack} stacked?
     * @param itemStack The {@link ItemStack}.
     * @return true if stacked or false if not or RoseStacker is not hooked into.
     */
    public boolean isStacked(@NonNull ItemStack itemStack) {
        if(roseStackerAPI == null) return false;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        if(nmsHandler == null) return false;

        return nmsHandler.getItemStackNBTInt(itemStack, "StackSize") > 0;
    }

    /**
     * Get a stacked {@link ItemStack} for the amount provided.
     * @apiNote Will return the original ItemStack with the amount set using {@link ItemStack#setAmount(int)} if RoseStacker isn't hooked into.
     * @param itemStack The original {@link ItemStack}.
     * @param entityType The {@link EntityType} of the spawner or null if not a spawner.
     * @param amount The amount.
     * @return An {@link ItemStack}.
     */
    public @NonNull ItemStack getUpdatedItemStack(@NonNull ItemStack itemStack, @Nullable EntityType entityType, int amount) {
        if(roseStackerAPI == null) {
            itemStack.setAmount(amount);
            return itemStack;
        }

        if(entityType != null) {
            return ItemUtils.getSpawnerAsStackedItemStack(SpawnerType.of(entityType), amount);
        } else {
            return ItemUtils.getBlockAsStackedItemStack(itemStack.getType(), amount);
        }
    }

    /**
     * Get the stack size of the {@link ItemStack} provided.
     * @param itemStack The {@link ItemStack} to get the stack size for.
     * @return The stack size or 0 if not a stacked ItemStack or RoseStacker isn't hooked into. Use {@link ItemStack#getAmount()} in such a scenario.
     */
    public int getStackSize(@NonNull ItemStack itemStack) {
        if(roseStackerAPI == null) return 0;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        if(nmsHandler == null) return 0;

        return nmsHandler.getItemStackNBTInt(itemStack, "StackSize");
    }

    /**
     * Get the {@link SpawnerType} from the {@link ItemStack}.
     * @param itemStack The {@link ItemStack}.
     * @return The {@link SpawnerType} or null.
     */
    public @Nullable SpawnerType getSpawnerType(@NonNull ItemStack itemStack) {
        if(roseStackerAPI == null) return null;

        return ItemUtils.getStackedItemSpawnerType(itemStack);
    }

    /**
     * Get the stack size of the {@link Entity} provided.
     * @apiNote If the provided entity is not stacked or RoseStacker was not hooked into, the method will always return 1.
     * @param entity The {@link Entity} to get the stack size for.
     * @return The entity's stack size or 1.
     */
    public int getStackSize(@NonNull Entity entity) {
        if(roseStackerAPI == null) return 1;
        if(!(entity instanceof LivingEntity livingEntity)) return 1;

        StackedEntity stackedEntity = roseStackerAPI.getStackedEntity(livingEntity);
        if(stackedEntity != null) {
            return stackedEntity.getStackSize();
        } else {
            return 1;
        }
    }

    /**
     * Get the existing stacked entity or create a new one.
     * @param entity The {@link LivingEntity}.
     * @return A {@link StackedEntity} or null.
     */
    public @Nullable StackedEntity getStackedEntity(@NonNull LivingEntity entity) {
        if(roseStackerAPI == null) return null;

        StackedEntity existingStackedEntity = roseStackerAPI.getStackedEntity(entity);
        if(existingStackedEntity != null) return existingStackedEntity;

        return roseStackerAPI.createEntityStack(entity, false);
    }
}