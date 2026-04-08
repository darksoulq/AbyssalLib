package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatisticCriterion implements AdvancementCriterion {

    public static final Codec<StatisticCriterion> CODEC = new Codec<>() {
        @Override
        public <D> StatisticCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Statistic statistic = Codec.enumCodec(Statistic.class).decode(ops, map.get(ops.createString("statistic")));
            int threshold = Codecs.INT.decode(ops, map.get(ops.createString("threshold")));
            return new StatisticCriterion(statistic, threshold);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, StatisticCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("statistic"), Codec.enumCodec(Statistic.class).encode(ops, value.statistic),
                ops.createString("threshold"), Codecs.INT.encode(ops, value.threshold)
            ));
        }
    };

    public static final CriterionType<StatisticCriterion> TYPE = () -> CODEC;

    private final Statistic statistic;
    private final int threshold;

    public StatisticCriterion(Statistic statistic, int threshold) {
        this.statistic = statistic;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return player.getStatistic(statistic) >= threshold;
    }
}