package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a player places a block in the Abyssal mod framework.
 * This event is cancellable, allowing you to prevent block placement.
 */
public class AbyssalBlockPlaceEvent extends Event implements Cancellable {
    /** List of event handlers for this event */
    private static final HandlerList handlers = new HandlerList();

    /** The block being placed by the player. */
    private final Block block;
    /** The player placing the block. */
    private final Player player;
    /** The item being used to place the block. */
    private final ItemStack item;
    /** Flag to determine if the event is cancelled */
    private boolean cancelled = false;

    /**
     * Constructs a new AbyssalBlockPlaceEvent.
     *
     * @param player The player placing the block.
     * @param block  The block being placed.
     * @param item   The item used to place the block.
     */
    public AbyssalBlockPlaceEvent(Player player, Block block, ItemStack item) {
        this.player = player;
        this.block = block;
        this.item = item;
    }

    /**
     * Gets the block being placed.
     *
     * @return The block being placed.
     */
    public Block block() {
        return block;
    }
    /**
     * Gets the player placing the block.
     *
     * @return The player placing the block.
     */
    public Player player() {
        return player;
    }
    /**
     * Gets the item used to place the block.
     *
     * @return The item being used.
     */
    public ItemStack item() {
        return item;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return True if the event is cancelled, otherwise false.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancel True to cancel the event, false to allow it to proceed.
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
