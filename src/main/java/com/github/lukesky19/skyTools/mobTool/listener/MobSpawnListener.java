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
import com.github.lukesky19.skyTools.mobTool.util.MobToolKeys;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Listens for when a player attempts to use a custom spawn egg from the mob capture tool.
 */
public class MobSpawnListener implements Listener {
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param hookManager A {@link HookManager} instance.
     */
    public MobSpawnListener(@NotNull HookManager hookManager) {
        this.hookManager = hookManager;
    }

    /**
     * Listens for when a player clicks with an item from a mob capture tool.
     * @param playerInteractEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnEggClick(@NotNull PlayerInteractEvent playerInteractEvent) {
        ItemsAdderHook itemsAdderHook = hookManager.getHook(ItemsAdderHook.class);
        if(!itemsAdderHook.isHooked()) return;
        @Nullable Location location = playerInteractEvent.getInteractionPoint();
        if(location == null) return;
        @Nullable ItemStack itemStack = playerInteractEvent.getItem();
        if(itemStack == null) return;
        if(!itemStack.getType().toString().toLowerCase().contains("spawn_egg")) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        @Nullable String namespacedId = persistentDataContainer.get(MobToolKeys.ITEMSADDER_NAMESPACED_ID.getKey(), PersistentDataType.STRING);
        if(namespacedId == null) return;

        playerInteractEvent.setCancelled(true);

        boolean result = itemsAdderHook.spawnCustomEntity(location, namespacedId);

        if(result) {
            ItemStack removeStack = itemStack.clone();
            removeStack.setAmount(1);

            playerInteractEvent.getPlayer().getInventory().removeItem(removeStack);
        } else {
            playerInteractEvent.setCancelled(false);
        }
    }
}
