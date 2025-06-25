package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.level.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Custom event triggered when an {@link Entity} dies.
 * <p>
 * This event is {@link Cancellable}, allowing plugins to prevent entity death behavior.
 * </p>
 */
public class EntityDeathEvent extends Event implements Cancellable {

    /**
     * Handler list for Bukkit's internal event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The entity that is dying.
     */
    private final Entity<? extends LivingEntity> entity;

    /**
     * The entity that killed the dying entity, or {@code null} if there was no killer.
     */
    private final org.bukkit.entity.Entity killer;

    /**
     * Whether the event is cancelled.
     */
    private boolean isCancelled;

    /**
     * Constructs a new {@code EntityDeathEvent}.
     *
     * @param entity The custom entity that is dying.
     * @param killer The Bukkit entity that killed it, or {@code null} if there is none.
     */
    public EntityDeathEvent(@NotNull Entity<? extends LivingEntity> entity,
                            @Nullable org.bukkit.entity.Entity killer) {
        this.entity = entity;
        this.killer = killer;
    }

    /**
     * Gets the custom entity that is dying.
     *
     * @return The dying entity.
     */
    public @NotNull Entity<? extends LivingEntity> getEntity() {
        return entity;
    }

    /**
     * Gets the Bukkit entity responsible for the kill.
     *
     * @return The killer entity, or {@code null} if there was no killer.
     */
    public @Nullable org.bukkit.entity.Entity getKiller() {
        return killer;
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return {@code true} if the event is cancelled; {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of this event.
     *
     * @param v {@code true} to cancel the event, {@code false} to let it proceed.
     */
    @Override
    public void setCancelled(boolean v) {
        this.isCancelled = v;
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
}
