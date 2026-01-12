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
package com.github.lukesky19.skyTools.mobTool.tool;

import com.github.lukesky19.skyTools.core.api.Tool;
import com.github.lukesky19.skyTools.core.configuration.data.Locale;
import com.github.lukesky19.skyTools.core.configuration.manager.LocaleManager;
import com.github.lukesky19.skyTools.core.integration.HookManager;
import com.github.lukesky19.skyTools.core.integration.ProtectionManager;
import com.github.lukesky19.skyTools.core.integration.impl.ItemsAdderHook;
import com.github.lukesky19.skyTools.core.integration.impl.RoseStackerHook;
import com.github.lukesky19.skyTools.mobTool.configuration.MobCaptureToolConfig;
import com.github.lukesky19.skyTools.mobTool.configuration.MobCaptureToolConfigurationManager;
import com.github.lukesky19.skyTools.mobTool.util.MobToolKeys;
import com.github.lukesky19.skyTools.mobTool.util.SpawnEggKeys;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.api.player.PlayerUtil;
import com.github.lukesky19.skylib.api.registry.RegistryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * This class represents an {@link ItemStack} that is a mob capture tool.
 */
public class MobCaptureTool extends Tool {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull HookManager hookManager;
    private final @NotNull ProtectionManager protectionManager;
    private final @NotNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager;

    private final @NotNull Player player;
    private @Nullable ItemStack itemStack;
    private @Nullable EquipmentSlot equipmentSlot = null;
    private int uses = -1;

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param localeManager A {@link LocaleManager} instance.
     * @param mobCaptureToolConfigurationManager A {@link MobCaptureToolConfigurationManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param protectionManager A {@link ProtectionManager} instance.
     * @param player The {@link Player} using the tool.
     */
    public MobCaptureTool(
            @NotNull ComponentLogger logger,
            @NotNull LocaleManager localeManager,
            @NotNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager,
            @NotNull HookManager hookManager,
            @NotNull ProtectionManager protectionManager,
            @NotNull Player player) {
        this.logger = logger;
        this.localeManager = localeManager;
        this.mobCaptureToolConfigurationManager = mobCaptureToolConfigurationManager;
        this.hookManager = hookManager;
        this.protectionManager = protectionManager;

        this.player = player;
    }

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param localeManager A {@link LocaleManager} instance.
     * @param mobCaptureToolConfigurationManager A {@link MobCaptureToolConfigurationManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param protectionManager A {@link ProtectionManager} instance.
     * @param player The {@link Player} using the tool.
     * @param itemStack The {@link ItemStack} for the tool.
     * @param equipmentSlot The {@link EquipmentSlot} the tool is in.
     */
    public MobCaptureTool(
            @NotNull ComponentLogger logger,
            @NotNull LocaleManager localeManager,
            @NotNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager,
            @NotNull HookManager hookManager,
            @NotNull ProtectionManager protectionManager,
            @NotNull Player player,
            @NotNull ItemStack itemStack,
            @NotNull EquipmentSlot equipmentSlot) {
        this.logger = logger;
        this.localeManager = localeManager;
        this.mobCaptureToolConfigurationManager = mobCaptureToolConfigurationManager;
        this.hookManager = hookManager;
        this.protectionManager = protectionManager;

        this.player = player;
        this.itemStack = itemStack;
        this.equipmentSlot = equipmentSlot;

        // Get the PersistentDataContainer
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        // Get the uses from the ItemStack
        uses = pdc.getOrDefault(MobToolKeys.USES.getKey(), PersistentDataType.INTEGER, -1);
    }

    /**
     * Create the {@link ItemStack} for the mob capture tool.
     * @apiNote Will use -1 for uses, meaning no usage limit. See {@link #createTool(int)}.
     * @return The {@link ItemStack} or null.
     */
    @Override
    public @Nullable ItemStack createTool() {
        return createTool(-1);
    }

    /**
     * Create the {@link ItemStack} for the mob capture tool.
     * @param uses The number of uses the tool has.
     * @return The {@link ItemStack} or null.
     */
    @Override
    public @Nullable ItemStack createTool(int uses) {
        uses = Math.max(-1, uses);

        @Nullable MobCaptureToolConfig mobCaptureToolConfig = mobCaptureToolConfigurationManager.getConfiguration();
        if(mobCaptureToolConfig == null) {
            logger.error(AdventureUtil.deserialize("<red>Unable to create the mob capture tool because the configuration is invalid.</red>"));
            return null;
        }

        List<TagResolver.Single> placeholderList = List.of(Placeholder.parsed("uses", String.valueOf(uses)));
        @NotNull Optional<@NotNull ItemStack> optionalItemStack = new ItemStackBuilder(logger)
                .fromItemStackConfig(mobCaptureToolConfig.item(), null, null, placeholderList)
                .buildItemStack();
        if(optionalItemStack.isEmpty()) {
            logger.error(AdventureUtil.deserialize("<red>Unable to create the mob capture tool because the configuration is invalid.</red>"));
            return null;
        }

        ItemStack itemStack = optionalItemStack.get();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        pdc.set(MobToolKeys.MOB_CAPTURE_TOOL.getKey(), PersistentDataType.INTEGER, 1);
        pdc.set(MobToolKeys.USES.getKey(), PersistentDataType.INTEGER, uses);

        itemStack.setItemMeta(itemMeta);

        this.itemStack = itemStack;

        return itemStack;
    }

    /**
     * Save the number of uses to the ItemStack.
     */
    @Override
    public void saveTool() {
        if(itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(MobToolKeys.USES.getKey(), PersistentDataType.INTEGER, uses);

        @Nullable MobCaptureToolConfig mobCaptureToolConfig = mobCaptureToolConfigurationManager.getConfiguration();
        if(mobCaptureToolConfig != null) {
            List<TagResolver.Single> placeholderList = List.of(Placeholder.parsed("uses", String.valueOf(uses)));

            List<String> stringLore = mobCaptureToolConfig.item().lore();
            List<Component> componentLore = stringLore.stream().map(loreLine -> AdventureUtil.deserialize(loreLine, placeholderList)).toList();

            itemMeta.lore(componentLore);
        } else {
            logger.error(AdventureUtil.deserialize("Unable to update mob capture tool lore because of invalid build tool configuration."));
            logger.info(AdventureUtil.deserialize("Uses are still accurately tracked, but the item's lore may not accurately display the uses."));
        }

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Turn the provided entity into a spawn egg if possible.
     * @param entity The {@link Entity}.
     */
    public void captureEntity(@NotNull Entity entity) {
        // Only allow capture of mobs
        if(!(entity instanceof Mob mob)) return;
        @NotNull Locale locale = localeManager.getConfiguration();

        // Only allow capture if the player can interact with entities
        if(!protectionManager.canInteractWithEntities(player, entity.getLocation())) {
            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.mobCaptureTool().noAccess()));
            return;
        }

        // Get the spawn egg
        ItemStack spawnEgg = getSpawnEgg(mob);
        // If null, return
        if(spawnEgg == null) return;

        // Give the player the spawn egg
        PlayerUtil.giveItem(player.getInventory(), spawnEgg, spawnEgg.getAmount(), player.getLocation());

        // Ignore tools with infinite uses
        if(uses == -1) return;

        // Log an error if EquipmentSlot is null
        if(equipmentSlot == null) {
            logger.warn(AdventureUtil.deserialize("Unable to save updated tool uses due to a null EquipmentSlot."));
            return;
        }

        // Update uses
        uses--;

        // Remove the item if uses are 0, otherwise save the uses to the ItemStack
        if(uses == 0) {
            player.getInventory().setItem(equipmentSlot, ItemType.AIR.createItemStack());

            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.mobCaptureTool().toolUsedUp()));
        } else {
            saveTool();
        }
    }

    /**
     * Get the spawn egg {@link ItemStack} for the entity.
     * @apiNote This will remove the entity if successful.
     * @param entity The {@link LivingEntity}.
     * @return The {@link ItemStack} or null.
     */
    private @Nullable ItemStack getSpawnEgg(@NotNull LivingEntity entity) {
        RoseStackerHook roseStackerHook = hookManager.getHook(RoseStackerHook.class);
        ItemsAdderHook itemsAdderHook = hookManager.getHook(ItemsAdderHook.class);

        // Get the EntityType
        EntityType entityType = entity.getType();

        // Get the entity's AI Status
        boolean ai = entity.hasAI();
        boolean aware = !(entity instanceof Mob mob) || mob.isAware();

        // Get the custom entity name or the default name
        @Nullable Component entityNameComponent = entity.customName();
        @NotNull String entityNameString = entityNameComponent != null ? AdventureUtil.serialize(entityNameComponent) : entity.getName();

        // The amount of mobs in the stack
        // Will return 1 if RoseStacker isn't hooked so a isHooked check isn't required.
        int stackSize = roseStackerHook.getStackSize(entity);

        // ItemAdders - Get the namespaced id for the entity (if any)
        @Nullable String namespacedId = null;
        if(itemsAdderHook.isHooked()) {
            namespacedId = itemsAdderHook.getCustomEntityNamespaceId(entity);
        }

        // Attempt to get the ItemStack for the spawn egg
        @Nullable ItemStack itemStack;
        if(namespacedId != null) {
            // Use ItemsAdderHook to attempt to get the custom spawn egg by the entity's namespaced id
            itemStack = itemsAdderHook.getCustomItem(namespacedId);

            // Use a normal spawn egg as a backup
            if(itemStack == null ) {
                itemStack = getSpawnEgg(entityType);
            }
        } else {
            // Use SkyTool's method as a base if not a stacked entity
            itemStack = getSpawnEgg(entityType);
        }

        // If no ItemStack was created, return null
        if(itemStack == null) return null;

        // Create a snapshot of the entity
        @Nullable EntitySnapshot entitySnapshot = entity.createSnapshot();
        if(entitySnapshot == null) return null;

        // Remove the entity
        entity.remove();

        // Get the ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the entity to spawn
        if(itemMeta instanceof SpawnEggMeta spawnEggMeta) {
            spawnEggMeta.setSpawnedEntity(entitySnapshot);
        } else {
            // Clear the name of the ItemStack (if any)
            itemMeta.customName(null);
        }

        // Set the lore
        List<Component> lore = List.of(
                AdventureUtil.deserialize("<gray>Entity Type: </gray>" + entityType.toString().toLowerCase()),
                AdventureUtil.deserialize("<gray>Entity Name: </gray>" + entityNameString),
                AdventureUtil.deserialize("<gray>Custom: </gray>" + (namespacedId != null)),
                AdventureUtil.deserialize("<gray>Has AI: </gray>" + (ai && aware)),
                AdventureUtil.deserialize("<gray>Amount: " + stackSize));
        itemMeta.lore(lore);

        // Store custom data
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

        persistentDataContainer.set(SpawnEggKeys.CUSTOM_SPAWN_EGG.getKey(), PersistentDataType.INTEGER, 1);
        persistentDataContainer.set(SpawnEggKeys.AI_STATUS.getKey(), PersistentDataType.BOOLEAN, ai);
        persistentDataContainer.set(SpawnEggKeys.AWARE_STATUS.getKey(), PersistentDataType.BOOLEAN, aware);
        persistentDataContainer.set(SpawnEggKeys.ROSESTACKER_STACK_SIZE.getKey(), PersistentDataType.INTEGER, stackSize);

        if(namespacedId != null) {
            persistentDataContainer.set(SpawnEggKeys.ITEMSADDER_NAMESPACED_ID.getKey(), PersistentDataType.STRING, namespacedId);
        }

        // Set the item meta
        itemStack.setItemMeta(itemMeta);

        // Return the ItemStack
        return itemStack;
    }

    /**
     * Get the spawn egg for the {@link EntityType}.
     * @param entityType The {@link EntityType}.
     * @return An {@link ItemStack} or null.
     */
    private @Nullable ItemStack getSpawnEgg(@NotNull EntityType entityType) {
        @Nullable ItemType itemType = getItemTypeFromEntityType(entityType);
        if(itemType == null) return null;

        return itemType.createItemStack();
    }

    /**
     * Get the {@link ItemType} for the spawn egg of the {@link EntityType}.
     * @param entityType The {@link EntityType}.
     * @return The {@link ItemType} or null if no spawn egg exists for that {@link EntityType}.
     */
    private @Nullable ItemType getItemTypeFromEntityType(@NotNull EntityType entityType) {
        return RegistryUtil.getItemType(logger, entityType.getKey().getKey() + "_spawn_egg").orElse(null);
    }
}
