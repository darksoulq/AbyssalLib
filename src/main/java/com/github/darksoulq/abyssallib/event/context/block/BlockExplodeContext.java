package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.event.block.BlockExplodeEvent;

/**
 * Context for {@link BlockExplodeEvent}, used to determine whether the block involved in the explosion should be destroyed.
 */
public class BlockExplodeContext extends Context<BlockExplodeEvent> implements ExplodeContext {
    /**
     * Whether the block should be destroyed as a result of the explosion.
     */
    private boolean shouldExplode = false;

    /**
     * Constructs a new {@code BlockExplodeContext} for the given event.
     *
     * @param event the {@link BlockExplodeEvent} this context wraps
     */
    public BlockExplodeContext(BlockExplodeEvent event) {
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
