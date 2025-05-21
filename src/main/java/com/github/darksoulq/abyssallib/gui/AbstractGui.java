package com.github.darksoulq.abyssallib.gui;

import com.github.darksoulq.abyssallib.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.event.context.gui.GuiCloseContext;
import com.github.darksoulq.abyssallib.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.gui.slot.Slot;
import com.github.darksoulq.abyssallib.gui.slot.SlotHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.*;

/**
 * Represents an abstract GUI screen within the AbyssalLib system.
 * This class provides a shared or per-player inventory abstraction with full slot registration,
 * tick logic, and event handling for clicks, drags, and GUI lifecycle events.
 * <p>
 * Supports both shared slot layouts (default) and player-specific slot layouts via {@code playerSlots}.
 */
public abstract class AbstractGui {

    /**
     * The type of menu this GUI represents (e.g., CHEST, HOPPER, etc.).
     */
    private final MenuType type;

    /**
     * The title of the GUI displayed to players.
     */
    private final Component title;

    /**
     * SlotHolder containing all slots shared across all players.
     */
    public final SlotHolder sharedSlots = new SlotHolder();

    /**
     * SlotHolders mapped per player for custom layout overrides.
     */
    public final Map<Player, SlotHolder> playerSlots = new HashMap<>();

    /**
     * Currently active InventoryView for each player viewing this GUI.
     */
    private final Map<Player, InventoryView> views = new HashMap<>();

    /**
     * Constructs a new GUI with the given title and menu type.
     *
     * @param title the title displayed to the player
     * @param type  the inventory menu type
     */
    public AbstractGui(Component title, MenuType type) {
        this.title = title;
        this.type = type;
    }

    // ────────────────────────────────────────
    // Slot Registration
    // ────────────────────────────────────────

    /**
     * Registers a shared slot in the specified section.
     *
     * @param type the inventory section (TOP or BOTTOM)
     * @param slot the slot to register
     */
    public void slot(Type type, Slot slot) {
        sharedSlots.add(type, slot);
    }

    /**
     * Registers multiple shared slots in the specified section.
     *
     * @param type the inventory section (TOP or BOTTOM)
     * @param slot the list of slots to register
     */
    public void slot(Type type, List<Slot> slot) {
        slot.forEach(slot1 -> sharedSlots.add(type, slot1));
    }

    /**
     * Registers a slot for a specific player in a specific section.
     * This overrides the shared layout for the given player.
     *
     * @param player the player
     * @param type   the inventory section
     * @param slot   the slot to register
     */
    public void slot(Player player, Type type, Slot slot) {
        playerSlots.computeIfAbsent(player, p -> new SlotHolder()).add(type, slot);
    }

    /**
     * Retrieves a slot for a specific player at a given index and section.
     * Falls back to the shared layout if no player-specific layout is present.
     *
     * @param player the player
     * @param type   the section
     * @param index  the index of the slot
     * @return the slot or null if not found
     */
    public Slot slot(Player player, Type type, int index) {
        SlotHolder holder = playerSlots.getOrDefault(player, sharedSlots);
        return holder.get(type, index);
    }

    // ────────────────────────────────────────
    // GUI Lifecycle
    // ────────────────────────────────────────

    /**
     * Opens the GUI for a player.
     *
     * @param player the player to open the GUI for
     */
    public void open(Player player) {
        InventoryView view = type.create(player, title);
        views.put(player, view);
        player.openInventory(view);
        init(player);
    }

    /**
     * Closes the GUI for a specific player.
     *
     * @param player the player whose GUI should be closed
     */
    public void close(Player player) {
        InventoryView view = views.remove(player);
        if (view != null) {
            player.closeInventory();
        }
    }

    /**
     * Closes the GUI for all current viewers and clears the view map.
     */
    public void closeAll() {
        for (Player p : viewers()) {
            p.closeInventory();
        }
        views.clear();
    }

    /**
     * Returns all players currently viewing this GUI.
     *
     * @return a set of current viewers
     */
    public Set<Player> viewers() {
        return views.keySet();
    }

    /**
     * Returns the inventory view for the given player.
     *
     * @param player the player to query
     * @return their InventoryView or null if not viewing
     */
    public InventoryView view(Player player) {
        return views.get(player);
    }

    /**
     * Gets a specific inventory section (top or bottom) for a player.
     *
     * @param player the player
     * @param type   the section type
     * @return the inventory section or null if the view is unavailable
     */
    public Inventory inventory(Player player, Type type) {
        InventoryView view = views.get(player);
        if (view == null) return null;
        return switch (type) {
            case TOP -> view.getTopInventory();
            case BOTTOM -> view.getBottomInventory();
        };
    }

    /**
     * Called each tick to update slots and trigger GUI logic.
     */
    public void tick() {
        for (Player player : viewers()) {
            SlotHolder holder = playerSlots.getOrDefault(player, sharedSlots);
            for (Slot slot : holder.TOP) slot.onTick(this);
            for (Slot slot : holder.BOTTOM) slot.onTick(this);
            drawFor(player);
        }

        onTick();
    }

    /**
     * Redraws the GUI for all viewers.
     */
    public void draw() {
        for (Player player : viewers()) {
            drawFor(player);
        }
    }

    /**
     * Redraws the GUI for a single player.
     *
     * @param player the player to redraw for
     */
    public void drawFor(Player player) {
        SlotHolder holder = playerSlots.getOrDefault(player, sharedSlots);

        for (Type type : Type.values()) {
            if (!enableHandling(type)) continue;
            Inventory inv = inventory(player, type);
            if (inv == null) continue;

            ItemStack[] items = new ItemStack[inv.getSize()];
            Arrays.fill(items, null);
            for (Slot slot : holder.getAll(type)) {
                items[slot.index()] = slot.item();
            }
            inv.setContents(items);
        }
    }


    // ────────────────────────────────────────
    // Event Handling
    // ────────────────────────────────────────

    /**
     * Handles click events on registered slots.
     *
     * @param ctx the click context
     */
    public void handleClick(GuiClickContext ctx) {
        handleInteraction(ctx.gui.inventory(ctx.player, Type.TOP), getSlotList(ctx.player, Type.TOP), ctx);
        handleInteraction(ctx.gui.inventory(ctx.player, Type.BOTTOM), getSlotList(ctx.player, Type.BOTTOM), ctx);
    }

    private void handleInteraction(Inventory targetInv, List<Slot> slotList, GuiClickContext ctx) {
        if (ctx.event.getClickedInventory() == targetInv) {
            for (Slot slot : slotList) {
                if (slot.index() == ctx.event.getSlot()) {
                    slot.onClick(ctx);
                    return;
                }
            }
            ctx.cancel();
        }
    }

    /**
     * Handles item drag events, preventing dragging into GUI inventories.
     *
     * @param ctx the drag context
     */
    public void handleDrag(GuiDragContext ctx) {
        for (int ignored : ctx.event.getRawSlots()) {
            if (ctx.gui.inventory(ctx.player, Type.TOP) == ctx.player.getOpenInventory().getTopInventory()) {
                ctx.cancel();
                return;
            }
        }
    }

    // ────────────────────────────────────────
    // Optional Overrides
    // ────────────────────────────────────────

    /**
     * Called when a player opens the GUI. Can be overridden.
     *
     * @param player the player opening the GUI
     */
    public void init(Player player) {}

    /**
     * Called every tick. Can be overridden for GUI logic.
     */
    public void onTick() {}

    /**
     * Called when a player closes the GUI. Can be overridden.
     *
     * @param ctx the close context
     */
    public void onClose(GuiCloseContext ctx) {}

    /**
     * Determines whether a specific inventory section should be handled by the GUI.
     *
     * @param type the section type
     * @return true if handling should occur, false otherwise
     */
    public boolean enableHandling(Type type) {
        return true;
    }

    /**
     * Gets all slots (shared or per-player) for a given player and section.
     *
     * @param player the player
     * @param type   the inventory section
     * @return the slot list
     */
    public List<Slot> getSlotList(Player player, Type type) {
        return playerSlots.getOrDefault(player, sharedSlots).getAll(type);
    }

    /**
     * Enum representing sections of the inventory view.
     */
    public enum Type {
        /**
         * The top inventory (the GUI's inventory).
         */
        TOP,
        /**
         * The bottom inventory (the player's inventory).
         */
        BOTTOM
    }
}
