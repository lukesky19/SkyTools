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
package com.github.lukesky19.skyTools.buildTool.task;

import com.github.lukesky19.skyTools.buildTool.event.BuildToolPlaceBlockEvent;
import com.github.lukesky19.skyTools.buildTool.queue.BlockPlacementQueue;
import com.github.lukesky19.skyTools.buildTool.util.PlacementData;
import com.github.lukesky19.skyTools.core.SkyTools;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.ProtectionManager;
import com.github.lukesky19.skyTools.core.integration.impl.RoseStackerHook;
import com.github.lukesky19.skyTools.core.integration.impl.SkyHoppersHook;
import dev.rosewood.rosestacker.nms.spawner.SpawnerType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * This task manages the processing of a {@link BlockPlacementQueue}.
 */
public class PlacementTask extends BukkitRunnable {
    private final @NotNull SkyTools skyTools;
    private final @NotNull BlockPlacementQueue blockPlacementQueue;
    private final @NotNull ProtectionManager protectionManager;
    private final @NotNull HookManager hookManager;

    private final int placementsPerRun;

    /**
     * Constructor
     * @param skyTools A {@link SkyTools} instance.
     * @param blockPlacementQueue A {@link BlockPlacementQueue} instance.
     * @param protectionManager A {@link ProtectionManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param placementsPerRun How many blocks to place per run.
     */
    public PlacementTask(
            @NotNull SkyTools skyTools,
            @NotNull BlockPlacementQueue blockPlacementQueue,
            @NotNull ProtectionManager protectionManager,
            @NotNull HookManager hookManager,
            int placementsPerRun) {
        this.skyTools = skyTools;
        this.blockPlacementQueue = blockPlacementQueue;
        this.protectionManager = protectionManager;
        this.hookManager = hookManager;

        this.placementsPerRun = placementsPerRun;
    }

    /**
     * Process the queued block placements until there is no more data to process or the hard limit is reached.
     */
    @Override
    public void run() {
        if(placementsPerRun <= 0) blockPlacementQueue.clear();
        if(blockPlacementQueue.isEmpty()) return;
        RoseStackerHook roseStackerHook = hookManager.getHook(RoseStackerHook.class);
        SkyHoppersHook skyHoppersHook = hookManager.getHook(SkyHoppersHook.class);
        int count = 0;

        while(count < placementsPerRun && !blockPlacementQueue.isEmpty()) {
            @Nullable PlacementData placementData = blockPlacementQueue.peek();
            if(placementData == null) continue;
            Material placementMaterial = placementData.material();

            Player player = placementData.player();
            if(!player.isOnline() || !player.isConnected()) {
                blockPlacementQueue.remove();  // If player is no longer available, remove the entry
                continue;
            }

            Inventory inventory = player.getInventory();
            int invSize = inventory.getSize();
            Iterator<Location> locationIterator = placementData.positionList().iterator();

            while(locationIterator.hasNext() && count < placementsPerRun) {
                Location location = locationIterator.next();
                // Skip unloaded chunks
                if(!location.isChunkLoaded()) {
                    locationIterator.remove();
                    continue;
                }

                Block block = location.getBlock();
                Material blockType = block.getType();

                // Skip non-air blocks
                if(!blockType.isAir()) {
                    locationIterator.remove();
                    continue;
                }

                // Skip if the player can't place blocks at the location
                if(!protectionManager.canPlaceBlocks(player, location)) {
                    locationIterator.remove();
                    continue;
                }

                // Attempt to find an item in the inventory
                boolean foundItem = false;
                for(int i = 0; i < invSize; i++) {
                    if(i >= 36 && i <= 39) {
                        continue;
                    }

                    ItemStack invStack = inventory.getItem(i);
                    if(invStack == null || invStack.isEmpty() || !invStack.getType().equals(placementMaterial)) {
                        continue;
                    }

                    // Skip SkyHoppers
                    if(skyHoppersHook.isHooked()) {
                        if(skyHoppersHook.isSkyHopper(invStack)) continue;
                    }

                    if(roseStackerHook.isHooked()) {
                        if(roseStackerHook.isStacked(invStack)) {
                            if(!processStackedItemStack(player, block, placementMaterial, inventory, invStack, i)) continue;
                        } else {
                            if(!processNonStackedItemStack(player, block, placementMaterial, inventory, invStack, i)) continue;
                        }
                    } else {
                        if(!processNonStackedItemStack(player, block, placementMaterial, inventory, invStack, i)) continue;
                    }

                    // Increment count
                    count++;

                    // Mark that item was found
                    foundItem = true;

                    // No need to continue the search after a valid item was found.
                    break;
                }

                // Remove the queued placement if the player lacks the required items
                if(!foundItem) {
                    blockPlacementQueue.remove();
                    break;
                }
            }

            // Remove the queued placement if all locations processed
            if(!locationIterator.hasNext()) {
                blockPlacementQueue.remove();
            }
        }
    }

    /**
     * Process the placement from the stacked ItemStack. Stacked refers to RoseStacker here.
     * @param player The {@link Player} who initiated the build tool usage.
     * @param block The {@link Block} to modify.
     * @param placementMaterial The {@link Material} to set the block to.
     * @param inventory The {@link Inventory} the {@link ItemStack} is in.
     * @param invStack The {@link ItemStack}.
     * @param slot The slot the {@link ItemStack} is in.
     * @return true if successful, false if not.
     */
    private boolean processStackedItemStack(
            @NotNull Player player,
            @NotNull Block block,
            @NotNull Material placementMaterial,
            @NotNull Inventory inventory,
            @NotNull ItemStack invStack,
            int slot) {
        RoseStackerHook roseStackerHook = hookManager.getHook(RoseStackerHook.class);
        ItemMeta itemMeta = invStack.getItemMeta();
        if(itemMeta == null) return false;

        // Get the EntityType if a spawner
        @Nullable EntityType entityType = null;
        if(roseStackerHook.isHooked() && placementMaterial.equals(Material.SPAWNER)) {
            @Nullable SpawnerType spawnerType = roseStackerHook.getSpawnerType(invStack);
            if(spawnerType != null) {
                if(!spawnerType.isEmpty()) {
                    entityType = spawnerType.get().orElse(null);
                }
            }
        }

        // Get the BlockData from the ItemStack
        @Nullable BlockData blockData = getBlockData(invStack);

        int invStackSize = roseStackerHook.getStackSize(invStack);
        if(invStackSize <= 0) return false;

        // Calculate the item stack size
        int updatedAmount = invStackSize - 1;
        // If less than 0, remove the item
        if(updatedAmount <= 0) {
            // Replace the item stack with an empty item stack
            inventory.setItem(slot, ItemType.AIR.createItemStack());
        } else {
            ItemStack updatedItemStack = roseStackerHook.getUpdatedItemStack(invStack, entityType, updatedAmount);

            inventory.setItem(slot, updatedItemStack);
        }

        placeBlock(player, block, placementMaterial, blockData, entityType);

        return true;
    }

    /**
     * Process the placement from the non-stacked ItemStack. Non-stacked refers to RoseStacker here.
     * @param player The {@link Player} who initiated the build tool usage.
     * @param block The {@link Block} to modify.
     * @param placementMaterial The {@link Material} to set the block to.
     * @param inventory The {@link Inventory} the {@link ItemStack} is in.
     * @param invStack The {@link ItemStack}.
     * @param slot The slot the {@link ItemStack} is in.
     * @return true if successful, false if not.
     */
    private boolean processNonStackedItemStack(
            @NotNull Player player,
            @NotNull Block block,
            @NotNull Material placementMaterial,
            @NotNull Inventory inventory,
            @NotNull ItemStack invStack,
            int slot) {
        // Get the ItemMeta
        ItemMeta itemMeta = invStack.getItemMeta();
        if(itemMeta == null) return false;

        // Get the BlockData from the ItemStack
        @Nullable BlockData blockData = getBlockData(invStack);

        // Calculate the item stack size
        int updatedAmount = invStack.getAmount() - 1;
        // If less than 0, remove the item
        if(updatedAmount <= 0) {
            // Replace the item stack with an empty item stack
            inventory.setItem(slot, ItemType.AIR.createItemStack());
        } else {
            invStack.setAmount(updatedAmount);
        }

        placeBlock(player, block, placementMaterial, blockData, null);

        return true;
    }

    /**
     * Get the {@link BlockData} from the {@link ItemStack}.
     * @param itemStack The {@link ItemStack}.
     * @return The {@link BlockData} or null if it has no block data.
     */
    private @Nullable BlockData getBlockData(@NotNull ItemStack itemStack) {
        // Get the ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return null;

        // Get the BlockData from the ItemMeta
        @Nullable BlockData blockData = null;
        if(itemMeta instanceof BlockDataMeta blockDataMeta) {
            if(blockDataMeta.hasBlockData()) {
                blockData = blockDataMeta.getBlockData(itemStack.getType());
            }
        } else if(itemMeta instanceof BlockStateMeta blockStateMeta) {
            if(blockStateMeta.hasBlockState()) {
                blockData = blockStateMeta.getBlockState().getBlockData();
            }
        }

        return blockData;
    }

    /**
     * Replace the block with the placement material, block data, and entity type if non-null and a spawner.
     * @param player The {@link Player} who initiated the build tool usage.
     * @param block The {@link Block} to modify.
     * @param placementMaterial The {@link Material} to set the block to.
     * @param blockData The {@link BlockData}.
     * @param entityType The {@link EntityType} or null.
     */
    private void placeBlock(
            @NotNull Player player,
            @NotNull Block block,
            @NotNull Material placementMaterial,
            @Nullable BlockData blockData,
            @Nullable EntityType entityType) {
        BlockState originalState = block.getState(true);

        // Set the block type
        block.setType(placementMaterial);

        // Set the block data
        if(blockData != null) {
            block.setBlockData(blockData);
        }

        // Set the entity the spawner should spawn
        // This is extra handling for RoseStacker spawners that handles their own storage of the entity type to spawn
        if(block.getState(false) instanceof CreatureSpawner creatureSpawner) {
            if(entityType != null) {
                creatureSpawner.setSpawnedType(entityType);

                creatureSpawner.update();
            }
        }

        skyTools.getServer().getPluginManager().callEvent(new BuildToolPlaceBlockEvent(player, block, originalState));
    }
}
