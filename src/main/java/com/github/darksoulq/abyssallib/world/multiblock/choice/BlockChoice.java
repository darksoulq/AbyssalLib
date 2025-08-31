package com.github.darksoulq.abyssallib.world.multiblock.choice;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.multiblock.MultiblockChoice;

import java.util.List;

public class BlockChoice extends MultiblockChoice {
    private final List<CustomBlock> expected;

    public BlockChoice(CustomBlock expected) {
        this.expected = List.of(expected);
    }
    public BlockChoice(List<CustomBlock> expected) {
        this.expected = expected;
    }

    public List<CustomBlock> getChoices() {
        return expected;
    }

    @Override
    public boolean matches(org.bukkit.block.Block block) {
        CustomBlock provided = CustomBlock.from(block);
        return expected.contains(provided);
    }
}
