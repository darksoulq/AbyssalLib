package com.github.darksoulq.abyssallib.server.event.custom.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RecipeReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public RecipeReloadEvent() {}

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}