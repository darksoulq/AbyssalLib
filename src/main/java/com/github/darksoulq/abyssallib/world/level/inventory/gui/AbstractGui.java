package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.Slot;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.SlotHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an abstract GUI screen within the AbyssalLib system.
 * <p>
 * This class provides a shared or per-player inventory abstraction with slot registration,
 * ticking logic, and support for click, drag, and close events.
 */
public abstract class AbstractGui {

    /**
     * The type of inventory menu being used.
     */
    private final MenuType type;

    /**
     * The title component shown on the GUI screen.
     */
    private final Component title;

    /**
     * Shared slot layout for all players using this GUI.
     */
    public final SlotHolder sharedSlots = new SlotHolder();

    /**
     * Custom per-player slot layouts.
     */
    public final Map<Player, SlotHolder> playerSlots = new HashMap<>();

    /**
     * The currently active inventory views for each player using this GUI.
     */
    private final Map<Player, InventoryView> views = new HashMap<>();

    /**
     * Constructs a new AbstractGui instance.
     *
     * @param title the GUI title
     * @param type  the Bukkit menu type
     */
    public AbstractGui(Component title, MenuType type) {
        this.title = title;
        this.type = type;
    }

    /**
     * Registers a shared slot in the specified section.
     *
     * @param type the inventory section (TOP or BOTTOM)
     * @param slot the slot instance
     */
    public void slot(Type type, Slot slot) {
        sharedSlots.add(type, slot);
    }

    /**
     * Registers multiple shared slots in the specified section.
     *
     * @param type the inventory section (TOP or BOTTOM)
     * @param slot the list of slot instances
     */
    public void slot(Type type, List<Slot> slot) {
        slot.forEach(slot1 -> sharedSlots.add(type, slot1));
    }

    /**
     * Registers a per-player slot, overriding the shared layout.
     *
     * @param player the player
     * @param type   the section
     * @param slot   the slot instance
     */
    public void slot(Player player, Type type, Slot slot) {
        playerSlots.computeIfAbsent(player, p -> new SlotHolder()).add(type, slot);
    }

    /**
     * Gets a slot from either the player's custom layout or the shared layout.
     *
     * @param player the player
     * @param type   the section
     * @param index  the slot index
     * @return the slot or {@code null} if not found
     */
    public Slot slot(Player player, Type type, int index) {
        SlotHolder holder = playerSlots.getOrDefault(player, sharedSlots);
        return holder.get(type, index);
    }

    /**
     * Opens this GUI for the given player.
     *
     * @param player the player
     */
    public void open(Player player) {
        InventoryView view = type.create(player, title);
        views.put(player, view);
        player.openInventory(view);
        init(player);
    }

    /**
     * Closes this GUI for the specified player.
     *
     * @param player the player
     */
    public void close(Player player) {
        InventoryView view = views.remove(player);
        if (view != null) {
            player.closeInventory();
        }
    }

    /**
     * Closes the GUI for all currently viewing players.
     */
    public void closeAll() {
        for (Player p : viewers()) {
            p.closeInventory();
        }
        views.clear();
    }

    /**
     * Gets the set of players currently viewing this GUI.
     *
     * @return a set of players
     */
    public Set<Player> viewers() {
        return views.keySet();
    }

    /**
     * Gets the {@link InventoryView} for a given player.
     *
     * @param player the player
     * @return the inventory view, or null if not currently open
     */
    public InventoryView view(Player player) {
        return views.get(player);
    }

    /**
     * Gets the top or bottom inventory section for the player.
     *
     * @param player the player
     * @param type   the section type
     * @return the inventory or {@code null} if the view is unavailable
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
     * Ticks all slots for all viewing players.
     * <p>
     * Invokes {@link Slot#onTick(AbstractGui, Player)} and {@link #onTick(Player)}.
     */
    public void tick() {
        for (Player player : viewers()) {
            SlotHolder holder = playerSlots.getOrDefault(player, sharedSlots);
            for (Slot slot : holder.TOP) slot.onTick(this, player);
            for (Slot slot : holder.BOTTOM) slot.onTick(this, player);
            onTick(player);
        }
    }

    /**
     * Called when a player opens the GUI.
     * <p>
     * Override this to perform initialization logic.
     *
     * @param player the player opening the GUI
     */
    public void init(Player player) {}

    /**
     * Called every tick for each viewer.
     * <p>
     * Override to implement per-player ticking logic.
     *
     * @param player the player
     */
    public void onTick(Player player) {}

    /**
     * Called when a player closes the GUI.
     * <p>
     * Override to handle cleanup or state-saving logic.
     *
     * @param inventory the inventory being closed
     * @param player    the player closing it
     * @param reason    the reason for the close
     */
    public void onClose(Inventory inventory, Player player, InventoryCloseEvent.Reason reason) {}

    /**
     * Determines if a particular section should be processed by this GUI.
     * <p>
     * Override to ignore specific sections (e.g., prevent handling player inventory).
     *
     * @param type the section type
     * @return true to process, false to ignore
     */
    public boolean shouldHandle(Type type) {
        return true;
    }

    /**
     * Gets the slot list for the specified player and section.
     *
     * @param player the player
     * @param type   the inventory section
     * @return list of slots
     */
    public List<Slot> getSlotList(Player player, Type type) {
        return playerSlots.getOrDefault(player, sharedSlots).getAll(type);
    }

    /**
     * Enumeration for the two inventory sections used in GUI views.
     */
    public enum Type {
        /**
         * Represents the top inventory (the GUI container).
         */
        TOP,

        /**
         * Represents the bottom inventory (the player's inventory).
         */
        BOTTOM
    }
}
