package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public class CustomStatisticCriterion implements AdvancementCriterion {

    public static final Codec<CustomStatisticCriterion> CODEC = new Codec<>() {
        @Override
        public <D> CustomStatisticCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Statistic stat = Statistic.CODEC.decode(ops, map.get(ops.createString("statistic")));
            int threshold = Codecs.INT.decode(ops, map.get(ops.createString("threshold")));
            return new CustomStatisticCriterion(stat, threshold);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomStatisticCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("statistic"), Statistic.CODEC.encode(ops, value.stat),
                ops.createString("threshold"), Codecs.INT.encode(ops, value.threshold)
            ));
        }
    };

    public static final CriterionType<CustomStatisticCriterion> TYPE = () -> CODEC;

    private final Statistic stat;
    private final int threshold;

    public CustomStatisticCriterion(Statistic stat, int threshold) {
        this.stat = stat;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return PlayerStatistics.of(player).get(stat) >= threshold;
    }
}