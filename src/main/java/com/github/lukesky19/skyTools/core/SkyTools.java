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
package com.github.lukesky19.skyTools.core;

import com.github.lukesky19.skyTools.buildTool.configuration.BuildToolConfigurationManager;
import com.github.lukesky19.skyTools.buildTool.listener.BuildToolListener;
import com.github.lukesky19.skyTools.buildTool.queue.BlockPlacementQueue;
import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.core.command.SkyToolsCommand;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.core.configuration.manager.SettingsManager;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.ProtectionManager;
import com.github.lukesky19.skyTools.core.integration.impl.SkyShopHook;
import com.github.lukesky19.skyTools.core.listener.ItemSoldListener;
import com.github.lukesky19.skyTools.core.task.TaskManager;
import com.github.lukesky19.skyTools.mobTool.configuration.MobCaptureToolConfigurationManager;
import com.github.lukesky19.skyTools.mobTool.listener.MobCaptureListener;
import com.github.lukesky19.skyTools.mobTool.listener.MobSpawnListener;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
 * The main plugin's class.
 */
public final class SkyTools extends SkyPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private BuildToolConfigurationManager buildToolConfigurationManager;
    private MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager;
    private TaskManager taskManager;

    /**
     * Constructor
     */
    public SkyTools() {}

    /**
     * Setup plugin data.
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;

        // Settings
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);

        // Integration/Hooks
        HookManager hookManager = new HookManager(this);
        ProtectionManager protectionManager = new ProtectionManager(hookManager);

        // Build Tool
        buildToolConfigurationManager = new BuildToolConfigurationManager(this);
        BlockPlacementQueue blockPlacementQueue = new BlockPlacementQueue();
        BuildToolManager buildToolManager = new BuildToolManager(this.getComponentLogger(), buildToolConfigurationManager, blockPlacementQueue, hookManager);

        // Mob Capture Tool
        mobCaptureToolConfigurationManager = new MobCaptureToolConfigurationManager(this);
        MobCaptureToolManager mobCaptureToolManager = new MobCaptureToolManager(this.getComponentLogger(), localeManager, hookManager, protectionManager, mobCaptureToolConfigurationManager);

        taskManager = new TaskManager(this, settingsManager, buildToolManager, blockPlacementQueue, protectionManager, hookManager);

        // Register Commands
        SkyToolsCommand skyToolsCommand = new SkyToolsCommand(this, localeManager, buildToolManager, mobCaptureToolManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands ->
                        commands.registrar().register(skyToolsCommand.createCommand(),
                                "Command to manage and use the SkyTools plugin.",
                                List.of("tools", "tools")));

        // Register Listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new BuildToolListener(localeManager, buildToolConfigurationManager, buildToolManager, hookManager), this);
        pluginManager.registerEvents(new MobCaptureListener(mobCaptureToolManager), this);
        if(hookManager.getHook(SkyShopHook.class).isHooked()) {
            pluginManager.registerEvents(new ItemSoldListener(buildToolManager, mobCaptureToolManager), this);
        }
        pluginManager.registerEvents(new MobSpawnListener(mobCaptureToolManager, hookManager), this);

        // Reload the plugin
        reload();
    }

    /**
     * Method to reload the plugin.
     */
    @Override
    public void reload() {
        settingsManager.loadConfiguration();
        localeManager.loadConfiguration();
        buildToolConfigurationManager.loadConfiguration();
        mobCaptureToolConfigurationManager.loadConfiguration();

        taskManager.stopTasks();
        taskManager.startTasks();
    }

    /**
     * Cleanup plugin data.
     */
    @Override
    public void onDisable() {
        if(taskManager != null) {
            taskManager.stopTasks();
        }
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if(skyLib != null && skyLib.isEnabled()) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 4) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.deserialize("SkyLib Version 1.4.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}