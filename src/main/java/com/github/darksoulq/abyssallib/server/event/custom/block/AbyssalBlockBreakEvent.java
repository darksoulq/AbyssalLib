package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a block is broken in the Abyssal mod framework.
 * This event is cancellable, allowing you to prevent block breaks.
 */
public class AbyssalBlockBreakEvent extends Event implements Cancellable {
    /*
     * List of event handlers for this event
     */
    private static final HandlerList handlers = new HandlerList();

    /*
     * The block being broken
     */
    private final Block block;
    /*
     * The player who is breaking the block
     */
    private final Player player;
    /*
     * Flag to determine if the event is cancelled
     */
    private boolean cancelled = false;

    /**
     * Constructs a new AbyssalBlockBreakEvent.
     *
     * @param player The player who is breaking the block.
     * @param block  The block being broken.
     */
    public AbyssalBlockBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    /**
     * Gets the block being broken.
     *
     * @return The block being broken.
     */
    public Block block() {
        return block;
    }
    /**
     * Gets the player who is breaking the block.
     *
     * @return The player who is breaking the block.
     */
    public Player player() {
        return player;
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
