package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

/**
 * An advancement criterion tracking the total number of specific blocks mined by a player.
 */
public class BlockMinedCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the block mined criterion.
     */
    public static final Codec<BlockMinedCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("block").forGetter(BlockMinedCriterion.class, p -> p.blockId),
        Codecs.INT.fieldOf("amount").forGetter(BlockMinedCriterion.class, p -> p.amount)
    ).apply(instance, BlockMinedCriterion::new)).describe("BlockMinedCriterion");

    /**
     * The registered type definition for the block mined criterion.
     */
    public static final CriterionType<BlockMinedCriterion> TYPE = () -> CODEC;

    private final Key blockId;
    private final int amount;

    /**
     * Constructs a new BlockMinedCriterion.
     *
     * @param blockId The key of the block to track.
     * @param amount  The amount required.
     */
    public BlockMinedCriterion(Key blockId, int amount) {
        this.blockId = blockId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player has mined the required amount of the target block.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.BLOCKS_MINED.get(blockId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }
}