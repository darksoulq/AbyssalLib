package me.darksoul.abyssallib.gui;

import me.darksoul.abyssallib.event.context.gui.GuiClickContext;
import me.darksoul.abyssallib.event.context.gui.GuiCloseContext;
import me.darksoul.abyssallib.event.context.gui.GuiDragContext;
import me.darksoul.abyssallib.gui.slot.Slot;
import me.darksoul.abyssallib.gui.slot.SlotHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for custom GUI implementations.
 * <p>
 * Handles internal inventory view, slot management, ticking, and basic event routing for clicks and drags.
 */
public abstract class AbstractGui {
    private final InventoryView view;
    public SlotHolder slots = new SlotHolder();

    /**
     * Constructs a new GUI instance with a title and menu type.
     *
     * @param player the player viewing the GUI
     * @param title  the GUI title as a {@link Component}
     * @param type   the type of menu (e.g., GENERIC_9x1, HOPPER)
     */
    public AbstractGui(Player player, Component title, MenuType type) {
        view = type.create(player, title);
    }

    // Inventory
    /**
     * Returns the {@link InventoryView} representing this GUI.
     *
     * @return the inventory view
     */
    public InventoryView view() {
        return view;
    }

    /**
     * Gets the inventory of the specified GUI section.
     *
     * @param type the type (TOP or BOTTOM)
     * @return the inventory for the section, or null if unknown
     */
    public Inventory inventory(Type type) {
        if (type.equals(Type.TOP)) {
            return view.getTopInventory();
        } else if (type.equals(Type.BOTTOM)) {
            return view.getBottomInventory();
        }
        return null;
    }

    // Slots
    /**
     * Registers a slot in the specified section of the GUI.
     *
     * @param type the inventory section
     * @param slot the slot to register
     */
    public void slot(Type type, Slot slot) {
        slots.add(type, slot);
    }
    /**
     * Registers multiple slots in the specified section of the GUI.
     *
     * @param type  the inventory section
     * @param slot the list of slots to register
     */
    public void slot(Type type, List<Slot> slot) {
        slot.forEach(slot1 -> slots.add(type, slot1));
    }
    /**
     * Retrieves a registered slot by index and section.
     *
     * @param type  the inventory section
     * @param index the slot index
     * @return the slot at that index, or null if not found
     */
    public Slot slot(Type type, int index) {
        return slots.get(type, index);
    }

    /**
     * Determines whether the GUI handles interaction for a given section.
     * Override to selectively disable handling of certain parts.
     *
     * @param type the inventory section
     * @return true if handled, false otherwise
     */
    public boolean enableHandling(Type type) {
        return true;
    }

    /**
     * Called every tick by the GUI manager.
     * Updates all slots and performs custom logic defined in {@link Slot#onTick(AbstractGui)} and in {@link #onTick()}.
     */
    public void tick() {
        for (Slot slot : slots.TOP) {
            slot.onTick(this);
        }
        for (Slot slot : slots.BOTTOM) {
            slot.onTick(this);
        }
        draw();
        onTick();
    }
    /**
     * Redraws all items in the GUI from registered slot definitions.
     * Clears and sets inventory contents based on registered slots.
     */
    public void draw() {
        // TOP
        if (enableHandling(Type.TOP)) {
            if (slots.TOP.isEmpty()) {
                inventory(Type.TOP).clear();
            }
            ItemStack[] items = inventory(Type.TOP).getContents();
            Arrays.fill(items, null);
            for (Slot slot : slots.TOP) {
                items[slot.index()] = slot.item();
            }
            inventory(Type.TOP).setContents(items);
        }
        // BOTTOM
        if (enableHandling(Type.BOTTOM)) {
            if (slots.BOTTOM.isEmpty()) {
                inventory(Type.BOTTOM).clear();
            }
            ItemStack[] items = inventory(Type.BOTTOM).getContents();
            Arrays.fill(items, null);
            for (Slot slot : slots.BOTTOM) {
                items[slot.index()] = slot.item();
            }
            inventory(Type.BOTTOM).setContents(items);
        }
    }
    /**
     * Handles a click event inside the GUI.
     * Maps the clicked slot to the appropriate {@link Slot} handler.
     *
     * @param ctx the click event context
     */
    public void handleClick(GuiClickContext ctx) {
        // TOP
        Map<Integer, Slot> indexedSlots = new HashMap<>();
        if (enableHandling(Type.TOP) && ctx.event().getClickedInventory() == ctx.gui().inventory(Type.TOP)) {
            for (Slot slot : slots.TOP) {
                indexedSlots.put(slot.index(), slot);
            }
            if (!indexedSlots.containsKey(ctx.event().getSlot())) ctx.cancel();
            indexedSlots.get(ctx.slotIndex()).onClick(ctx);
            // BOTTOM
        } else if (enableHandling(Type.BOTTOM) && ctx.event().getClickedInventory() == ctx.gui().inventory(Type.BOTTOM)) {
            for (Slot slot : slots.BOTTOM) {
                indexedSlots.put(slot.index(), slot);
            }
            if (!indexedSlots.containsKey(ctx.event().getSlot())) ctx.cancel();
            indexedSlots.get(ctx.slotIndex()).onClick(ctx);
        }
    }
    /**
     * Handles a drag event inside the GUI.
     * Prevents dragging items into the top inventory if interaction is disabled.
     *
     * @param ctx the drag event context
     */
    public void handleDrag(GuiDragContext ctx) {
        if (enableHandling(Type.TOP) || enableHandling(Type.BOTTOM)) {
            for (int rSlot : ctx.event().getRawSlots()) {
                if (ctx.gui().view.getInventory(rSlot) == ctx.gui().inventory(Type.TOP)) {
                    ctx.cancel();
                    return;
                }
            }
        }
    }

    // Abstract
    /**
     * Called when the GUI is initialized for a player.
     *
     * @param player the player opening the GUI
     */
    public void init(Player player) {}
    /**
     * Called every tick. Override to implement custom ticking behavior.
     */
    public void onTick() {}
    /**
     * Called when the GUI is closed.
     *
     * @param ctx the close event context
     */
    public void onClose(GuiCloseContext ctx) {}

    /**
     * Enum representing inventory sections used by this GUI.
     */
    public enum Type {
        TOP,
        BOTTOM
    }
}
