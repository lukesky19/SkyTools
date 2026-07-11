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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages interfacing with SkyHoppers.
 */
public class SkyHoppersHook implements Hook {
    private final @NonNull SkyPlugin plugin;
    private @Nullable SkyHoppersAPI skyHoppersAPI;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     */
    public SkyHoppersHook(@NonNull SkyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to get the {@link SkyHoppersAPI} from SkyPlayTime.
     */
    @Override
    public void initialize() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("SkyHoppers");
        if(plugin != null && plugin.isEnabled()) {
            RegisteredServiceProvider<SkyHoppersAPI> rsp = this.plugin.getServer().getServicesManager().getRegistration(SkyHoppersAPI.class);
            if(rsp != null) {
                skyHoppersAPI = rsp.getProvider();
            }
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return skyHoppersAPI != null;
    }

    /**
     * Is the {@link ItemStack} provided a SkyHopper?
     * @param itemStack The {@link ItemStack}.
     * @return true if a SkyHopper, false if not.
     */
    public boolean isSkyHopper(@NonNull ItemStack itemStack) {
        if(skyHoppersAPI == null) return false;

        return skyHoppersAPI.isItemStackSkyHopper(itemStack);
    }
}
