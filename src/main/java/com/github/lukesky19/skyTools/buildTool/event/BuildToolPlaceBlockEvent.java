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
package com.github.lukesky19.skyTools.buildTool.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a build tool places a block for a player.
 */
public class BuildToolPlaceBlockEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final @NotNull Player player;
    private final @NotNull Block block;
    private final @NotNull BlockState blockState;

    /**
     * Constructor
     * @param player The {@link Player} purchasing the item.
     * @param block The {@link Block} that was modified.
     * @param blockState The {@link BlockState} from before the block was modified.
     */
    public BuildToolPlaceBlockEvent(
            @NotNull Player player,
            @NotNull Block block,
            @NotNull BlockState blockState) {
        this.player = player;
        this.block = block;
        this.blockState = blockState;
    }

    /**
     * Get the {@link Player} making the purchase.
     * @return The {@link Player} making the purchase.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Get the {@link Block} that was modified.
     * @return The {@link Block} that was modified.
     */
    public @NotNull Block getBlock() {
        return block;
    }

    /**
     * Get the {@link BlockState} of the block before it was modified.
     * @apiNote This is a snapshot of the BlockState, not a live BlockState.
     * @return The {@link BlockState} of the block before it was modified.
     */
    public @NotNull BlockState getBlockState() {
        return blockState;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}.
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
