package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when a player's persistent statistic value is changed.
 * This event is {@link Cancellable}, allowing listeners to prevent the change
 * or modify the new value before it is saved to the database.
 *
 * @param <T>
 * The data type of the statistic value being changed.
 */
public class PlayerStatisticChangeEvent<T> extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The player whose statistic is being modified.
     */
    private final Player player;

    /**
     * The statistic container being updated.
     */
    private final Statistic statistic;

    /**
     * The previous value of the statistic before this change.
     */
    private final T oldValue;

    /**
     * The proposed new value for the statistic.
     */
    private T newValue;

    /**
     * The current cancellation state of this event.
     */
    private boolean isCancelled;

    /**
     * Constructs a new PlayerStatisticChangeEvent.
     *
     * @param player
     * The {@link Player} instance whose data is changing.
     * @param statistic
     * The {@link Statistic} instance involved in the change.
     * @param oldValue
     * The value prior to the update.
     * @param newValue
     * The value proposed for the update.
     */
    public PlayerStatisticChangeEvent(@NotNull Player player, @NotNull Statistic statistic, T oldValue, T newValue) {
        this.player = player;
        this.statistic = statistic;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Retrieves the player involved in this statistic change.
     *
     * @return
     * The {@link Player} instance.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the specific statistic container that is being updated.
     *
     * @return
     * The {@link Statistic} definition and container.
     */
    public @NotNull Statistic getStatistic() {
        return statistic;
    }

    /**
     * Retrieves the value of the statistic prior to this change event.
     *
     * @return
     * The old value of type {@code T}.
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * Retrieves the proposed new value for the statistic.
     *
     * @return
     * The proposed new value of type {@code T}.
     */
    public T getNewValue() {
        return newValue;
    }

    /**
     * Overrides the proposed new value with a different one.
     *
     * @param newValue
     * The new value to be assigned to the player's statistic.
     */
    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    /**
     * Checks if the statistic change has been cancelled.
     *
     * @return
     * True if the change is prevented, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param v
     * True to cancel the change, false to allow it.
     */
    @Override
    public void setCancelled(boolean v) {
        this.isCancelled = v;
    }

    /**
     * Retrieves the list of handlers listening to this event instance.
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