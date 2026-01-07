package com.github.lukesky19.skyTools.core.integration.impl;

import com.github.lukesky19.skyHoppers.SkyHoppersAPI;
import com.github.lukesky19.skyTools.core.integration.Hook;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages interfacing with SkyHoppers.
 */
public class SkyHoppersHook implements Hook {
    private final @NotNull SkyPlugin plugin;
    private @Nullable SkyHoppersAPI skyHoppersAPI;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     */
    public SkyHoppersHook(@NotNull SkyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to get the {@link SkyHoppersAPI} from SkyPlayTime.
     */
    @Override
    public void initialize() {
        @Nullable Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("SkyPlayTime");
        if(plugin != null && plugin.isEnabled()) {
            @Nullable RegisteredServiceProvider<SkyHoppersAPI> rsp = this.plugin.getServer().getServicesManager().getRegistration(SkyHoppersAPI.class);
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
    public boolean isSkyHopper(@NotNull ItemStack itemStack) {
        if(skyHoppersAPI == null) return false;

        return skyHoppersAPI.isItemStackSkyHopper(itemStack);
    }
}
