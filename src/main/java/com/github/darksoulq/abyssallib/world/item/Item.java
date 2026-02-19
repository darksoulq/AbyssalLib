package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.InventoryClickType;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a custom item within the AbyssalLib framework.
 * <p>
 * This class serves as the primary template for creating new items, handling
 * their data components, tooltips, and interaction logic.
 */
public class Item implements Cloneable {
    /** The unique {@link Identifier} representing the item's namespace and path. */
    private Identifier id;
    /** The actual {@link ItemStack} linked to this custom item instance. */
    private ItemStack stack;
    /** The map containing all {@link DataComponent}s applied to this item. */
    private ComponentMap componentMap;
    /** The helper object used to build and manage procedural tooltips. */
    public Tooltip tooltip = new Tooltip();

    /**
     * Internal constructor for wrapping an existing item stack.
     *
     * @param stack The {@link ItemStack} to wrap and analyze.
     */
    @ApiStatus.Internal
    public Item(ItemStack stack) {
        this.id = Identifier.of("unknown", "unknown");
        this.stack = stack;
        componentMap = new ComponentMap(this);
    }

    /**
     * Constructs a new custom item definition.
     *
     * @param id   The unique {@link Identifier} for the item (e.g., "modid:item_name").
     * @param base The base {@link Material} used for the underlying item stack.
     */
    public Item(Identifier id, Material base) {
        this.id = id;
        stack = ItemStack.of(base);
        Integer size = stack.getData(DataComponentTypes.MAX_STACK_SIZE);
        componentMap = new ComponentMap(this);

        for (DataComponent<?> comp : componentMap.getAllComponents()) {
            if (!(comp instanceof Vanilla)) return;
            componentMap.removeData(comp.getType());
        }

        if (size != null) setData(new MaxStackSize(size));
        setData(new ItemName(Component.translatable("item." + id.getNamespace() + "." + id.getPath())));
        setData(new ItemModel(id.asNamespacedKey()));
        setData(new CustomMarker(id));
    }

    /**
     * Called when a tooltip is being generated. Override to add static lines.
     *
     * @param tooltip The {@link Tooltip} instance to populate with lines/styles.
     */
    public void createTooltip(Tooltip tooltip) {}

    /**
     * Called when a tooltip is being generated for a specific viewer.
     *
     * @param tooltip The {@link Tooltip} instance to populate.
     * @param player  The {@link Player} viewing the item, or {@code null} if unknown.
     */
    public void createTooltip(Tooltip tooltip, @Nullable Player player) {
        createTooltip(tooltip);
    }

    /**
     * Updates the item's data components (Lore, DisplayTooltip, TooltipStyle)
     * based on the current state of the {@link #tooltip} object.
     */
    public void updateTooltip() {
        setData(new Lore(ItemLore.lore(tooltip.lines)));
        setData(new DisplayTooltip(TooltipDisplay.tooltipDisplay()
            .hideTooltip(tooltip.hide)
            .hiddenComponents(tooltip.hiddenComponents).build()));
        if (tooltip.style != null) setData(new TooltipStyle(tooltip.style.asNamespacedKey()));
        else unsetData(TooltipStyle.TYPE);
    }

    /**
     * Adds or updates a data component for this item.
     *
     * @param component The {@link DataComponent} instance to apply.
     */
    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }

    /**
     * Retrieves the data component of the specified type.
     *
     * @param <C>  The specific type of DataComponent.
     * @param type The {@link DataComponentType} to look for.
     * @return The found component, or {@code null} if not set.
     */
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return  componentMap.getData(type);
    }

    /**
     * Checks if this item possesses a specific component type.
     *
     * @param type The {@link DataComponentType} to verify.
     * @return {@code true} if the component is present.
     */
    public boolean hasData(DataComponentType<?> type) {
        return componentMap.hasData(type);
    }

    /**
     * Removes the data component associated with the provided class type.
     *
     * @param clazz The {@link DataComponentType} identifying the component to unset.
     */
    public void unsetData(DataComponentType<?> clazz) {
        componentMap.removeData(clazz);
    }

    /**
     * Checks if this item belongs to a specific tag by ID.
     *
     * @param id The {@link Identifier} of the tag (e.g., "minecraft:swords").
     * @return {@code true} if the item is included in the tag.
     */
    public boolean hasTag(Identifier id) {
        if (!(Registries.TAGS.get(id.toString()) instanceof ItemTag tag)) {
            AbyssalLib.getInstance().getLogger().severe("Unknown tag: " + id);
            return false;
        }
        return tag.contains(stack);
    }

    /**
     * Checks if this item belongs to a specific {@link ItemTag}.
     *
     * @param tag The {@link ItemTag} instance to check.
     * @return {@code true} if the item stack matches the tag criteria.
     */
    public boolean hasTag(ItemTag tag) {
        return tag.contains(stack);
    }

    /**
     * Adds this custom item's identity to an existing tag's logic.
     *
     * @param tag The {@link ItemTag} to extend with this item.
     */
    public void setTag(ItemTag tag) {
        tag.add(ItemPredicate.builder()
            .value(new CustomMarker(id))
            .build());
    }

    /**
     * Callback triggered after an entity successfully mines a block with this item.
     *
     * @param source The {@link LivingEntity} performing the mining.
     * @param target The {@link Block} that was destroyed.
     * @return The resulting {@link ActionResult} determining event continuation.
     */
    public ActionResult postMine(LivingEntity source, Block target) {
        return ActionResult.PASS;
    }

    /**
     * Callback triggered after an entity hits another entity with this item.
     *
     * @param source The {@link LivingEntity} attacker.
     * @param target The {@link Entity} victim.
     * @return The resulting {@link ActionResult} for the hit event.
     */
    public ActionResult postHit(LivingEntity source, Entity target) {
        return ActionResult.PASS;
    }

    /**
     * Callback triggered when a player uses this item on a specific block or entity.
     *
     * @param ctx The {@link UseContext} containing information about the interaction.
     * @return The {@link ActionResult} of the block interaction.
     */
    public ActionResult onUseOn(UseContext ctx) {
        return  ActionResult.PASS;
    }

    /**
     * Callback triggered when a player uses this item while clicking into the air.
     *
     * @param source The {@link LivingEntity} using the item.
     * @param hand   The {@link EquipmentSlot} used for the click.
     * @param type   The {@link ClickType} (Right-click or Left-click) performed in air.
     * @return The {@link ActionResult} of the air interaction.
     */
    public ActionResult onUse(LivingEntity source, EquipmentSlot hand, ClickType type) {
        return ActionResult.PASS;
    }

    /**
     * Called once per tick for every player that has this item in their inventory.
     *
     * @param player The {@link Player} carrying the item.
     */
    public void onInventoryTick(Player player) {}

    /**
     * Called when the item's position within a player's inventory changes.
     *
     * @param player  The {@link Player} whose inventory was updated.
     * @param newSlot The index of the new slot, or {@code null} if moved out of inventory.
     */
    public void onSlotChange(Player player, @Nullable Integer newSlot) {}

    /**
     * Callback triggered when a player clicks this item inside an inventory GUI.
     *
     * @param player    The {@link Player} clicking.
     * @param slot      The raw slot index clicked.
     * @param inventory The {@link PlayerInventory} involved.
     * @param type      The {@link InventoryClickType} representing the click style.
     * @return The {@link ActionResult} determining if the click is allowed.
     */
    public ActionResult onClickInInventory(Player player, int slot, PlayerInventory inventory, InventoryClickType type) {
        return ActionResult.PASS;
    }

    /**
     * Called when a player drops this item onto the ground.
     *
     * @param player The {@link Player} who dropped the item.
     * @return The {@link ActionResult} of the drop.
     */
    public ActionResult onDrop(Player player) {
        return ActionResult.PASS;
    }

    /**
     * Called when a player picks this item up from the ground.
     *
     * @param player The {@link Player} picking up the item.
     * @return The {@link ActionResult} of the pickup.
     */
    public ActionResult onPickup(Player player) {
        return ActionResult.PASS;
    }

    /**
     * Called when a player uses the swap-hand key with this item.
     *
     * @param player  The {@link Player} swapping the item.
     * @param current The {@link EquipmentSlot} the item was in before the swap.
     * @return The {@link ActionResult} of the swap action.
     */
    public ActionResult onSwapHand(Player player, EquipmentSlot current) {
        return ActionResult.PASS;
    }

    /**
     * Called when this item is placed in an anvil and requires custom logic.
     *
     * @param ctx The {@link AnvilContext} containing the input and result slots.
     * @return The {@link ActionResult} of the anvil process.
     */
    public ActionResult onAnvilPrepare(AnvilContext ctx) {
        return ActionResult.PASS;
    }

    /**
     * Called when a player successfully crafts this item.
     *
     * @param player The {@link Player} who completed the craft.
     */
    public void onCraftedBy(Player player) {}

    /** @return The item's unique {@link Identifier}. */
    public Identifier getId() {
        return id;
    }

    /** @return The underlying {@link ItemStack} associated with this instance. */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Creates a temporary clone of the item and renders it for a specific player's view.
     *
     * @param player The {@link Player} for whom the stack is being generated.
     * @return A localized {@link ItemStack} with a customized tooltip.
     */
    public ItemStack getStack(@Nullable Player player) {
        Item contextItem = this.clone();
        contextItem.tooltip.lines.clear();
        contextItem.createTooltip(contextItem.tooltip, player);
        contextItem.updateTooltip();
        return contextItem.getStack();
    }

    /** @return The internal {@link ComponentMap} managing this item's data. */
    public ComponentMap getComponentMap() {
        return componentMap;
    }

    /** @return A {@link CTag} instance for accessing persistent NBT-like data. */
    public CTag getCTag() {
        return CTag.getCTag(stack);
    }

    /** @param container The {@link CTag} data to apply to the item's PDC. */
    public void setCTag(CTag container) {
        CTag.setCTag(container, stack);
    }

    /**
     * Resolves a standard Bukkit stack into a custom library Item.
     *
     * @param stack The {@link ItemStack} to check.
     * @return A custom {@link Item} instance if valid, otherwise {@code null}.
     */
    public static Item resolve(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        Item base = new Item(stack);
        if (!base.hasData(CustomMarker.TYPE)) return null;
        Identifier id = (Identifier) base.getData(CustomMarker.TYPE).getValue();
        if (id == null) return null;
        Item item = Registries.ITEMS.get(id.toString());
        if (item == null) return null;
        Item clone = item.clone();
        clone.stack = stack;
        clone.componentMap = new ComponentMap(clone);
        return clone;
    }

    /**
     * Attempts to find the custom block associated with a block-item.
     *
     * @param item The {@link Item} to analyze.
     * @return The {@link CustomBlock} counterpart, or {@code null} if not a block.
     */
    public static CustomBlock asBlock(Item item) {
        if (!item.hasData(BlockItem.TYPE)) return null;
        Identifier blockId = (Identifier) item.getData(BlockItem.TYPE).getValue();
        return Registries.BLOCKS.get(blockId.toString()).clone();
    }

    /**
     * Performs a deep clone of the item, including its stack, map, and tooltip state.
     *
     * @return A new {@link Item} clone.
     */
    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.id = this.id;
            item.stack = this.stack.clone();
            item.componentMap = new ComponentMap(item);
            item.tooltip = new Tooltip();
            item.tooltip.hide = this.tooltip.hide;
            item.tooltip.style = this.tooltip.style;
            item.tooltip.lines = new ArrayList<>(this.tooltip.lines);
            item.tooltip.hiddenComponents = new HashSet<>(this.tooltip.hiddenComponents);
            return item;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compares this item against another object for equality based on Identifier.
     *
     * @param o The other object.
     * @return {@code true} if both items share the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }

    /** @return A hash code derived from the item's Identifier. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Internal class used to configure the visual representation of an item's tooltip.
     */
    public static class Tooltip {
        /** Determines if the entire tooltip (name and lore) is hidden from the user. */
        private boolean hide;
        /** The list of Adventure {@link Component}s representing the item's lore lines. */
        public List<Component> lines = new ArrayList<>();
        /** A set of vanilla component types that are explicitly hidden from the client display. */
        public Set<io.papermc.paper.datacomponent.DataComponentType> hiddenComponents = new HashSet<>();
        /** The {@link Identifier} of the resource-pack based tooltip style to apply. */
        private Identifier style = null;

        /**
         * Sets whether the tooltip should be visible to players.
         *
         * @param v {@code true} for visible, {@code false} for hidden.
         */
        public void setVisible(boolean v) {
            this.hide = !v;
        }

        /**
         * Marks a vanilla data component as hidden in the tooltip display.
         *
         * @param type The Paper {@link io.papermc.paper.datacomponent.DataComponentType} to hide.
         */
        public void withHidden(io.papermc.paper.datacomponent.DataComponentType type) {
            this.hiddenComponents.add(type);
        }

        /**
         * Appends a new line of text to the tooltip lore.
         *
         * @param component The {@link Component} text line to add.
         */
        public void addLine(Component component) {
            lines.add(component);
        }

        /**
         * Assigns a custom style identifier to the tooltip.
         *
         * @param style The {@link Identifier} representing the style.
         */
        public void withStyle(Identifier style) {
            this.style = style;
        }

        /** @return The currently assigned tooltip style {@link Identifier}. */
        public Identifier getStyle() {
            return this.style;
        }

        /** @return {@code true} if the tooltip is configured as hidden. */
        public boolean isVisible() {
            return hide;
        }

        /**
         * Retrieves a specific line from the tooltip lore.
         *
         * @param index The line index.
         * @return The {@link Component} at the given index.
         */
        public Component getLine(int index) {
            return lines.get(index);
        }
    }
}