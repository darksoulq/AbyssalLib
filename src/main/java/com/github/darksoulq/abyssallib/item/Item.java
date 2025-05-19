package com.github.darksoulq.abyssallib.item;

import com.github.darksoulq.abyssallib.block.Block;
import com.github.darksoulq.abyssallib.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.event.context.item.ItemUseContext;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.registry.Registry;
import com.github.darksoulq.abyssallib.tag.ItemTag;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom item with additional functionality on top of the standard {@link ItemStack}.
 * This class adds support for custom item metadata, including a unique item ID, event handling, and more.
 *
 * <p> Items of this class are identified by a unique {@link ResourceLocation}, and they support
 * various item actions like right-click, left-click, and use on blocks or entities. The item also allows
 * for storing and retrieving custom data.
 */
public class Item {
    private final ItemStack stack;
    private final ResourceLocation id;
    private final ItemSettings settings;

    /**
     * Constructs a new Item with a specific ID and material.
     *
     * @param id the unique ID for this item
     * @param material the material for the item
     */
    public Item(ResourceLocation id, Material material) {
        stack = new ItemStack(material);
        this.id = id;
        stack.setData(DataComponentTypes.ITEM_NAME, Component.translatable("item." + id.namespace() + "." + id.path()));
        stack.setData(DataComponentTypes.ITEM_MODEL, id.toNamespace());
        writeIdTag();
        this.settings = new ItemSettings(this);
    }

    /**
     * Writes the custom ID tag to the item NBT data.
     */
    private void writeIdTag() {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CompoundTag tag = new CompoundTag();

        CompoundTag custom = tag.getCompoundOrEmpty("CustomData");
        custom.putString("AbyssalItemId", id.toString());
        tag.put("CustomData", custom);

        nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        ItemStack updated = CraftItemStack.asBukkitCopy(nms);
        stack.setItemMeta(updated.getItemMeta());
    }

    /**
     * Gets the unique ID of this item.
     *
     * @return the unique ID of this item
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Handles the right-click action on this item.
     *
     * @param ctx the context of the item use event
     */
    public void onRightClick(ItemUseContext ctx) {}
    /**
     * Handles the left-click action on this item.
     *
     * @param ctx the context of the item use event
     */
    public void onLeftClick(ItemUseContext ctx) {}
    /**
     * Handles using this item on a block.
     *
     * @param ctx the context of the item use event
     */
    public void onUseOnBlock(ItemUseContext ctx) {}
    /**
     * Handles using this item on an entity.
     *
     * @param ctx the context of the item use event
     */
    public void onUseEntity(ItemUseContext ctx) {}
    /**
     * Handles the preparation of this item in an anvil.
     *
     * @param ctx the context of the anvil preparation event
     */
    public void onAnvilPrepare(AnvilContext ctx) {}

    /**
     * Handles interactions with this item, dispatching the correct action based on the event type.
     *
     * @param ctx the context of the item use event
     */
    public void onInteract(ItemUseContext ctx) {
        if (ctx.event instanceof org.bukkit.event.player.PlayerInteractEvent event) {
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK -> {
                    onUseOnBlock(ctx);
                    onRightClick(ctx);
                }
                case RIGHT_CLICK_AIR -> onRightClick(ctx);
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> onLeftClick(ctx);
            }
        }
    }

    /**
     * Sets a custom piece of data for this item.
     *
     * @param key the key for the custom data
     * @param value the value of the custom data
     */
    public void data(String key, String value) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData dta = nms.get(DataComponents.CUSTOM_DATA);
        if (dta == null) {
            return;
        }

        CompoundTag tag = dta.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (!custom.contains(key)) {
                custom.putString(key, value);
                tag.put("CustomData", custom);
                nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                stack.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
            }
        }
    }

    /**
     * Gets a custom piece of data for this item.
     *
     * @param key the key for the custom data
     * @return the value of the custom data, or null if not present
     */
    public String data(String key) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (custom.getString(key).isPresent()) {
                return custom.getString(key).get();
            }
        }
        return null;
    }

    /**
     * Gets the settings associated with this item.
     *
     * @return the item settings
     */
    public ItemSettings settings() {
        return settings;
    }
    public ItemStack stack() { return stack; }

    /**
     * Attempts to retrieve a registered {@link Item} instance from the given {@link ItemStack}.
     * <p>
     * This checks if the {@code ItemStack} contains the "AbyssalItemId" in its custom data,
     * and if it corresponds to a known registered {@code Item} in {@code BuiltinRegistries.ITEMS}.
     * </p>
     *
     * @param stack the Bukkit {@code ItemStack} to inspect
     * @return the corresponding {@code Item} if registered and found, or {@code null} if not an Abyssal item
     */
    public static Item from(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;

        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        CustomData data = nms.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            if (custom.getString("AbyssalItemId").isPresent()) {
                String idStr = custom.getString("AbyssalItemId").get();
                if (idStr.isEmpty()) return null;

                ResourceLocation id = ResourceLocation.fromString(idStr);
                Registry.RegistryEntry<Item> entry = BuiltinRegistries.ITEMS.getEntry(id.toString());

                return entry != null ? entry.create(id.toString()) : null;
            }
        }
        return null;
    }

    /**
     * Checks whether this item is part of the specified {@link ItemTag}.
     *
     * @param id the {@link ResourceLocation} of the tag to check
     * @return true if this item is included in the tag, false otherwise
     */
    public boolean hasTag(ResourceLocation id) {
        ItemTag tag = (ItemTag) BuiltinRegistries.ITEM_TAGS.get(id.toString());
        return tag.contains(this);
    }

    /**
     * Clones this item, creating a new instance with the same ID and material.
     *
     * @return a new cloned item
     */
    @Override
    public @NotNull Item clone() {
        return new Item(id, stack.getType());
    }

    /**
     * Converts this item to its corresponding {@link Block}, if it is a block item.
     *
     * @param item the item to convert
     * @return the corresponding {@link Block}, or null if the item is not a block
     */
    public static Block asBlock(Item item) {
        if (!BuiltinRegistries.BLOCK_ITEMS.containsKey(item.getId().toString())) return null;
        return BuiltinRegistries.BLOCK_ITEMS.get(item.getId().toString());
    }
}
