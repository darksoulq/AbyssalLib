package com.github.darksoulq.abyssallib.server.event.custom.server;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registry;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a {@link DeferredRegistry} is applied to a {@link Registry}.
 * <p>
 * This event is cancellable, allowing control over whether the registry data is applied.
 * </p>
 *
 * @param <T> The type of objects managed by the registries.
 */
public class RegistryApplyEvent<T> extends Event implements Cancellable {

    /**
     * Handler list for Bukkit's internal event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The target registry that the {@link DeferredRegistry} is applied to.
     */
    private final Registry<T> target;

    /**
     * The {@link DeferredRegistry} being applied.
     */
    private final DeferredRegistry<T> source;

    /**
     * Whether this event has been cancelled.
     */
    private boolean isCancelled;

    /**
     * Constructs a new {@code RegistryApplyEvent}.
     *
     * @param target The registry to apply the data to.
     * @param source The deferred registry to be applied.
     */
    public RegistryApplyEvent(@NotNull Registry<T> target, @NotNull DeferredRegistry<T> source) {
        this.target = target;
        this.source = source;
    }

    /**
     * Gets the target registry that the {@link DeferredRegistry} is being applied to.
     *
     * @return The target {@link Registry}.
     */
    public @NotNull Registry<T> target() {
        return target;
    }

    /**
     * Gets the {@link DeferredRegistry} being applied.
     *
     * @return The source {@link DeferredRegistry}.
     */
    public @NotNull DeferredRegistry<T> source() {
        return source;
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
     * Sets the cancellation state of this event.
     *
     * @param cancel {@code true} to cancel the event, {@code false} to allow it to proceed.
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
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
