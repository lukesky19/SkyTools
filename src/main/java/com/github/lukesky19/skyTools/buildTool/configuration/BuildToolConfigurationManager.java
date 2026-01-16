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
package com.github.lukesky19.skyTools.buildTool.configuration;

import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the build tool configuration.
 */
public class BuildToolConfigurationManager extends SimpleConfigManager<BuildToolConfig> {
    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     */
    public BuildToolConfigurationManager(@NotNull SkyPlugin plugin) {
        super(plugin, Path.of(plugin.getDataFolder() + File.separator + "tools" + File.separator + "build_tool.yml"), BuildToolConfig.class);
    }

    /**
     * Save the bundled build tool configuration if it doesn't exist.
     */
    @Override
    protected void saveBundledConfig() {
        plugin.saveResource("tools" + File.separator + "build_tool.yml", false);
    }

    /**
     * Migrate the configuration. There is currently no migration needed so this just returns the passed configuration.
     * @param buildToolConfig The {@link BuildToolConfig}.
     * @return The {@link BuildToolConfig}.
     */
    @Override
    public @Nullable BuildToolConfig migrateConfiguration(@NotNull BuildToolConfig buildToolConfig) {
        return buildToolConfig;
    }

    /**
     * Validates the configuration. This always returns true.
     * @return Always returns true.
     */
    @Override
    public boolean validateConfiguration(@Nullable BuildToolConfig buildToolConfig) {
        return true;
    }
}
