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

import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfigurationManager;
import com.github.lukesky19.skyTools.buildTool.queue.BlockPlacementQueue;
import com.github.lukesky19.skyTools.buildTool.util.BuildToolKeys;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages the creation and validation of {@link BuildTool}s.
 */
public class BuildToolManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull BuildToolConfigurationManager buildToolConfigurationManager;
    private final @NotNull BlockPlacementQueue blockPlacementQueue;
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param buildToolConfigurationManager A {@link BuildToolConfigurationManager} instance.
     * @param blockPlacementQueue A {@link BlockPlacementQueue} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public BuildToolManager(
            @NotNull ComponentLogger logger,
            @NotNull BuildToolConfigurationManager buildToolConfigurationManager,
            @NotNull BlockPlacementQueue blockPlacementQueue, @NotNull HookManager hookManager) {
        this.logger = logger;
        this.buildToolConfigurationManager = buildToolConfigurationManager;
        this.blockPlacementQueue = blockPlacementQueue;
        this.hookManager = hookManager;
    }

    /**
     * Create a new {@link BuildTool} {@link ItemStack}.
     * @return The {@link ItemStack} or null.
     */
    public @Nullable ItemStack createBuildTool() {
        BuildTool buildTool = new BuildTool(logger, buildToolConfigurationManager, blockPlacementQueue, hookManager);

        return buildTool.createTool();
    }

    /**
     * Get the {@link BuildTool} from the {@link ItemStack}.
     * @param itemStack The {@link ItemStack}.
     * @return The {@link BuildTool} or null if not a build tool.
     */
    public @Nullable BuildTool getBuildTool(@NotNull ItemStack itemStack) {
        if(!isBuildTool(itemStack)) return null;

        return new BuildTool(logger, buildToolConfigurationManager, blockPlacementQueue, hookManager, itemStack);
    }

    /**
     * Is the {@link ItemStack} a {@link BuildTool}?
     * @param itemStack The {@link ItemStack}.
     * @return true if a build tool, or false if not.
     */
    public boolean isBuildTool(@NotNull ItemStack itemStack) {
        @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        return pdc.has(BuildToolKeys.BUILD_TOOL.getKey());
    }
}
