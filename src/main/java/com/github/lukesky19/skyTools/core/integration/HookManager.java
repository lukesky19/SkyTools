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
package com.github.lukesky19.skyTools.core.integration;

import com.github.lukesky19.skyTools.core.integration.impl.*;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages hooks.
 */
public class HookManager {
    private final @NonNull Map<Class<?>, Hook> hooks = new HashMap<>();

    /**
     * Constructor
     * @param plugin A {@link JavaPlugin} instance.
     */
    public HookManager(@NonNull SkyPlugin plugin) {
        BentoBoxHook bentoBoxHook = new BentoBoxHook(plugin);
        registerHook(BentoBoxHook.class, bentoBoxHook);

        ItemsAdderHook itemsAdderHook = new ItemsAdderHook(plugin);
        registerHook(ItemsAdderHook.class, itemsAdderHook);

        RoseStackerHook roseStackerHook = new RoseStackerHook(plugin);
        registerHook(RoseStackerHook.class, roseStackerHook);

        SkyHoppersHook skyHoppersHook = new SkyHoppersHook(plugin);
        registerHook(SkyHoppersHook.class, skyHoppersHook);

        SkyShopHook skyShopHook = new SkyShopHook(plugin);
        registerHook(SkyShopHook.class, skyShopHook);

        WorldGuardHook worldGuardHook = new WorldGuardHook(plugin);
        registerHook(WorldGuardHook.class, worldGuardHook);
    }

    /**
     * Register a hook.
     * @param hookClass The class.
     * @param hook The class instance.
     * @param <T> Parameter for any class that extends {@link Hook}.
     */
    public <T extends Hook> void registerHook(@NonNull Class<T> hookClass, @NonNull Hook hook) {
        hooks.put(hookClass, hook);
        hook.initialize();
    }

    /**
     * Get a hook.
     * @param hookClass The class.
     * @return The class instance.
     * @param <T> Parameter for any class that extends {@link Hook}.
     */
    public @NonNull <T extends Hook> T getHook(@NonNull Class<T> hookClass) {
        return hookClass.cast(hooks.get(hookClass));
    }
}
