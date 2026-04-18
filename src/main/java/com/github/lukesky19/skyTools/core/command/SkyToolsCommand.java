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
package com.github.lukesky19.skyTools.core.command;

import com.github.lukesky19.skyTools.buildTool.command.BuildToolCommand;
import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.mobTool.command.MobCaptureToolCommand;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class creates the main /skytools command to register.
 */
public class SkyToolsCommand {
    private final @NonNull SkyPlugin skyPlugin;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull BuildToolManager buildToolManager;
    private final @NonNull MobCaptureToolManager mobCaptureToolManager;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     */
    public SkyToolsCommand(
            @NonNull SkyPlugin skyPlugin,
            @NonNull LocaleManager localeManager,
            @NonNull BuildToolManager buildToolManager,
            @NonNull MobCaptureToolManager mobCaptureToolManager) {
        this.skyPlugin = skyPlugin;
        this.localeManager = localeManager;
        this.buildToolManager = buildToolManager;
        this.mobCaptureToolManager = mobCaptureToolManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("skytools")
                .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools"))
                .then(Commands.literal("help")
                        .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools.help"))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();

                            CommandSender sender = ctx.getSource().getSender();
                            for(String message : locale.help()) {
                                sender.sendMessage(AdventureUtility.deserialize(message));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("reload")
                        .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools.reload"))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();

                            skyPlugin.reload();

                            if(sender instanceof Player) {
                                sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.reload()));
                            } else {
                                sender.sendMessage(AdventureUtility.deserialize(locale.reload()));
                            }

                            return 1;
                        }))
                .then(new BuildToolCommand(localeManager, buildToolManager).createCommand())
                .then(new MobCaptureToolCommand(localeManager, mobCaptureToolManager).createCommand())
                .build();
    }
}