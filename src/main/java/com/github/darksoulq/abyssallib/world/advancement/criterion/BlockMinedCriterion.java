package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Map;

public class BlockMinedCriterion implements AdvancementCriterion {

    public static final Codec<BlockMinedCriterion> CODEC = new Codec<>() {
        @Override
        public <D> BlockMinedCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Key blockId = Codecs.KEY.decode(ops, map.get(ops.createString("block")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new BlockMinedCriterion(blockId, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, BlockMinedCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("block"), Codecs.KEY.encode(ops, value.blockId),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<BlockMinedCriterion> TYPE = () -> CODEC;

    private final Key blockId;
    private final int amount;

    public BlockMinedCriterion(Key blockId, int amount) {
        this.blockId = blockId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.BLOCKS_MINED.get(blockId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }
}