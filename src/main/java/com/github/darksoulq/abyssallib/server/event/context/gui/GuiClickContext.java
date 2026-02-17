package com.github.darksoulq.abyssallib.server.event.context.gui;

import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A context object representing the details of an inventory click interaction.
 * <p>
 * This class encapsulates all relevant data when a player clicks within an inventory,
 * including the inventory view, the specific slot clicked, the items involved (cursor and slot),
 * and the type of action performed.
 *
 * @param source           The entity (player) who clicked in the inventory.
 * @param view             The Gui View currently open.
 * @param clickedInventory The specific inventory that was clicked, or null if clicked outside.
 * @param currentItem      The item currently in the clicked slot, or null if empty/outside.
 * @param cursor           The item currently held on the cursor, or null if empty.
 * @param slot             The slot index in the specific inventory.
 * @param rawSlot          The raw slot index in the inventory view.
 * @param clickType        The type of click performed (e.g., LEFT, RIGHT, SHIFT_LEFT).
 * @param action           The inventory action that was triggered.
 * @param slotType         The type of slot that was clicked.
 * @param hotbarKey        The hotbar key pressed (0-8) if applicable, or -1 otherwise.
 */
public record GuiClickContext(HumanEntity source, GuiView view, @Nullable Inventory clickedInventory,
                              @Nullable ItemStack currentItem, @Nullable ItemStack cursor,
                              int slot, int rawSlot,
                              ClickType clickType, InventoryAction action,
                              InventoryType.SlotType slotType, int hotbarKey) {

    /**
     * Gets the entity that performed the click.
     *
     * @return The {@link HumanEntity} source.
     */
    @Override
    public HumanEntity source() {
        return source;
    }

    /**
     * Gets the view of the gui that is open.
     *
     * @return The {@link GuiView}.
     */
    @Override
    public GuiView view() {
        return view;
    }

    /**
     * Gets the specific inventory that was clicked.
     * <p>
     * This may be the top inventory or the bottom (player) inventory.
     *
     * @return The clicked {@link Inventory}, or {@code null} if clicked outside the window.
     */
    @Override
    public @Nullable Inventory clickedInventory() {
        return clickedInventory;
    }

    /**
     * Gets the item currently residing in the clicked slot.
     *
     * @return The {@link ItemStack} in the slot, or {@code null} if empty or clicked outside.
     */
    @Override
    public @Nullable ItemStack currentItem() {
        return currentItem;
    }

    /**
     * Gets the item currently held on the mouse cursor.
     *
     * @return The cursor {@link ItemStack}, or {@code null} if empty.
     */
    @Override
    public @Nullable ItemStack cursor() {
        return cursor;
    }

    /**
     * Gets the slot index within the specific {@link #clickedInventory()}.
     *
     * @return The slot index.
     */
    @Override
    public int slot() {
        return slot;
    }

    /**
     * Gets the raw slot index within the entire {@link InventoryView}.
     *
     * @return The raw slot index.
     */
    @Override
    public int rawSlot() {
        return rawSlot;
    }

    /**
     * Gets the specific type of click performed.
     *
     * @return The {@link ClickType}.
     */
    @Override
    public ClickType clickType() {
        return clickType;
    }

    /**
     * Gets the action determined by the server for this click.
     *
     * @return The {@link InventoryAction}.
     */
    @Override
    public InventoryAction action() {
        return action;
    }

    /**
     * Gets the type of the slot that was clicked.
     *
     * @return The {@link InventoryType.SlotType}.
     */
    @Override
    public InventoryType.SlotType slotType() {
        return slotType;
    }

    /**
     * Gets the hotbar button index if the click type was a number key press.
     *
     * @return The button index (0-8), or -1 if not applicable.
     */
    @Override
    public int hotbarKey() {
        return hotbarKey;
    }
}