package com.github.lukesky19.skyTools.buildTool.command;

import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.player.PlayerUtil;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class creates the build_tool command argument to register.
 */
public class BuildToolCommand {
    private final @NotNull LocaleManager localeManager;
    private final @NotNull BuildToolManager buildToolManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     */
    public BuildToolCommand(
            @NotNull LocaleManager localeManager,
            @NotNull BuildToolManager buildToolManager) {
        this.localeManager = localeManager;
        this.buildToolManager = buildToolManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("build_tool")
                .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools.build_tool"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            Player player = playerResolver.resolve(ctx.getSource()).getFirst();

                            @Nullable ItemStack itemStack = buildToolManager.createBuildTool();
                            if(itemStack == null) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().configError()));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.buildTool().configError()));
                                }

                                return 0;
                            }

                            PlayerUtil.giveItem(player.getInventory(), itemStack, 1, player.getLocation());

                            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().toolGiven()));

                            if(sender instanceof Player) {
                                sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.buildTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            } else {
                                sender.sendMessage(AdventureUtil.deserialize(locale.buildTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            }

                            return 1;
                        }))
                .build();
    }
}
