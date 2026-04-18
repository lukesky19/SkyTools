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
package com.github.lukesky19.skyTools.mobTool.command;

import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.mobTool.tool.MobCaptureToolManager;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * This class creates the mob_capture_tool command argument to register.
 */
public class MobCaptureToolCommand {
    private final @NonNull LocaleManager localeManager;
    private final @NonNull MobCaptureToolManager mobCaptureToolManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param mobCaptureToolManager A {@link MobCaptureToolManager} instance.
     */
    public MobCaptureToolCommand(
            @NonNull LocaleManager localeManager,
            @NonNull MobCaptureToolManager mobCaptureToolManager) {
        this.localeManager = localeManager;
        this.mobCaptureToolManager = mobCaptureToolManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("mob_capture_tool")
                .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools.mob_capture_tool"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("uses", IntegerArgumentType.integer(-1, Integer.MAX_VALUE))
                                .executes(ctx -> {
                                    Locale locale = localeManager.getConfiguration();
                                    CommandSender sender = ctx.getSource().getSender();
                                    PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                    Player player = playerResolver.resolve(ctx.getSource()).getFirst();
                                    int uses = ctx.getArgument("uses", Integer.class);

                                    ItemStack itemStack = mobCaptureToolManager.createMobCaptureTool(player, uses);
                                    if(itemStack == null) {
                                        if(sender instanceof Player) {
                                            sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().configError()));
                                        } else {
                                            sender.sendMessage(AdventureUtility.deserialize(locale.mobCaptureTool().configError()));
                                        }

                                        return 0;
                                    }

                                    PlayerUtil.giveItem(player.getInventory(), itemStack, 1, player.getLocation());

                                    player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().toolGiven()));

                                    if(sender instanceof Player) {
                                        sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                                    } else {
                                        sender.sendMessage(AdventureUtility.deserialize(locale.mobCaptureTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                                    }

                                    return 1;
                                }))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            Player player = playerResolver.resolve(ctx.getSource()).getFirst();

                            ItemStack itemStack = mobCaptureToolManager.createMobCaptureTool(player, -1);
                            if(itemStack == null) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().configError()));
                                } else {
                                    sender.sendMessage(AdventureUtility.deserialize(locale.mobCaptureTool().configError()));
                                }

                                return 0;
                            }

                            PlayerUtil.giveItem(player.getInventory(), itemStack, 1, player.getLocation());

                            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().toolGiven()));

                            if(sender instanceof Player) {
                                sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            } else {
                                sender.sendMessage(AdventureUtility.deserialize(locale.mobCaptureTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            }

                            return 1;
                        }))
                .build();
    }
}