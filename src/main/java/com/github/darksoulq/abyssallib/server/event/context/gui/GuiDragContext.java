package com.github.darksoulq.abyssallib.server.event.context.gui;

import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * A context object representing the details of an inventory drag interaction.
 * <p>
 * This class encapsulates all relevant data when a player drags an item across inventory slots,
 * including the items being distributed, the slots affected, and the type of drag operation.
 *
 * @param source        The entity (player) who performed the drag.
 * @param view          The Gui View currently open.
 * @param newCursor     The item that will remain on the cursor after the drag completes.
 * @param oldCursor     The item that was on the cursor before the drag started.
 * @param type          The type of drag performed (e.g., SINGLE/Right-click drag or EVEN/Left-click drag).
 * @param newItems      A map of raw slot IDs to the ItemStacks that will be placed in them.
 * @param rawSlots      The set of raw slot IDs involved in the drag.
 * @param inventorySlots The set of converted slot IDs (relative to the specific inventory) involved.
 */
public record GuiDragContext(HumanEntity source, GuiView view,
                             @Nullable ItemStack newCursor, @NotNull ItemStack oldCursor,
                             DragType type, Map<Integer, ItemStack> newItems,
                             Set<Integer> rawSlots, Set<Integer> inventorySlots) {

    /**
     * Gets the entity that performed the drag.
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
     * Gets the result cursor after the drag is done.
     *
     * @return The new cursor {@link ItemStack}, or {@code null} if empty.
     */
    @Override
    public @Nullable ItemStack newCursor() {
        return newCursor;
    }

    /**
     * Gets the cursor item prior to the drag modification.
     *
     * @return The original cursor {@link ItemStack}.
     */
    @Override
    public @NotNull ItemStack oldCursor() {
        return oldCursor;
    }

    /**
     * Gets the DragType describing the distribution behavior.
     *
     * @return The {@link DragType}.
     */
    @Override
    public DragType type() {
        return type;
    }

    /**
     * Gets all items to be added to the inventory in this drag.
     *
     * @return A map from raw slot ID to the new {@link ItemStack} to be placed there.
     */
    @Override
    public Map<Integer, ItemStack> newItems() {
        return newItems;
    }

    /**
     * Gets the raw slot IDs to be changed in this drag.
     * <p>
     * These IDs correspond to {@link InventoryView#getItem(int)}.
     *
     * @return A set of raw slot IDs.
     */
    @Override
    public Set<Integer> rawSlots() {
        return rawSlots;
    }

    /**
     * Gets the converted inventory slot IDs to be changed.
     * <p>
     * These IDs correspond to {@link org.bukkit.inventory.Inventory#getItem(int)}.
     *
     * @return A set of inventory slot IDs.
     */
    @Override
    public Set<Integer> inventorySlots() {
        return inventorySlots;
    }
}