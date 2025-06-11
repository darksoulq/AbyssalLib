package com.github.darksoulq.abyssallib.world.level.item;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.data.tag.ItemTag;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a custom item with additional functionality on top of the standard {@link ItemStack}.
 * This class adds support for custom item metadata, including a unique item ID, event handling, and more.
 *
 * <p> Items of this class are identified by a unique {@link Identifier}, and they support
 * various item actions like right-click, left-click, and use on blocks or entities. The item also allows
 * for storing and retrieving custom data.
 */
public class Item {
    /** Backing Bukkit item stack */
    private ItemStack stack;
    /** Unique identifier for this custom item */
    private final Identifier id;
    /** Settings for additional configuration and behavior */
    private final ItemSettings settings;
    /**
     * Contains lore and hidden tag control for the item's tooltip.
     */
    public final Tooltip tooltip = new Tooltip();

    /**
     * Constructs a new Item with a specific ID and material.
     *
     * @param id the unique ID for this item
     * @param material the material for the item
     */
    public Item(Identifier id, Material material) {
        stack = new ItemStack(material);
        this.id = id;
        for (DataComponentType dt : stack.getDataTypes()) {
            stack.unsetData(dt);
        }
        stack.setData(DataComponentTypes.ITEM_NAME, Component.translatable("item." + id.namespace() + "." + id.path()));
        stack.setData(DataComponentTypes.ITEM_MODEL, id.toNamespace());
        writeIdTag();
        
        updateTooltip();
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
    public Identifier getId() {
        return id;
    }

    /**
     * Override this to append custom tooltip lines or configure hidden components.
     * Called when the item is being prepared for display.
     *
     * @param tooltip the tooltip instance to modify
     */
    public void createTooltip(Tooltip tooltip) {}

    /**
     * Updates the tooltip of the stack using {@link #createTooltip}
     */
    public void updateTooltip() {
        createTooltip(tooltip);
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(tooltip.lines));
        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hideTooltip(tooltip.hide)
                .hiddenComponents(tooltip.hiddenComponents)
                .build());
        if (tooltip.style != null) {
            stack.setData(DataComponentTypes.TOOLTIP_STYLE, tooltip.style.toKey());
        }
    }

    /**
     * Called when the item hits an entity (melee attack).
     *
     * @param source The entity using the item
     * @param target The entity that was hit
     * @return ActionResult to cancel vanilla event or allow it
     */
    public ActionResult postHit(LivingEntity source, Entity target) {
        return ActionResult.PASS;
    }

    /**
     * Called when the item is used to break a block.
     *
     * @param block The block that was mined
     * @param miner The player who mined the block
     * @return ActionResult to cancel vanilla event or allow it
     */
    public ActionResult postMine(org.bukkit.block.Block block, Player miner) {
        return ActionResult.PASS;
    }

    /**
     * Called when the player uses this item (left/right click in air or entity).
     *
     * @param type   Click type (RIGHT or LEFT)
     * @param user   The player using the item
     * @param hand   The hand that is involved in this event
     * @param target entity target (can be null)
     * @return ActionResult to cancel vanilla event or allow it
     */
    public ActionResult useOnEntity(ClickType type, Player user, EquipmentSlot hand,
                                    Entity target) {
        return ActionResult.PASS;
    }

    /**
     * Called when the item is used on a block (right/left click).
     *
     * @param type   Click type (RIGHT or LEFT)
     * @param block  The clicked block
     * @param face   The face of the block clicked
     * @param user   The player using the item
     * @param hand   The hand that is involved in this event
     * @return ActionResult to cancel vanilla event or allow it
     */
    public ActionResult useOnBlock(ClickType type, org.bukkit.block.Block block,
                                   BlockFace face, Player user, EquipmentSlot hand) {
        return ActionResult.PASS;
    }

    /**
     * Called when the item is crafted.
     *
     * @param player The player who crafted the item
     */
    public void onCraft(Player player) {}

    /**
     * Handles the preparation of this item in an anvil.
     *
     * @param ctx the context of the anvil preparation event
     * @return ActionResult to cancel vanilla event or allow it
     */
    public ActionResult onAnvilPrepare(AnvilContext ctx) {
        return ActionResult.CANCEL;
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
                custom.putString(key, value);
                tag.put("CustomData", custom);
                nms.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                stack.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
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
    /**
     * Returns the internal Bukkit ItemStack representation.
     *
     * @return ItemStack instance
     */
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

                Identifier id = Identifier.of(idStr);
                Item entry = BuiltinRegistries.ITEMS.get(id.toString());
                entry.stack = stack;

                return entry;
            }
        }
        return null;
    }

    /**
     * Checks whether this item is part of the specified {@link ItemTag}.
     *
     * @param id the {@link Identifier} of the tag to check
     * @return true if this item is included in the tag, false otherwise
     */
    public boolean hasTag(Identifier id) {
        ItemTag tag = (ItemTag) BuiltinRegistries.ITEM_TAGS.get(id.toString());
        return tag.contains(this);
    }

    /**
     * Clones this item.
     *
     * @return a new cloned item
     */
    @Override
    public @NotNull Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts this item to its corresponding {@link Block}, if it is a block item.
     *
     * @param item the item to convert
     * @return the corresponding {@link Block}, or null if the item is not a block
     */
    public static Block asBlock(Item item) {
        if (!BuiltinRegistries.BLOCK_ITEMS.contains(item.getId().toString())) return null;
        return BuiltinRegistries.BLOCK_ITEMS.get(item.getId().toString());
    }

    /**
     * Represents a tooltip controller for a custom item.
     * Provides lore lines and controls which internal components are hidden.
     */
    public static class Tooltip {

        /**
         * Whether to completely hide the tooltip from display.
         */
        private boolean hide;

        /**
         * The list of {@link Component} lines shown as the item’s lore.
         */
        public List<Component> lines = new ArrayList<>();

        /**
         * A set of {@link DataComponentType} entries that should be hidden from tooltip display.
         */
        public Set<DataComponentType> hiddenComponents = new HashSet<>();

        /**
         * The style to apply to the tooltip display.
         */
        private Identifier style = null;

        /**
         * Sets whether the entire tooltip should be hidden.
         * This overrides all other tooltip settings.
         *
         * @param v true to hide the tooltip completely
         */
        public void hide(boolean v) {
            this.hide = v;
        }

        /**
         * Hides a specific {@link DataComponentType} (like attributes, enchantments, etc.)
         * from appearing in the tooltip.
         * Ignored if the tooltip is completely hidden.
         *
         * @param type the component to hide
         */
        public void hide(DataComponentType type) {
            this.hiddenComponents.add(type);
        }

        /**
         * Adds a single line to the tooltip's lore.
         *
         * @param component the component to add
         */
        public void line(Component component) {
            lines.add(component);
        }

        /**
         * Sets the style id for the tooltip.
         * 
         * @param style the style ID to apply
         */
        public void style(Identifier style) {
            this.style = style;
        }

        /**
         * Returns the style id to apply to the tooltip
         * 
         * @return the {@link Identifier} of the style
         */
        public Identifier style() {
            return this.style;
        }

        /**
         * Returns whether the tooltip should be hidden completely.
         *
         * @return true if hidden
         */
        public boolean hidden() {
            return hide;
        }
    }
}
