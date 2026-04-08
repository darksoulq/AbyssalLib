package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;

import java.util.Map;

public class LevelCriterion implements AdvancementCriterion {

    public static final Codec<LevelCriterion> CODEC = new Codec<>() {
        @Override
        public <D> LevelCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            int level = Codecs.INT.decode(ops, map.get(ops.createString("level")));
            return new LevelCriterion(level);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LevelCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("level"), Codecs.INT.encode(ops, value.level)
            ));
        }
    };

    public static final CriterionType<LevelCriterion> TYPE = () -> CODEC;

    private final int level;

    public LevelCriterion(int level) {
        this.level = level;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return player.getLevel() >= level;
    }
}