package com.github.darksoulq.abyssallib.world.multiblock.choice;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.multiblock.MultiblockChoice;

import java.util.List;

/**
 * A multiblock choice implementation that matches against specific AbyssalLib CustomBlocks.
 * <p>
 * This choice allows a multiblock pattern to require either a single specific
 * {@link CustomBlock} or any block from a provided list of valid custom blocks.
 */
public class BlockChoice extends MultiblockChoice {

    /**
     * The list of custom blocks that are considered valid for this choice.
     */
    private final List<CustomBlock> expected;

    /**
     * Constructs a BlockChoice that requires a single specific CustomBlock.
     *
     * @param expected The custom block required by this choice.
     */
    public BlockChoice(CustomBlock expected) {
        this.expected = List.of(expected);
    }

    /**
     * Constructs a BlockChoice that accepts any CustomBlock from a provided list.
     *
     * @param expected The list of acceptable custom blocks.
     */
    public BlockChoice(List<CustomBlock> expected) {
        this.expected = expected;
    }

    /**
     * Retrieves the list of custom blocks that satisfy this requirement.
     *
     * @return The list of valid custom block choices.
     */
    public List<CustomBlock> getChoices() {
        return expected;
    }

    /**
     * Checks if the block in the world matches one of the expected CustomBlocks.
     * <p>
     * This method resolves the {@link CustomBlock} instance from the world {@link org.bukkit.block.Block}
     * and checks if that instance is contained within the allowed list.
     *
     * @param block The Bukkit block to validate.
     * @return {@code true} if the block is a valid CustomBlock for this choice; {@code false} otherwise.
     */
    @Override
    public boolean matches(org.bukkit.block.Block block) {
        CustomBlock provided = CustomBlock.from(block);
        return expected.contains(provided);
    }
}