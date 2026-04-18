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
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
import com.github.lukesky19.skylib.paper.api.registry.RegistryUtil;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * This class represents an {@link ItemStack} that is a mob capture tool.
 */
public class MobCaptureTool extends Tool {
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull HookManager hookManager;
    private final @NonNull ProtectionManager protectionManager;
    private final @NonNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager;

    private final @NonNull Player player;
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
            @NonNull ComponentLogger logger,
            @NonNull LocaleManager localeManager,
            @NonNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager,
            @NonNull HookManager hookManager,
            @NonNull ProtectionManager protectionManager,
            @NonNull Player player) {
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
            @NonNull ComponentLogger logger,
            @NonNull LocaleManager localeManager,
            @NonNull MobCaptureToolConfigurationManager mobCaptureToolConfigurationManager,
            @NonNull HookManager hookManager,
            @NonNull ProtectionManager protectionManager,
            @NonNull Player player,
            @NonNull ItemStack itemStack,
            @NonNull EquipmentSlot equipmentSlot) {
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

        MobCaptureToolConfig mobCaptureToolConfig = mobCaptureToolConfigurationManager.getConfiguration();
        if(mobCaptureToolConfig == null) {
            logger.warn(AdventureUtility.plain("Unable to create the mob capture tool because the configuration is invalid."));
            return null;
        }

        List<TagResolver.Single> placeholderList = List.of(Placeholder.parsed("uses", String.valueOf(uses)));
        Optional<@NonNull ItemStack> optionalItemStack = new ItemStackBuilder(logger)
                .fromItemStackConfig(mobCaptureToolConfig.item(), null, placeholderList)
                .buildItemStack();
        if(optionalItemStack.isEmpty()) {
            logger.warn(AdventureUtility.plain("Unable to create the mob capture tool because the configuration is invalid."));
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

        MobCaptureToolConfig mobCaptureToolConfig = mobCaptureToolConfigurationManager.getConfiguration();
        if(mobCaptureToolConfig != null) {
            List<TagResolver.Single> placeholderList = List.of(Placeholder.parsed("uses", String.valueOf(uses)));

            List<String> stringLore = mobCaptureToolConfig.item().lore();
            List<Component> componentLore = stringLore.stream().map(loreLine -> AdventureUtility.deserialize(loreLine, placeholderList)).toList();

            itemMeta.lore(componentLore);
        } else {
            logger.warn(AdventureUtility.plain("Unable to update mob capture tool lore because of invalid build tool configuration."));
            logger.info(AdventureUtility.plain("Uses are still accurately tracked, but the item's lore may not accurately display the uses."));
        }

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Turn the provided entity into a spawn egg if possible.
     * @param entity The {@link Entity}.
     */
    public void captureEntity(@NonNull Entity entity) {
        // Only allow capture of mobs
        if(!(entity instanceof Mob mob)) return;
        Locale locale = localeManager.getConfiguration();

        MobCaptureToolConfig mobCaptureToolConfig = mobCaptureToolConfigurationManager.getConfiguration();
        if(mobCaptureToolConfig == null) {
            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().configError()));
            return;
        }

        if(mobCaptureToolConfig.restrictedWorlds().contains(entity.getWorld().getName())) {
            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().worldNotAllowed()));
            return;
        }

        // Only allow capture if the player can interact with entities
        if(!protectionManager.canInteractWithEntities(player, entity.getLocation())) {
            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().noAccess()));
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
            logger.warn(AdventureUtility.plain("Unable to save updated tool uses due to a null EquipmentSlot."));
            return;
        }

        // Update uses
        uses--;

        // Remove the item if uses are 0, otherwise save the uses to the ItemStack
        if(uses == 0) {
            player.getInventory().setItem(equipmentSlot, ItemType.AIR.createItemStack());

            player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.mobCaptureTool().toolUsedUp()));
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
    private @Nullable ItemStack getSpawnEgg(@NonNull LivingEntity entity) {
        RoseStackerHook roseStackerHook = hookManager.getHook(RoseStackerHook.class);
        ItemsAdderHook itemsAdderHook = hookManager.getHook(ItemsAdderHook.class);

        // Get the EntityType
        EntityType entityType = entity.getType();

        // Get the entity's AI Status
        boolean ai = entity.hasAI();
        boolean aware = !(entity instanceof Mob mob) || mob.isAware();

        // Get the custom entity name or the default name
        Component entityNameComponent = entity.customName();
        String entityNameString = entityNameComponent != null ? AdventureUtility.serialize(entityNameComponent) : entity.getName();

        // The amount of mobs in the stack
        // Will return 1 if RoseStacker isn't hooked so a isHooked check isn't required.
        int stackSize = roseStackerHook.getStackSize(entity);

        // ItemAdders - Get the namespaced id for the entity (if any)
        String namespacedId = null;
        if(itemsAdderHook.isHooked()) {
            namespacedId = itemsAdderHook.getCustomEntityNamespaceId(entity);
        }

        // Attempt to get the ItemStack for the spawn egg
        ItemStack itemStack;
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
        EntitySnapshot entitySnapshot = entity.createSnapshot();
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
                AdventureUtility.deserialize("<gray>Entity Type: </gray>" + entityType.toString().toLowerCase()),
                AdventureUtility.deserialize("<gray>Entity Name: </gray>" + entityNameString),
                AdventureUtility.deserialize("<gray>Custom: </gray>" + (namespacedId != null)),
                AdventureUtility.deserialize("<gray>Has AI: </gray>" + (ai && aware)),
                AdventureUtility.deserialize("<gray>Amount: " + stackSize));
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
    private @Nullable ItemStack getSpawnEgg(@NonNull EntityType entityType) {
        ItemType itemType = getItemTypeFromEntityType(entityType);
        if(itemType == null) return null;

        return itemType.createItemStack();
    }

    /**
     * Get the {@link ItemType} for the spawn egg of the {@link EntityType}.
     * @param entityType The {@link EntityType}.
     * @return The {@link ItemType} or null if no spawn egg exists for that {@link EntityType}.
     */
    private @Nullable ItemType getItemTypeFromEntityType(@NonNull EntityType entityType) {
        return RegistryUtil.getItemType(logger, entityType.getKey().getKey() + "_spawn_egg").orElse(null);
    }
}