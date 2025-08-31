package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a custom {@link Entity} is spawned into the world.
 * <p>
 * This event is cancellable, allowing control over whether the spawn should proceed.
 * </p>
 */
public class EntitySpawnEvent extends Event implements Cancellable {

    /**
     * Handler list for Bukkit's internal event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The custom entity being spawned.
     */
    private final Entity<? extends LivingEntity> entity;

    /**
     * The reason for this entity's spawn.
     */
    private final SpawnReason reason;

    /**
     * Whether the spawn event is cancelled.
     */
    private boolean isCancelled = false;

    /**
     * Constructs a new {@code EntitySpawnEvent}.
     *
     * @param entity The custom entity being spawned.
     * @param reason The reason for the entity's spawn.
     */
    public EntitySpawnEvent(@NotNull Entity<? extends LivingEntity> entity, @NotNull SpawnReason reason) {
        this.entity = entity;
        this.reason = reason;
    }

    /**
     * Gets the custom entity being spawned.
     *
     * @return The entity being spawned.
     */
    public @NotNull Entity<? extends LivingEntity> getEntity() {
        return entity;
    }

    /**
     * Gets the reason for this entity's spawn.
     *
     * @return The spawn reason.
     */
    public @NotNull SpawnReason getReason() {
        return reason;
    }

    /**
     * Checks whether the event is cancelled.
     *
     * @return {@code true} if the event is cancelled, {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param v {@code true} to cancel the event, {@code false} to allow it.
     */
    @Override
    public void setCancelled(boolean v) {
        isCancelled = v;
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

    public static HandlerList getHandlerList() { return handlers; }

    /**
     * The reason why a custom entity is being spawned.
     */
    public enum SpawnReason {
        /**
         * Spawned naturally through world mechanics.
         */
        NATURAL,

        /**
         * Spawned through an item action (e.g., block or item use).
         */
        ITEM,

        /**
         * Spawned by a plugin directly.
         */
        PLUGIN
    }
}
