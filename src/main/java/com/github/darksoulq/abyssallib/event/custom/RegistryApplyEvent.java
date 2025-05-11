package com.github.darksoulq.abyssallib.event.custom;

import com.github.darksoulq.abyssallib.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.registry.Registry;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a DeferredRegistry is applied to a target Registry.
 * This event is cancellable, allowing you to prevent the application of the DeferredRegistry.
 *
 * @param <T> The type of the objects being registered.
 */
public class RegistryApplyEvent<T> extends Event implements Cancellable {
    /** List of event handlers for this event */
    private static final HandlerList handlers = new HandlerList();

    /** The target registry where the DeferredRegistry is applied. */
    private final Registry<T> target;
    /** The source DeferredRegistry being applied. */
    private final DeferredRegistry<T> source;
    /** Flag to determine if the event is cancelled */
    private boolean isCancelled;

    /**
     * Constructs a new RegistryApplyEvent.
     *
     * @param target The target registry to which the DeferredRegistry is applied.
     * @param source The DeferredRegistry being applied to the target.
     */
    public RegistryApplyEvent(Registry<T> target, DeferredRegistry<T> source) {
        this.target = target;
        this.source = source;
    }

    /**
     * Gets the target registry where the DeferredRegistry is applied.
     *
     * @return The target registry.
     */
    public Registry<T> target() {
        return target;
    }
    /**
     * Gets the source DeferredRegistry that is being applied.
     *
     * @return The DeferredRegistry being applied.
     */
    public DeferredRegistry<T> source() {
        return source;
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
        return isCancelled;
    }
    /**
     * Sets the cancellation state of the event.
     *
     * @param cancel True to cancel the event, false to allow it to proceed.
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
