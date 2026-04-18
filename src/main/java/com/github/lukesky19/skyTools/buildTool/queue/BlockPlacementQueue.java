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
package com.github.lukesky19.skyTools.buildTool.queue;

import com.github.lukesky19.skyTools.buildTool.util.PlacementData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class manages the queue for {@link PlacementData}.
 */
public class BlockPlacementQueue {
    private final @NonNull Queue<PlacementData> queue = new LinkedList<>();
    private boolean state = true;

    /**
     * Constructor
     */
    public BlockPlacementQueue() {}

    /**
     * Queue {@link PlacementData}.
     * @param placementData The {@link PlacementData}.
     */
    public void queuePlacementData(@NonNull PlacementData placementData) {
        if(!state) return;

        queue.add(placementData);
    }

    /**
     * Is the queue empty?
     * @return true if empty, false if not.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Get the next {@link PlacementData} without removing it from the queue.
     * @return The {@link PlacementData} or null if the queue is empty.
     */
    public @Nullable PlacementData peek() {
        return queue.peek();
    }

    /**
     * Remove the next {@link PlacementData} from the queue.
     */
    public void remove() {
        queue.remove();
    }

    /**
     * Clear the queue.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Set the queue's state.
     * @param state true will allow processing, false will clear the queue and nothing will be queued.
     */
    public void setState(boolean state) {
        this.state = state;

        if(!state) clear();
    }
}
