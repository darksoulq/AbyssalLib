package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is called when a custom block is broken by a player.
 * <p>
 * This event is {@link Cancellable}, meaning the block break can be prevented by a plugin.
 * </p>
 */
public class BlockBrokenEvent extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The block that is being broken.
     */
    private final Block block;

    /**
     * The player who is breaking the block.
     */
    private final Player player;

    /**
     * Whether or not the event is cancelled.
     */
    private boolean cancelled = false;

    /**
     * Constructs a new {@code BlockBrokenEvent}.
     *
     * @param player The player breaking the block.
     * @param block  The custom block being broken.
     */
    public BlockBrokenEvent(@NotNull Player player, @NotNull Block block) {
        this.player = player;
        this.block = block;
    }

    /**
     * Gets the block being broken.
     *
     * @return The custom block involved in this event.
     */
    public @NotNull Block getBlock() {
        return block;
    }

    /**
     * Gets the player who is breaking the block.
     *
     * @return The player involved in this event.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Checks whether the event is currently cancelled.
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

    public static HandlerList getHandlerList() { return handlers; }
}
