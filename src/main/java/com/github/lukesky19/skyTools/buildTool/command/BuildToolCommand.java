package com.github.lukesky19.skyTools.buildTool.command;

import com.github.lukesky19.skyTools.buildTool.tool.BuildToolManager;
import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
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
 * This class creates the build_tool command argument to register.
 */
public class BuildToolCommand {
    private final @NonNull LocaleManager localeManager;
    private final @NonNull BuildToolManager buildToolManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param buildToolManager A {@link BuildToolManager} instance.
     */
    public BuildToolCommand(
            @NonNull LocaleManager localeManager,
            @NonNull BuildToolManager buildToolManager) {
        this.localeManager = localeManager;
        this.buildToolManager = buildToolManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skytools command.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("build_tool")
                .requires(ctx -> ctx.getSender().hasPermission("skytools.commands.skytools.build_tool"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            Player player = playerResolver.resolve(ctx.getSource()).getFirst();

                            ItemStack itemStack = buildToolManager.createBuildTool();
                            if(itemStack == null) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.buildTool().configError()));
                                } else {
                                    sender.sendMessage(AdventureUtility.deserialize(locale.buildTool().configError()));
                                }

                                return 0;
                            }

                            PlayerUtil.giveItem(player.getInventory(), itemStack, 1, player.getLocation());

                            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.buildTool().toolGiven()));

                            if(sender instanceof Player) {
                                sender.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.buildTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            } else {
                                sender.sendMessage(AdventureUtility.deserialize(locale.buildTool().playerToolGiven(), List.of(Placeholder.parsed("player", player.getName()))));
                            }

                            return 1;
                        }))
                .build();
    }
}