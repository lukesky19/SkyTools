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
package com.github.lukesky19.skyTools.core.task;

import com.github.lukesky19.skyTools.buildTool.queue.BlockPlacementQueue;
import com.github.lukesky19.skyTools.buildTool.task.HighlightTask;
import com.github.lukesky19.skyTools.buildTool.task.PlacementTask;
import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.core.SkyTools;
import com.github.lukesky19.skyTools.core.configuration.data.Settings;
import com.github.lukesky19.skyTools.core.configuration.manager.SettingsManager;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.ProtectionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages the plugin's tasks.
 */
public class TaskManager {
    private final @NotNull SkyTools skyTools;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull BuildToolManager buildToolManager;
    private final @NotNull BlockPlacementQueue blockPlacementQueue;
    private final @NotNull ProtectionManager protectionManager;
    private final @NotNull HookManager hookManager;

    private @Nullable PlacementTask placementTask;
    private @Nullable HighlightTask highlightTask;

    /**
     * Constructor
     * @param skyTools A {@link SkyTools} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     * @param blockPlacementQueue A {@link BlockPlacementQueue} instance.
     * @param protectionManager A {@link ProtectionManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public TaskManager(
            @NotNull SkyTools skyTools,
            @NotNull SettingsManager settingsManager,
            @NotNull BuildToolManager buildToolManager,
            @NotNull BlockPlacementQueue blockPlacementQueue,
            @NotNull ProtectionManager protectionManager,
            @NotNull HookManager hookManager) {
        this.skyTools = skyTools;
        this.settingsManager = settingsManager;
        this.buildToolManager = buildToolManager;
        this.blockPlacementQueue = blockPlacementQueue;
        this.protectionManager = protectionManager;
        this.hookManager = hookManager;
    }

    /**
     * Start the tasks.
     */
    public void startTasks() {
        stopTasks();

        startPlacementTask();
        startHighlightTask();
    }

    /**
     * Stop the tasks.
     */
    public void stopTasks() {
        stopPlacementTask();
        stopHighlightTask();
    }

    /**
     * Start the {@link PlacementTask}.
     */
    private void startPlacementTask() {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            blockPlacementQueue.setState(false);
            return;
        }
        if(settings.placementFrequencyTicks() <= 0) {
            blockPlacementQueue.setState(false);
            return;
        }
        if(settings.placementsPerRun() <= 0) {
            blockPlacementQueue.setState(false);
            return;
        }

        blockPlacementQueue.setState(true);

        placementTask = new PlacementTask(skyTools, blockPlacementQueue, protectionManager, hookManager, settings.placementsPerRun());
        placementTask.runTaskTimer(skyTools, settings.placementFrequencyTicks(), settings.placementFrequencyTicks());
    }

    /**
     * Stop the {@link PlacementTask}.
     */
    private void stopPlacementTask() {
        if(placementTask == null) return;

        if(!placementTask.isCancelled()) {
            placementTask.cancel();
        }

        placementTask = null;
    }

    /**
     * Start the {@link HighlightTask}.
     */
    private void startHighlightTask() {
        highlightTask = new HighlightTask(skyTools, buildToolManager);
        highlightTask.runTaskTimer(skyTools, 20L, 20L);
    }

    /**
     * Stop the {@link HighlightTask}.
     */
    private void stopHighlightTask() {
        if(highlightTask == null) return;

        if(!highlightTask.isCancelled()) {
            highlightTask.cancel();
        }

        highlightTask = null;
    }
}
