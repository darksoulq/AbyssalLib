package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that is triggered when a player interacts with a custom block.
 * <p>
 * This event is {@link Cancellable}, meaning the interaction can be prevented by a plugin.
 * </p>
 */
public class BlockInteractionEvent extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The block being interacted with.
     */
    private final CustomBlock block;

    /**
     * The face of the block that was interacted with.
     */
    private final BlockFace face;

    /**
     * The location where the interaction occurred.
     */
    private final @Nullable Location interaction;

    /**
     * The type of action performed (e.g., right-click, left-click).
     */
    private final Action action;

    /**
     * The item used by the player during the interaction.
     */
    private final @Nullable ItemStack item;

    /**
     * The player interacting with the block.
     */
    private final Player player;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    /**
     * Constructs a new {@code BlockInteractionEvent}.
     *
     * @param player      The player interacting with the block.
     * @param block       The block being interacted with.
     * @param face        The face of the block being interacted with.
     * @param interaction The location where the interaction occurred.
     * @param action      The action performed by the player.
     * @param item        The item used by the player during the interaction.
     */
    public BlockInteractionEvent(
            @NotNull Player player,
            @NotNull CustomBlock block,
            @NotNull BlockFace face,
            Location interaction,
            @NotNull Action action,
            @Nullable ItemStack item
    ) {
        this.player = player;
        this.block = block;
        this.face = face;
        this.interaction = interaction;
        this.action = action;
        this.item = item;
    }

    /**
     * Gets the block being interacted with.
     *
     * @return The block being interacted with.
     */
    public @NotNull CustomBlock getBlock() {
        return block;
    }

    /**
     * Gets the player interacting with the block.
     *
     * @return The player interacting with the block.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the face of the block being interacted with.
     *
     * @return The face of the block being interacted with.
     */
    public @NotNull BlockFace getBlockFace() {
        return face;
    }

    /**
     * Gets the location where the interaction occurred.
     *
     * @return The location of the interaction.
     */
    public @Nullable Location getInteractionPoint() {
        return interaction;
    }

    /**
     * Gets the action performed by the player (e.g., right-click, left-click).
     *
     * @return The action performed by the player.
     */
    public @NotNull Action getAction() {
        return action;
    }

    /**
     * Gets the item used by the player during the interaction.
     *
     * @return The item used by the player.
     */
    public @Nullable ItemStack getItem() {
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

    /**
     * Provides a static method to retrieve the handler list, required by Bukkit.
     *
     * @return
     * The static {@link HandlerList} for this event type.
     */
    public static HandlerList getHandlerList() { return handlers; }
}
