package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.level.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event triggered when a custom {@link Entity} is loaded into the world.
 * <p>
 * This event is fired after the entity has been deserialized and registered.
 * </p>
 */
public class EntityLoadEvent extends Event {

    /**
     * Handler list for Bukkit's internal event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The custom entity that was loaded.
     */
    private final Entity<? extends LivingEntity> entity;

    /**
     * Constructs a new {@code EntityLoadEvent}.
     *
     * @param entity The custom entity that was loaded into the world.
     */
    public EntityLoadEvent(@NotNull Entity<? extends LivingEntity> entity) {
        this.entity = entity;
    }

    /**
     * Gets the custom entity that was loaded.
     *
     * @return The loaded entity.
     */
    public @NotNull Entity<? extends LivingEntity> getEntity() {
        return entity;
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
}
