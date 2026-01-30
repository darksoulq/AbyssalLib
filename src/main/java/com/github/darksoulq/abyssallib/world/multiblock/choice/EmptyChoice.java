package com.github.darksoulq.abyssallib.world.multiblock.choice;

import com.github.darksoulq.abyssallib.world.multiblock.MultiblockChoice;
import org.bukkit.block.Block;

/**
 * A multiblock choice implementation that matches only if a block is empty.
 * <p>
 * This is used within multiblock patterns to ensure specific coordinates
 * contain air or are not occupied by solid blocks.
 */
public class EmptyChoice extends MultiblockChoice {

    /**
     * Checks if the given block is empty.
     *
     * @param block The block to check.
     * @return {@code true} if the block is empty; {@code false} otherwise.
     */
    @Override
    public boolean matches(Block block) {
        return block.isEmpty();
    }
}