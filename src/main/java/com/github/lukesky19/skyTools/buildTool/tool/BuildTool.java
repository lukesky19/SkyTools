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
package com.github.lukesky19.skyTools.buildTool.tool;

import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfig;
import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfigurationManager;
import com.github.lukesky19.skyTools.buildTool.queue.BlockPlacementQueue;
import com.github.lukesky19.skyTools.buildTool.util.BuildToolKeys;
import com.github.lukesky19.skyTools.buildTool.util.PlacementData;
import com.github.lukesky19.skyTools.core.api.Tool;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.impl.WorldGuardHook;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.libs.morepersistentdatatypes.DataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.github.lukesky19.skyTools.buildTool.util.PluginUtils.getLocationsInArea;

/**
 * This class represents an {@link ItemStack} that is a build tool.
 */
public class BuildTool extends Tool {
    private final @NotNull ComponentLogger logger;
    private final @NotNull BuildToolConfigurationManager buildToolConfigurationManager;
    private final @NotNull BlockPlacementQueue blockPlacementQueueManager;
    private final @NotNull HookManager hookManager;

    private @Nullable ItemStack itemStack;

    private @Nullable Material material;
    private @Nullable Location position1;
    private @Nullable Location position2;

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param buildToolConfigurationManager A {@link BuildToolConfigurationManager} instance.
     * @param blockPlacementQueueManager A {@link BlockPlacementQueue} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public BuildTool(
            @NotNull ComponentLogger logger,
            @NotNull BuildToolConfigurationManager buildToolConfigurationManager,
            @NotNull BlockPlacementQueue blockPlacementQueueManager, @NotNull HookManager hookManager) {
        this.logger = logger;
        this.buildToolConfigurationManager = buildToolConfigurationManager;
        this.blockPlacementQueueManager = blockPlacementQueueManager;
        this.hookManager = hookManager;
    }

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param buildToolConfigurationManager A {@link BuildToolConfigurationManager} instance.
     * @param blockPlacementQueueManager A {@link BlockPlacementQueue} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param itemStack The {@link ItemStack} for this build tool.
     */
    public BuildTool(
            @NotNull ComponentLogger logger,
            @NotNull BuildToolConfigurationManager buildToolConfigurationManager,
            @NotNull BlockPlacementQueue blockPlacementQueueManager,
            @NotNull HookManager hookManager,
            @NotNull ItemStack itemStack) {
        this.logger = logger;
        this.buildToolConfigurationManager = buildToolConfigurationManager;
        this.blockPlacementQueueManager = blockPlacementQueueManager;
        this.hookManager = hookManager;
        this.itemStack = itemStack;

        loadSettings();
    }

    /**
     * Create the {@link ItemStack} for the build tool.
     * The {@link ItemStack} will also be stored inside the BuildTool.
     * @return The {@link ItemStack} or null.
     */
    @Override
    public @Nullable ItemStack createTool() {
        @Nullable BuildToolConfig buildToolConfig = buildToolConfigurationManager.getConfiguration();
        if(buildToolConfig == null) {
            logger.error(AdventureUtil.deserialize("<red>Unable to create the build tool because the configuration is invalid.</red>"));
            return null;
        }

        List<TagResolver.Single> placeholderList = List.of(
                Placeholder.parsed("material", material != null ? material.name() : "None"),
                Placeholder.parsed("world1", position1 != null ? position1.getWorld().getName() : "None"),
                Placeholder.parsed("x1", position1 != null ? String.valueOf(position1.getBlockX()) : "None"),
                Placeholder.parsed("y1", position1 != null ? String.valueOf(position1.getBlockY()) : "None"),
                Placeholder.parsed("z1", position1 != null ? String.valueOf(position1.getBlockZ()) : "None"),
                Placeholder.parsed("world2", position2 != null ? position2.getWorld().getName() : "None"),
                Placeholder.parsed("x2", position2 != null ? String.valueOf(position2.getBlockX()) : "None"),
                Placeholder.parsed("y2", position2 != null ? String.valueOf(position2.getBlockY()) : "None"),
                Placeholder.parsed("z2", position2 != null ? String.valueOf(position2.getBlockZ()) : "None"));

        @NotNull Optional<@NotNull ItemStack> optionalItemStack = new ItemStackBuilder(logger)
                .fromItemStackConfig(buildToolConfig.item(), null, null, placeholderList)
                .buildItemStack();
        if(optionalItemStack.isEmpty()) {
            logger.error(AdventureUtil.deserialize("<red>Unable to create the build tool because the configuration is invalid.</red>"));
            return null;
        }

        ItemStack itemStack = optionalItemStack.get();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        pdc.set(BuildToolKeys.BUILD_TOOL.getKey(), PersistentDataType.INTEGER, 1);

        itemStack.setItemMeta(itemMeta);

        this.itemStack = itemStack;

        return itemStack;
    }

    /**
     * This tool doesn't support uses so just calls {@link #createTool()}.
     * @param uses The number of uses the tool has. (This value is ignored)
     * @return The {@link ItemStack} or null.
     */
    @Override
    public @Nullable ItemStack createTool(int uses) {
        return createTool();
    }

    /**
     * Save the tool's material and positions and update the item's lore.
     */
    @Override
    public void saveTool() {
        if(itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        if(!pdc.has(BuildToolKeys.BUILD_TOOL.getKey())) return; // ItemStack is not a Build Tool

        if(material != null) pdc.set(BuildToolKeys.MATERIAL.getKey(), PersistentDataType.STRING, material.toString());
        if(position1 != null) pdc.set(BuildToolKeys.POSITION1.getKey(), DataType.LOCATION, position1);
        if(position2 != null) pdc.set(BuildToolKeys.POSITION2.getKey(), DataType.LOCATION, position2);

        @Nullable BuildToolConfig buildToolConfig = buildToolConfigurationManager.getConfiguration();
        if(buildToolConfig != null) {
            List<TagResolver.Single> placeholderList = List.of(
                    Placeholder.parsed("material", material != null ? material.name() : "None"),
                    Placeholder.parsed("world1", position1 != null ? position1.getWorld().getName() : "None"),
                    Placeholder.parsed("x1", position1 != null ? String.valueOf(position1.getBlockX()) : "None"),
                    Placeholder.parsed("y1", position1 != null ? String.valueOf(position1.getBlockY()) : "None"),
                    Placeholder.parsed("z1", position1 != null ? String.valueOf(position1.getBlockZ()) : "None"),
                    Placeholder.parsed("world2", position2 != null ? position2.getWorld().getName() : "None"),
                    Placeholder.parsed("x2", position2 != null ? String.valueOf(position2.getBlockX()) : "None"),
                    Placeholder.parsed("y2", position2 != null ? String.valueOf(position2.getBlockY()) : "None"),
                    Placeholder.parsed("z2", position2 != null ? String.valueOf(position2.getBlockZ()) : "None"));

            List<String> stringLore = buildToolConfig.item().lore();
            List<Component> componentLore = stringLore.stream().map(loreLine -> AdventureUtil.deserialize(loreLine, placeholderList)).toList();

            itemMeta.lore(componentLore);
        } else {
            logger.error(AdventureUtil.deserialize("Unable to update build tool lore because of invalid build tool configuration."));
        }

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Load the tool's settings.
     */
    private void loadSettings() {
        if(itemStack == null) return;

        @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        @Nullable String materialName = pdc.get(BuildToolKeys.MATERIAL.getKey(), PersistentDataType.STRING);
        if(materialName != null) {
            material = Material.getMaterial(materialName);
        }
        position1 = pdc.get(BuildToolKeys.POSITION1.getKey(), DataType.LOCATION);
        position2 = pdc.get(BuildToolKeys.POSITION2.getKey(), DataType.LOCATION);
    }

    /**
     * Set the {@link ItemStack} for this build tool.
     * This will reload the settings from the ItemStack as well.
     * @param itemStack The {@link ItemStack} or null.
     */
    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;

        if(itemStack != null) loadSettings();
    }

    /**
     * Get the {@link ItemStack} for this build tool.
     * @return The {@link ItemStack} or null.
     */
    public @Nullable ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the {@link Material} to use for building.
     * @return The {@link Material} or null.
     */
    public @Nullable Material getMaterial() {
        return material;
    }

    /**
     * Set the {@link Material} to use for building.
     * @param material The {@link Material} or null.
     */
    public void setMaterial(@Nullable Material material) {
        this.material = material;

        saveTool();
    }

    /**
     * Get the {@link Location} for position 1.
     * @return The {@link Location} or null if not set.
     */
    public @Nullable Location getPosition1() {
        return position1;
    }

    /**
     * Set the {@link Location} for position 1.
     * @param position1 The {@link Location} or null.
     */
    public void setPosition1(@Nullable Location position1) {
        this.position1 = position1;

        saveTool();
    }

    /**
     * Get the {@link Location} for position 2.
     * @return The {@link Location} or null if not set.
     */
    public @Nullable Location getPosition2() {
        return position2;
    }

    /**
     * Set the {@link Location} for position 2.
     * @param position2 The {@link Location} or null.
     */
    public void setPosition2(@Nullable Location position2) {
        this.position2 = position2;

        saveTool();
    }

    /**
     * Queue block placement for the selected area.
     * @param player The {@link Player} that initiated the build.
     * @return true if a build is queued, false if not.
     */
    public boolean build(@NotNull Player player) {
        if(position1 == null || position2 == null) return false;
        if(!position1.getWorld().getName().equals(position2.getWorld().getName())) return false;
        if(material == null) return false;
        @Nullable BuildToolConfig buildToolConfig = buildToolConfigurationManager.getConfiguration();
        if(buildToolConfig == null) return false;
        if(buildToolConfig.restrictedWorlds().contains(position1.getWorld().getName())) return false;

        List<Location> locationList = getLocationsInArea(hookManager.getHook(WorldGuardHook.class), buildToolConfig.restrictedRegions(), position1, position2);
        if(locationList.isEmpty()) return false;

        blockPlacementQueueManager.queuePlacementData(new PlacementData(player, material, locationList));

        return true;
    }
}