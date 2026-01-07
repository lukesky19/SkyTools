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
package com.github.lukesky19.skyTools.buildTool.task;

import com.github.lukesky19.skyTools.buildTool.tool.BuildTool;
import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.lukesky19.skyTools.buildTool.util.PluginUtils.getHollowCube;

/**
 * This task is used to highlight the selected area when a player is holding a build wand.
 */
public class HighlightTask extends BukkitRunnable {
    private final SkyPlugin skyPlugin;
    private final @NotNull BuildToolManager buildToolManager;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     */
    public HighlightTask(SkyPlugin skyPlugin, @NotNull BuildToolManager buildToolManager) {
        this.skyPlugin = skyPlugin;
        this.buildToolManager = buildToolManager;
    }

    /**
     * Loop through online players and if the player is holding a build tool in their hand, highlight the selected area.
     */
    @Override
    public void run() {
        for(Player player : skyPlugin.getServer().getOnlinePlayers()) {
            @Nullable BuildTool buildTool = buildToolManager.getBuildTool(player.getInventory().getItemInMainHand());
            if(buildTool == null) continue;

            highlightArea(player, buildTool);
        }
    }

    /**
     * Highlight the selected area for the build wand.
     * @param player The {@link Player}.
     * @param buildTool The {@link BuildTool}.
     */
    private void highlightArea(@NotNull Player player, @NotNull BuildTool buildTool) {
        @Nullable Location position1 = buildTool.getPosition1();
        @Nullable Location position2 = buildTool.getPosition2();

        // Highlight position 1 with particles
        if(position1 != null) {
            getHollowCube(position1, position1, 0.5)
                    .forEach(location -> player.spawnParticle(
                            Particle.DUST,
                            location.clone(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            new Particle.DustOptions(Color.GREEN, 1)));
        }

        // Highlight position 2 with particles
        if(position2 != null) {
            getHollowCube(position2, position2, 0.5)
                    .forEach(location -> player.spawnParticle(
                            Particle.DUST,
                            location.clone(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            new Particle.DustOptions(Color.AQUA, 1)));
        }

        // Highlight the entire selected area with particles
        if (position1 != null && position2 != null && position1.getWorld().getName().equals(position2.getWorld().getName())) {
            getHollowCube(position1, position2, 0.5)
                    .forEach(location -> player.spawnParticle(
                            Particle.DUST,
                            location.clone(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            new Particle.DustOptions(Color.RED, 1)));
        }
    }
}
