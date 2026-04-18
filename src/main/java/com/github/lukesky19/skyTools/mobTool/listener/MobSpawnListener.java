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
package com.github.lukesky19.skyTools.mobTool.listener;

import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.impl.ItemsAdderHook;
import com.github.lukesky19.skyTools.core.integration.impl.RoseStackerHook;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import com.github.lukesky19.skyTools.mobTool.util.SpawnEggKeys;
import dev.lone.itemsadder.api.CustomEntity;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

/**
 * Listens for when a player attempts to use a custom spawn egg from the mob capture tool.
 */
public class MobSpawnListener implements Listener {
    private final @NonNull MobCaptureToolManager mobCaptureToolManager;
    private final @NonNull HookManager hookManager;

    /**
     * Constructor
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public MobSpawnListener(
            @NonNull MobCaptureToolManager mobCaptureToolManager,
            @NonNull HookManager hookManager) {
        this.mobCaptureToolManager = mobCaptureToolManager;
        this.hookManager = hookManager;
    }

    /**
     * Listens for when a player clicks with an item from a mob capture tool.
     * @param playerInteractEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnEggClick(@NonNull PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        ItemsAdderHook itemsAdderHook = hookManager.getHook(ItemsAdderHook.class);
        RoseStackerHook roseStackerHook = hookManager.getHook(RoseStackerHook.class);

        Location location = playerInteractEvent.getInteractionPoint();
        if(location == null) return;
        ItemStack itemStack = playerInteractEvent.getItem();
        if(itemStack == null) return;
        if(!mobCaptureToolManager.isCustomSpawnEgg(itemStack)) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        String namespacedId = persistentDataContainer.get(SpawnEggKeys.ITEMSADDER_NAMESPACED_ID.getKey(), PersistentDataType.STRING);
        boolean aiStatus = persistentDataContainer.getOrDefault(SpawnEggKeys.AI_STATUS.getKey(), PersistentDataType.BOOLEAN, true);
        boolean awareStatus = persistentDataContainer.getOrDefault(SpawnEggKeys.AWARE_STATUS.getKey(), PersistentDataType.BOOLEAN, true);
        int stackSize = persistentDataContainer.getOrDefault(SpawnEggKeys.ROSESTACKER_STACK_SIZE.getKey(), PersistentDataType.INTEGER, 1);
        EntitySnapshot entitySnapshot = itemMeta instanceof SpawnEggMeta spawnEggMeta ? spawnEggMeta.getSpawnedEntity() : null;

        if(itemsAdderHook.isHooked() && namespacedId != null) {
            playerInteractEvent.setCancelled(true);

            // Spawn the entity
            CustomEntity customEntity = itemsAdderHook.spawnCustomEntity(location, namespacedId);
            // Check if spawned successfully
            if(customEntity != null) {
                // Get the entity
                Entity entity = customEntity.getEntity();

                // Update entity AI settings
                updateEntityAI(entity, aiStatus, awareStatus);

                // Update the entity's stack size (RoseStacker)
                updateEntityStackSize(roseStackerHook, entity, stackSize);

                // Remove the item used to spawn the entity
                removeItemStack(player, itemStack);
            }
        } else if(entitySnapshot != null) {
            playerInteractEvent.setCancelled(true);

            // Spawn the entity
            Entity entity = entitySnapshot.createEntity(location);

            // Update entity AI settings
            updateEntityAI(entity, aiStatus, awareStatus);

            // Update the entity's stack size (RoseStacker)
            updateEntityStackSize(roseStackerHook, entity, stackSize);

            // Remove the item used to spawn the entity
            removeItemStack(player, itemStack);
        }
    }

    /**
     * Update the entity's AI and aware settings if applicable.
     * @param entity The {@link Entity}.
     * @param hasAI If the entity should have AI.
     * @param isAware If the entity should be aware.
     */
    private void updateEntityAI(@NonNull Entity entity, boolean hasAI, boolean isAware) {
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.setAI(hasAI);

            if(livingEntity instanceof Mob mob) {
                mob.setAware(isAware);
            }
        }
    }

    /**
     * Update the entity's stack size.
     * @param roseStackerHook A {@link RoseStackerHook}.
     * @param entity The {@link Entity}.
     * @param stackSize The stack size.
     */
    private void updateEntityStackSize(
            @NonNull RoseStackerHook roseStackerHook,
            @NonNull Entity entity,
            int stackSize) {
        if(!roseStackerHook.isHooked()) return;
        if(!(entity instanceof LivingEntity livingEntity)) return;
        StackedEntity stackedEntity = roseStackerHook.getStackedEntity(livingEntity);
        if(stackedEntity == null) return;

        stackedEntity.increaseStackSize(stackSize - 1, true);
    }

    /**
     * Remove the 1 item based on the original stack provided from the player's inventory.
     * @param player The {@link Player} to remove the item from.
     * @param originalStack The original {@link ItemStack}.
     */
    private void removeItemStack(@NonNull Player player, @NonNull ItemStack originalStack) {
        ItemStack removeStack = originalStack.clone();
        removeStack.setAmount(1);

        player.getInventory().removeItem(removeStack);
    }
}
