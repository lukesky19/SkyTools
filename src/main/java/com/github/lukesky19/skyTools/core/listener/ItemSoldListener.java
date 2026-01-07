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
import com.github.lukesky19.skyshop.event.ItemSoldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class listens to when an item is sold through SkyShop and prevents the selling of tools.
 */
public class ItemSoldListener implements Listener {
    private final @NotNull BuildToolManager buildToolManager;
    private final @NotNull MobCaptureToolManager mobCaptureToolManager;

    /**
     * Constructor.
     * @param buildToolManager A {@link BuildToolManager} instance.
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     */
    public ItemSoldListener(
            @NotNull BuildToolManager buildToolManager,
            @NotNull MobCaptureToolManager mobCaptureToolManager) {
        this.buildToolManager = buildToolManager;
        this.mobCaptureToolManager = mobCaptureToolManager;
    }

    /**
     * Listens for an {@link ItemSoldEvent} and cancels it if the item is any tool.
     * @param itemSoldEvent An {@link ItemSoldEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemSold(ItemSoldEvent itemSoldEvent) {
        ItemStack itemStack = itemSoldEvent.getItemStack();

        if(buildToolManager.isBuildTool(itemStack)) {
            itemSoldEvent.setCancelled(true);
            return;
        }

        if(mobCaptureToolManager.isMobCaptureTool(itemStack)) {
            itemSoldEvent.setCancelled(true);
        }
    }
}