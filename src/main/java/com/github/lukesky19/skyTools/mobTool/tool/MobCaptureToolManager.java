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
package com.github.lukesky19.skyTools.mobTool.tool;

import com.github.lukesky19.skyTools.buildTool.tool.BuildTool;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.ProtectionManager;
import com.github.lukesky19.skyTools.mobTool.configuration.MobCaptureToolConfigurationManager;
import com.github.lukesky19.skyTools.mobTool.util.MobToolKeys;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages the creation and validation of {@link MobCaptureTool}s.
 */
public class MobCaptureToolManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull HookManager hookManager;
    private final @NotNull ProtectionManager protectionManager;
    private final @NotNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager;

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param localeManager A {@link LocaleManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param protectionManager A {@link ProtectionManager} instance.
     * @param mobCaptureToolConfigurationManager A {@link MobCaptureToolConfigurationManager} instance.
     */
    public MobCaptureToolManager(
            @NotNull ComponentLogger logger,
            @NotNull LocaleManager localeManager,
            @NotNull HookManager hookManager,
            @NotNull ProtectionManager protectionManager,
            @NotNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager) {
        this.logger = logger;
        this.localeManager = localeManager;
        this.hookManager = hookManager;
        this.protectionManager = protectionManager;
        this.mobCaptureToolConfigurationManager = mobCaptureToolConfigurationManager;
    }

    /**
     * Create a new {@link BuildTool} {@link ItemStack}.
     * @param player The {@link Player} the tool is being used by.
     * @param uses The number of uses the mob capture tool has. Use -1 for infinite.
     * @return The {@link ItemStack} or null.
     */
    public @Nullable ItemStack createMobCaptureTool(@NotNull Player player, int uses) {
        MobCaptureTool mobCaptureTool = new MobCaptureTool(logger, localeManager, mobCaptureToolConfigurationManager, hookManager, protectionManager, player);

        return mobCaptureTool.createTool(uses);
    }

    /**
     * Get the {@link MobCaptureTool} for the {@link ItemStack}.
     * @param player The {@link Player} the tool is being used by.
     * @param itemStack The {@link ItemStack}.
     * @param equipmentSlot The {@link EquipmentSlot} the item is in.
     * @return The {@link MobCaptureTool} or null if not a mob capture tool.
     */
    public @Nullable MobCaptureTool getMobCaptureTool(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot) {
        if(!isMobCaptureTool(itemStack)) return null;

        return new MobCaptureTool(logger, localeManager, mobCaptureToolConfigurationManager, hookManager, protectionManager, player, itemStack, equipmentSlot);
    }

    /**
     * Is the {@link ItemStack} a {@link MobCaptureTool}?
     * @param itemStack The {@link ItemStack}.
     * @return true if a mob capture tool, or false if not.
     */
    public boolean isMobCaptureTool(@NotNull ItemStack itemStack) {
        @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        return pdc.has(MobToolKeys.MOB_CAPTURE_TOOL.getKey());
    }
}
