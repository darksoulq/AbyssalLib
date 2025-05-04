package me.darksoul.abyssalLib.event.custom;

import me.darksoul.abyssalLib.registry.DeferredRegistry;
import me.darksoul.abyssalLib.registry.Registry;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegistryApplyEvent<T> extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Registry<T> target;
    private final DeferredRegistry<T> source;
    private boolean isCancelled;

    public RegistryApplyEvent(Registry<T> target, DeferredRegistry<T> source) {
        this.target = target;
        this.source = source;
    }

    public Registry<T> target() {
        return target;
    }
    public DeferredRegistry<T> source() {
        return source;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
