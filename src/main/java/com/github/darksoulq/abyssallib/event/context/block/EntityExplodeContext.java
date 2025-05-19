package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Context for {@link EntityExplodeEvent}, used to control whether blocks should be destroyed as a result of the explosion.
 */
public class EntityExplodeContext extends Context<EntityExplodeEvent> implements ExplodeContext {
    /**
     * Whether the block should be destroyed as a result of the explosion.
     */
    private boolean shouldExplode = false;

    /**
     * Constructs a new {@code EntityExplodeContext} for the given event.
     *
     * @param event the {@link EntityExplodeEvent} this context wraps
     */
    public EntityExplodeContext(EntityExplodeEvent event) {
        super(event);
    }

    /**
     * Returns whether the block should be destroyed during the explosion.
     *
     * @return {@code true} if the block should be destroyed, {@code false} otherwise
     */
    @Override
    public boolean shouldExplode() {
        return shouldExplode;
    }

    /**
     * Sets whether the block should be destroyed during the explosion.
     *
     * @param v {@code true} to allow destruction, {@code false} to prevent it
     */
    public void shouldExplode(boolean v) {
        shouldExplode = v;
    }
}
