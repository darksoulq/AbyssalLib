package com.github.darksoulq.abyssallib.server.event.custom.server;

import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public class ResourcePackCompileEvent extends Event {
    /**
     * Handler list for Bukkit's internal event system.
     */
    private static final HandlerList handlers = new HandlerList();

    private final String pluginId;
    private final ResourcePack pack;

    public ResourcePackCompileEvent(@NotNull String pluginId, @NotNull ResourcePack pack) {
        this.pluginId = pluginId;
        this.pack = pack;
    }


    public @NotNull String pluginId() {
        return pluginId;
    }
    public @NotNull ResourcePack pack() {
        return pack;
    }

    /**
     * Returns the list of handlers for this event.
     *
     * @return The {@link HandlerList} for this event.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() { return handlers; }
}
