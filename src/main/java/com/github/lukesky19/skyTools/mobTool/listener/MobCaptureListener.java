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

import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureTool;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * This class listens for the usage of a mob capture tool.
 */
public class MobCaptureListener implements Listener {
    private final @NonNull MobCaptureToolManager mobCaptureToolManager;

    /**
     * Constructor
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     */
    public MobCaptureListener(@NonNull MobCaptureToolManager mobCaptureToolManager) {
        this.mobCaptureToolManager = mobCaptureToolManager;
    }

    /**
     * Listens for when a player clicks an entity with a mob capture tool.
     * @param playerInteractEntityEvent A {@link PlayerInteractEntityEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player = playerInteractEntityEvent.getPlayer();
        Entity entity = playerInteractEntityEvent.getRightClicked();

        EquipmentSlot equipmentSlot = playerInteractEntityEvent.getHand();
        ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
        if(itemStack.isEmpty()) return;
        MobCaptureTool mobCaptureTool = mobCaptureToolManager.getMobCaptureTool(player, itemStack, equipmentSlot);
        if(mobCaptureTool == null) return;

        playerInteractEntityEvent.setCancelled(true);

        mobCaptureTool.captureEntity(entity);
    }
}
