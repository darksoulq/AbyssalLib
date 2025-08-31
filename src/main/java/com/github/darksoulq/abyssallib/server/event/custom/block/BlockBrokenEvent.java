package com.github.darksoulq.abyssallib.server.event.custom.block;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    private final CustomBlock block;

    /**
     * The player who is breaking the block.
     */
    private final Player player;

    /**
     * Whether the api should handle loot drops for the block
     */
    private boolean baseDrops = true;

    /**
     * The drops to use if {@code baseDrops} is false
     */
    private List<ItemStack> newDrops = null;

    /**
     * Fortune level of the item used to break the block
     */
    private int fortuneLevel;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    /**
     * Constructs a new {@code BlockBrokenEvent}.
     *
     * @param player The player breaking the block.
     * @param block  The custom block being broken.
     */
    public BlockBrokenEvent(@Nullable Player player, @NotNull CustomBlock block, int fortuneLevel) {
        this.player = player;
        this.block = block;
        this.fortuneLevel = fortuneLevel;
    }

    /**
     * Gets the block being broken.
     *
     * @return The custom block involved in this event.
     */
    public @NotNull CustomBlock getBlock() {
        return block;
    }

    /**
     * Gets the player who is breaking the block.
     *
     * @return The player involved in this event.
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * Sets whether the api should handle block drops
     *
     * @param shouldDrop should api handle it
     */
    public void setBaseDrops(boolean shouldDrop) {
        baseDrops = shouldDrop;
    }

    /**
     * Gets whether api should handle block drops or not
     *
     * @return whether api should handle the drops
     */
    public boolean getBaseDrops() {
        return baseDrops;
    }

    /**
     * Gets the drops to use in case {@code setBaseDrops(false)} has been set
     *
     * @param drops the drops to use.
     */
    public void setDrops(List<ItemStack> drops) {
        newDrops = drops;
    }

    /**
     * Gets the item list to use as new drops.
     * @return the item stack list
     */
    public List<ItemStack> getNewDrops() {
        return newDrops;
    }

    /**
     * The level of fortune on the item that broke the block
     *
     * @return fortune level.
     */
    public int getFortuneLevel() {
        return fortuneLevel;
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
