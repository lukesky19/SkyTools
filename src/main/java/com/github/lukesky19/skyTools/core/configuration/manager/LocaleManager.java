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
package com.github.lukesky19.skyTools.core.configuration.manager;

import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.data.Settings;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NotNull SimpleConfigManager<Settings> settingsManager;
    private @NotNull Locale DEFAULT_LOCALE;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(@NotNull SkyPlugin plugin, @NotNull SimpleConfigManager<Settings> settingsManager) {
        super(plugin, Locale.class);
        this.settingsManager = settingsManager;

        createDefaultLocale();
    }

    /**
     * Gets the plugin's locale if not null or the default locale otherwise.
     * @return The plugin's locale if not null or the default locale otherwise.
     */
    @Override
    public @NotNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtil.deserialize("<red>Failed to load plugin's locale due to plugin settings being null.</red>"));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtil.deserialize("<red>Failed to load plugin's locale to use in settings.yml is null.</red>"));
            return;
        }

        String localeString = settings.locale();
        Path path = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));
        setConfigurationPath(path);

        super.loadConfiguration();
    }

    @Override
    public void saveBundledConfig() {
        Path path = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Update the locale configuration to the latest version if possible, or display an error.
     * @param locale The {@link Locale} to migrate.
     * @return The migrated {@link Locale} or null if migration failed.
     */
    @Override
    public @Nullable Locale migrateConfiguration(@NotNull Locale locale) {
        return locale;
    }

    /**
     * Validates if the locale is missing any strings.
     */
    @Override
    public boolean validateConfiguration(@Nullable Locale configuration) {
        if(configuration == null) return false;

        if(configuration.configVersion()  == null
                || configuration.prefix()  == null
                || configuration.reload()  == null
                || configuration.buildTool().toolGiven() == null
                || configuration.buildTool().playerToolGiven() == null
                || configuration.buildTool().configError()  == null
                || configuration.buildTool().positionOneInvalid()  == null
                || configuration.buildTool().positionTwoInvalid()  == null
                || configuration.buildTool().positionsDifferentWorlds() == null
                || configuration.buildTool().materialNotSet() == null
                || configuration.buildTool().playerLacksMaterials() == null
                || configuration.buildTool().worldNotAllowed() == null
                || configuration.buildTool().noLocationsFound() == null
                || configuration.buildTool().setPositionOne()  == null
                || configuration.buildTool().setPositionTwo()  == null
                || configuration.buildTool().noAccess()  == null
                || configuration.buildTool().buildQueued()  == null
                || configuration.mobCaptureTool().toolGiven() == null
                || configuration.mobCaptureTool().playerToolGiven() == null
                || configuration.mobCaptureTool().toolUsedUp() == null
                || configuration.mobCaptureTool().configError()  == null
                || configuration.mobCaptureTool().noAccess()  == null) {
            this.configuration = null;

            logger.error(AdventureUtil.deserialize("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtil.deserialize("You can regenerate your locale file by deleting it or adding the missing messages to resolve the issue."));

            return false;
        }

        return true;
    }

    /**
     * Creates the default locale.
     * It is created in a separate method so that the method can be minimized.
     */
    private void createDefaultLocale() {
        DEFAULT_LOCALE = new Locale(
                "1.0.0.0",
                "<#2FC211><bold>SkyTools</bold></#2FC211><gray> ▪ </gray>",
                List.of(
                        "<#2FC211>SkyTools is developed by <white><bold>lukeskywlker19</bold></white>.</#2FC211>",
                        "<#2FC211>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></#2FC211>",
                        " ",
                        "<#2FC211><bold>List of Commands:</bold></#2FC211>",
                        "<white>/<aqua>skytools</aqua> <yellow>help</yellow></white>",
                        "<white>/<aqua>skytools</aqua> <yellow>give build_tool <player_name></yellow></white>",
                        "<white>/<aqua>skytools</aqua> <yellow>give mob_capture_tool <player_name> [uses]</yellow></white>"),
                "<#2FC211>The plugin has reloaded successfully.</#2FC211>",
                new Locale.BuildToolMessages(
                        "<#2FC211>You have been given a build tool.</#2FC211>",
                        "<#2FC211>Gave player <white><player></white> a build tool.</#2FC211>",
                        "<red>Unable to create or update the build tool due to a configuration error.</red>",
                        "<red>Position 1 is not set.</red>",
                        "<red>Position 2 is not set.</red>",
                        "<red>The two positions must be set in the same world.</red>",
                        "<red>No material selected.</red>",
                        "<red>Your inventory doesn't contain any items of the selected material.</red>",
                        "<red>The build tool is not allowed in this world.</red>",
                        "<red>No valid locations found to place blocks at.</red>",
                        "<#2FC211>Set position 1 to <world> <x> <y> <z>.</#2FC211>",
                        "<#2FC211>Set position 2 to <world> <x> <y> <z>.</#2FC211>",
                        "<red>You do not have access to use the build tool here.</red>",
                        "<#2FC211>Queued block placements.</#2FC211>"),
                new Locale.MobCaptureToolMessages(
                        "<#2FC211>You have been given a mob capture tool.</#2FC211>",
                        "<#2FC211>Gave player <white><player></white> a mob capture tool.</#2FC211>",
                        "<dark_purple>POOF!</dark_purple> <#2FC211>Your mob capture tool ran out of uses.</#2FC211>",
                        "<red>Unable to create or update the mob capture tool due to a configuration error.</red>",
                        "<red>You do not have access to use the mob capture tool here.</red>"));
    }
}
