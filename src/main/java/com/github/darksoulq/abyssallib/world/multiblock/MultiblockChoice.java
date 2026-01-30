package com.github.darksoulq.abyssallib.world.multiblock;

import com.github.darksoulq.abyssallib.world.multiblock.choice.EmptyChoice;
import org.bukkit.block.Block;

/**
 * Represents an abstract requirement for a specific position within a multiblock pattern.
 * <p>
 * Implementations define whether a specific {@link Block} in the world satisfies the
 * condition required by the multiblock layout at that location.
 */
public abstract class MultiblockChoice {

    /**
     * Determines if the given world block matches this choice's requirements.
     *
     * @param block The block to check.
     * @return {@code true} if the block is a valid match; {@code false} otherwise.
     */
    public abstract boolean matches(Block block);

    /**
     * Creates a choice that requires the block position to be empty (air).
     *
     * @return A new {@link EmptyChoice} instance.
     */
    public static EmptyChoice empty() {
        return new EmptyChoice();
    }
}