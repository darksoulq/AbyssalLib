package me.darksoul.abyssallib.event.custom;

import me.darksoul.abyssallib.block.Block;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a player interacts with a block in the Abyssal mod framework.
 * This event is cancellable, allowing you to prevent block interactions.
 */
public class AbyssalBlockInteractEvent extends Event implements Cancellable {
    /**
     * List of event handlers for this event
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The block being interacted with
     */
    private final Block block;
    /**
     * The face of the block being interacted with
     */
    private final BlockFace face;
    /**
     * The location where the interaction occurred
     */
    private final Location interaction;
    /**
     * The action performed by the player (e.g., right-click, left-click)
     */
    private final Action action;
    /**
     * The item being used by the player during the interaction
     */
    private final ItemStack item;
    /**
     * The player interacting with the block
     */
    private final Player player;
    /**
     * Flag to determine if the event is cancelled
     */
    private boolean cancelled = false;

    /**
     * Constructs a new AbyssalBlockInteractEvent.
     *
     * @param player      The player interacting with the block.
     * @param block       The block being interacted with.
     * @param face        The face of the block being interacted with.
     * @param interaction The location of the interaction.
     * @param action      The action performed by the player (e.g., right-click, left-click).
     * @param item        The item used by the player during the interaction.
     */
    public AbyssalBlockInteractEvent(Player player, Block block, BlockFace face, Location interaction, Action action, ItemStack item) {
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
    public Block block() {
        return block;
    }
    /**
     * Gets the player interacting with the block.
     *
     * @return The player interacting with the block.
     */
    public Player player() {
        return player;
    }
    /**
     * Gets the face of the block being interacted with.
     *
     * @return The face of the block being interacted with.
     */
    public BlockFace blockFace() {
        return face;
    }
    /**
     * Gets the location of the interaction.
     *
     * @return The location of the interaction.
     */
    public Location interactionPoint() {
        return interaction;
    }
    /**
     * Gets the action performed by the player (e.g., right-click, left-click).
     *
     * @return The action performed by the player.
     */
    public Action action() {
        return action;
    }
    /**
     * Gets the item used by the player during the interaction.
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
