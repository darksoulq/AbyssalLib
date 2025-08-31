package com.github.darksoulq.abyssallib.world.multiblock.choice;

import com.github.darksoulq.abyssallib.world.multiblock.MultiblockChoice;
import org.bukkit.block.Block;

public class EmptyChoice extends MultiblockChoice {
    @Override
    public boolean matches(Block block) {
        return block.isEmpty();
    }
}
