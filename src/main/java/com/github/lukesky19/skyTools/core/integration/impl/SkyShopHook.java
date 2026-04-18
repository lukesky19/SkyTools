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
package com.github.lukesky19.skyTools.core.integration.impl;

import com.github.lukesky19.skyHoppers.SkyHoppersAPI;
import com.github.lukesky19.skyTools.core.integration.Hook;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

/**
 * This class manages interfacing with SkyShop.
 */
public class SkyShopHook implements Hook {
    private final @NonNull SkyPlugin plugin;
    private boolean hooked = false;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     */
    public SkyShopHook(@NonNull SkyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to get the {@link SkyHoppersAPI} from SkyPlayTime.
     */
    @Override
    public void initialize() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("SkyPlayTime");

        hooked = plugin != null && plugin.isEnabled();
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return hooked;
    }
}
