package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Map;

public class CustomStatisticCriterion implements AdvancementCriterion {

    public static final Codec<CustomStatisticCriterion> CODEC = new Codec<>() {
        @Override
        public <D> CustomStatisticCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Key statId = Codecs.KEY.decode(ops, map.get(ops.createString("statistic")));
            float threshold = Codecs.FLOAT.decode(ops, map.get(ops.createString("threshold")));
            return new CustomStatisticCriterion(statId, threshold);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomStatisticCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("statistic"), Codecs.KEY.encode(ops, value.statId),
                ops.createString("threshold"), Codecs.FLOAT.encode(ops, value.threshold)
            ));
        }
    };

    public static final CriterionType<CustomStatisticCriterion> TYPE = () -> CODEC;

    private final Key statId;
    private final float threshold;

    public CustomStatisticCriterion(Key statId, float threshold) {
        this.statId = statId;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Statistic stat = PlayerStatistics.of(player).get(statId);
        if (stat == null) return false;
        
        Object val = stat.getValue();
        if (val instanceof Number n) {
            return n.floatValue() >= threshold;
        } else if (val instanceof Boolean b) {
            return (threshold > 0) == b;
        }
        return false;
    }
}