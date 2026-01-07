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
package com.github.lukesky19.skyTools.buildTool.listener;

import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfig;
import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfigurationManager;
import com.github.lukesky19.skyTools.buildTool.tool.BuildTool;
import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.buildTool.util.PluginUtils;
import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.impl.SkyHoppersHook;
import com.github.lukesky19.skyTools.core.integration.impl.WorldGuardHook;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class listens for the usage of a build tool.
 */
public class BuildToolListener implements Listener {
    private final @NotNull LocaleManager localeManager;
    private final @NotNull BuildToolConfigurationManager buildToolConfigurationManager;
    private final @NotNull BuildToolManager buildToolManager;
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param buildToolConfigurationManager A {@link BuildToolConfigurationManager} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public BuildToolListener(
            @NotNull LocaleManager localeManager,
            @NotNull BuildToolConfigurationManager buildToolConfigurationManager,
            @NotNull BuildToolManager buildToolManager,
            @NotNull HookManager hookManager) {
        this.localeManager = localeManager;
        this.buildToolConfigurationManager = buildToolConfigurationManager;
        this.buildToolManager = buildToolManager;
        this.hookManager = hookManager;
    }

    /**
     * Listens for when a player clicks a block with a build tool.
     * @param playerInteractEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        EquipmentSlot hand = playerInteractEvent.getHand();
        if(hand == null || !hand.equals(EquipmentSlot.HAND)) return;

        @Nullable ItemStack itemStack = playerInteractEvent.getItem();
        if(itemStack == null) return;
        if(!(itemStack.getItemMeta() instanceof BlockDataMeta)) return;

        @Nullable BuildToolConfig buildToolConfig = buildToolConfigurationManager.getConfiguration();
        if(buildToolConfig == null) return;

        @Nullable BuildTool buildTool = buildToolManager.getBuildTool(itemStack);
        if(buildTool == null) return;

        @Nullable Block block = playerInteractEvent.getClickedBlock();
        if(block == null) return;

        Location location = block.getLocation();
        Action action = playerInteractEvent.getAction();

        playerInteractEvent.setCancelled(true);

        @NotNull Locale locale = localeManager.getConfiguration();
        if(action.isLeftClick()) {
            if(player.isSneaking()) {
                if(buildTool.getPosition1() == null) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().positionOneInvalid()));
                    return;
                }
                if(buildTool.getPosition2() == null) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().positionTwoInvalid()));
                    return;
                }

                if(buildTool.build(player)) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().buildQueued()));
                } else {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().noAccess()));
                }
            } else {
                if(PluginUtils.isLocationDisallowed(hookManager.getHook(WorldGuardHook.class), buildToolConfig.restrictedWorlds(), buildToolConfig.restrictedRegions(), location)) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().noAccess()));
                    return;
                }

                buildTool.setPosition1(location);

                List<TagResolver.Single> placeholderList = List.of(
                        Placeholder.parsed("world", location.getWorld().getName()),
                        Placeholder.parsed("x", String.valueOf(location.getBlockX())),
                        Placeholder.parsed("y", String.valueOf(location.getBlockY())),
                        Placeholder.parsed("z", String.valueOf(location.getBlockZ())));

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().setPositionOne(), placeholderList));

                buildTool.saveTool();
            }
        } else if(action.isRightClick()) {
            if(player.isSneaking()) {
                if(buildTool.getPosition1() == null) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().positionOneInvalid()));
                    return;
                }
                if(buildTool.getPosition2() == null) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().positionTwoInvalid()));
                    return;
                }

                if(buildTool.build(player)) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().buildQueued()));
                } else {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().noAccess()));
                }
            } else {
                if(PluginUtils.isLocationDisallowed(hookManager.getHook(WorldGuardHook.class), buildToolConfig.restrictedWorlds(), buildToolConfig.restrictedRegions(), location)) {
                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().noAccess()));
                    return;
                }

                buildTool.setPosition2(location);

                List<TagResolver.Single> placeholderList = List.of(
                        Placeholder.parsed("world", location.getWorld().getName()),
                        Placeholder.parsed("x", String.valueOf(location.getBlockX())),
                        Placeholder.parsed("y", String.valueOf(location.getBlockY())),
                        Placeholder.parsed("z", String.valueOf(location.getBlockZ())));

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().setPositionTwo(), placeholderList));

                buildTool.saveTool();
            }
        }
    }

    /**
     * This listener updates the select material for the build tool.
     * @param inventoryClickEvent An {@link InventoryClickEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSelectMaterial(InventoryClickEvent inventoryClickEvent) {
        if(!(inventoryClickEvent.getWhoClicked() instanceof Player)) return;
        if(!(inventoryClickEvent.getClickedInventory() instanceof PlayerInventory)) return;
        @NotNull ItemStack cursorItemStack = inventoryClickEvent.getCursor();
        if(cursorItemStack.isEmpty()) return;
        @Nullable ItemStack clickedItemStack = inventoryClickEvent.getCurrentItem();
        if(clickedItemStack == null || clickedItemStack.isEmpty()) return;
        @Nullable BuildTool buildTool = buildToolManager.getBuildTool(clickedItemStack);
        if(buildTool == null) return;

        inventoryClickEvent.setCancelled(true);

        // Don't use SkyHoppers
        SkyHoppersHook skyHoppersHook = hookManager.getHook(SkyHoppersHook.class);
        if(skyHoppersHook.isHooked()) {
            if(skyHoppersHook.isSkyHopper(cursorItemStack)) return;
        }

        buildTool.setMaterial(cursorItemStack.getType());

        buildTool.saveTool();
    }
}
