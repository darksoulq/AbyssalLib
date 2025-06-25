package com.github.darksoulq.abyssallib.world.level.inventory.gui.slot;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Represents a single interactive slot within a GUI.
 * <p>
 * This abstract base class provides a structure for custom slot behavior including click, drag,
 * and tick logic. Subclasses should override methods to define specific interactions and updates.
 */
public abstract class Slot {

    /**
     * The index of the slot within the inventory.
     */
    protected final int index;

    /**
     * Constructs a new Slot with the given index.
     *
     * @param index the slot index in the inventory
     */
    public Slot(int index) {
        this.index = index;
    }

    /**
     * Called when a player clicks on this slot.
     * <p>
     * Override this method to implement custom click behavior.
     *
     * @param gui          the GUI that this slot belongs to
     * @param clickedInventory the inventory being clicked
     * @param player       the player performing the click
     * @param type         the type of click (e.g. left, right, shift-click)
     * @param action       the inventory action
     * @param currentItem  the item currently in this slot
     * @param cursorItem   the item on the cursor
     * @return the result of the action, used to determine if it should proceed
     */
    public ActionResult onClick(AbstractGui gui,
                                Inventory clickedInventory,
                                Player player,
                                ClickType type,
                                InventoryAction action,
                                ItemStack currentItem,
                                ItemStack cursorItem) {
        return ActionResult.PASS;
    }

    /**
     * Called when a player drags items over this slot.
     * <p>
     * Override this to implement drag-and-drop behaviors.
     *
     * @param gui           the GUI containing this slot
     * @param primaryInventory the inventory where the drag originated
     * @param slots         the slots involved in the drag
     * @param newItems      the mapping of slot indices to the new items
     * @param cursor        the item on the player's cursor after the drag
     * @param oldCursor     the item on the player's cursor before the drag
     * @param type          the type of drag (even split, single, etc.)
     * @return the result of the drag, used to determine if it should proceed
     */
    public ActionResult onDrag(@NotNull AbstractGui gui,
                               @NotNull Inventory primaryInventory,
                               @NotNull Set<Integer> slots,
                               @NotNull Map<Integer, ItemStack> newItems,
                               @Nullable ItemStack cursor,
                               @NotNull ItemStack oldCursor,
                               @NotNull DragType type) {
        return ActionResult.PASS;
    }

    /**
     * Called once per tick for each viewer of the GUI.
     * <p>
     * Override this method to update or animate this slot every tick.
     *
     * @param gui    the GUI containing this slot
     * @param player the player viewing the GUI
     */
    public abstract void onTick(AbstractGui gui, Player player);

    /**
     * Gets the index of this slot in the inventory layout.
     *
     * @return the inventory index of this slot
     */
    public int index() {
        return index;
    }
}
