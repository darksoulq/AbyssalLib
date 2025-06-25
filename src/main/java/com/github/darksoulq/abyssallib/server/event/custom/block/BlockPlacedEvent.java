package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered when a player places a custom block.
 * <p>
 * This event is {@link Cancellable}, meaning the block placement can be prevented by a plugin.
 * </p>
 */
public class BlockPlacedEvent extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by the Bukkit event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The block being placed by the player.
     */
    private final Block block;

    /**
     * The player who is placing the block.
     */
    private final Player player;

    /**
     * The item used to place the block.
     */
    private final ItemStack item;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    /**
     * Constructs a new {@code BlockPlacedEvent}.
     *
     * @param player The player placing the block.
     * @param block  The block being placed.
     * @param item   The item used to place the block.
     */
    public BlockPlacedEvent(@NotNull Player player, @NotNull Block block, @NotNull ItemStack item) {
        this.player = player;
        this.block = block;
        this.item = item;
    }

    /**
     * Gets the block being placed.
     *
     * @return The block being placed.
     */
    public @NotNull Block getBlock() {
        return block;
    }

    /**
     * Gets the player placing the block.
     *
     * @return The player placing the block.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the item used to place the block.
     *
     * @return The item used to place the block.
     */
    public @NotNull ItemStack getItem() {
        return item;
    }

    /**
     * Checks whether the event is cancelled.
     *
     * @return {@code true} if the event is cancelled; {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event.
     *
     * @param cancel {@code true} to cancel the event, {@code false} to allow it to proceed.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the list of handlers listening to this event.
     *
     * @return The handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
