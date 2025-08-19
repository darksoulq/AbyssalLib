package com.github.darksoulq.abyssallib.world.level.multiblock.choice;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.multiblock.MultiblockChoice;

import java.util.List;

public class BlockChoice extends MultiblockChoice {
    private final List<Block> expected;

    public BlockChoice(Block expected) {
        this.expected = List.of(expected);
    }
    public BlockChoice(List<Block> expected) {
        this.expected = expected;
    }

    public List<Block> getChoices() {
        return expected;
    }

    @Override
    public boolean matches(org.bukkit.block.Block block) {
        Block provided = Block.from(block);
        return expected.contains(provided);
    }
}
