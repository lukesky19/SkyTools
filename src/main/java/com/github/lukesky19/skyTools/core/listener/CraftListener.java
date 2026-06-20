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
package com.github.lukesky19.skyTools.core.listener;

import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * This class listens to when an item is crafted prevents the crafting if a tool is in the crafting matrix.
 */
public class CraftListener implements Listener {
    private final @NonNull BuildToolManager buildToolManager;
    private final @NonNull MobCaptureToolManager mobCaptureToolManager;

    /**
     * Constructor.
     * @param buildToolManager A {@link BuildToolManager} instance.
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     */
    public CraftListener(
            @NonNull BuildToolManager buildToolManager,
            @NonNull MobCaptureToolManager mobCaptureToolManager) {
        this.buildToolManager = buildToolManager;
        this.mobCaptureToolManager = mobCaptureToolManager;
    }

    /**
     * Listens for an {@link CraftItemEvent} and cancels it if the crafting matrix contains any tools.
     * @param craftItemEvent An {@link CraftItemEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent craftItemEvent) {
        CraftingInventory craftingInventory = craftItemEvent.getInventory();
        ItemStack[] inputs = craftingInventory.getMatrix();

        if(Arrays.stream(inputs).anyMatch(itemStack ->
                buildToolManager.isBuildTool(itemStack) || mobCaptureToolManager.isMobCaptureTool(itemStack))) {
            craftItemEvent.setCancelled(true);
        }
    }
}