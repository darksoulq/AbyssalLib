package com.github.darksoulq.abyssallib.world.multiblock;

import com.github.darksoulq.abyssallib.world.multiblock.choice.EmptyChoice;
import org.bukkit.block.Block;

public abstract class MultiblockChoice {
    public abstract boolean matches(Block block);

    public static EmptyChoice empty() {
        return new EmptyChoice();
    }
}
