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
 * This event is {@link Cancellable}, meaning the block break can be prevented by a plugin.
 * It provides control over loot drop logic and access to environmental data like fortune levels.
 */
public class BlockBrokenEvent extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The custom block instance that is being broken.
     */
    private final CustomBlock block;

    /**
     * The player who is breaking the block.
     */
    private final Player player;

    /**
     * Flag indicating whether the API should handle the default loot drops.
     */
    private boolean baseDrops = true;

    /**
     * A custom list of drops to be used if the default drops are disabled.
     */
    private List<ItemStack> newDrops = null;

    /**
     * The level of the Fortune enchantment on the tool used to break the block.
     */
    private int fortuneLevel;

    /**
     * The current cancellation state of this event.
     */
    private boolean cancelled = false;

    /**
     * Constructs a new BlockBrokenEvent with player, block, and enchantment data.
     *
     * @param player
     * The player breaking the block, or null if broken by other means.
     * @param block
     * The {@link CustomBlock} instance being broken.
     * @param fortuneLevel
     * The level of the Fortune enchantment detected on the item used.
     */
    public BlockBrokenEvent(@Nullable Player player, @NotNull CustomBlock block, int fortuneLevel) {
        this.player = player;
        this.block = block;
        this.fortuneLevel = fortuneLevel;
    }

    /**
     * Retrieves the custom block involved in this break event.
     *
     * @return
     * The non-null {@link CustomBlock} involved in the event.
     */
    public @NotNull CustomBlock getBlock() {
        return block;
    }

    /**
     * Retrieves the player responsible for breaking the block.
     *
     * @return
     * The {@link Player} instance, or null.
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * Sets whether the internal API should proceed with its default loot table logic.
     *
     * @param shouldDrop
     * True to use default API drops, false to override with custom drops.
     */
    public void setBaseDrops(boolean shouldDrop) {
        baseDrops = shouldDrop;
    }

    /**
     * Checks if the internal API is currently configured to handle block drops.
     *
     * @return
     * True if the API handles drops, false otherwise.
     */
    public boolean getBaseDrops() {
        return baseDrops;
    }

    /**
     * Defines a specific list of items to drop instead of the default API loot.
     *
     * @param drops
     * The {@link List} of {@link ItemStack} objects to drop.
     */
    public void setDrops(List<ItemStack> drops) {
        newDrops = drops;
    }

    /**
     * Retrieves the list of custom items to be dropped if base drops are disabled.
     *
     * @return
     * A list of {@link ItemStack} objects, or null if none are set.
     */
    public List<ItemStack> getNewDrops() {
        return newDrops;
    }

    /**
     * Retrieves the detected Fortune level applied to the block break calculation.
     *
     * @return
     * The integer level of the Fortune enchantment.
     */
    public int getFortuneLevel() {
        return fortuneLevel;
    }

    /**
     * Checks whether the block break event has been cancelled by a listener.
     *
     * @return
     * True if cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation status of the block break.
     *
     * @param cancel
     * True to prevent the block from breaking, false to allow it.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Retrieves the set of handlers listening to this event instance.
     *
     * @return
     * The {@link HandlerList} for this event.
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
    public static HandlerList getHandlerList() {
        return handlers;
    }
}