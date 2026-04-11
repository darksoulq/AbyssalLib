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

public class EntityKilledCriterion implements AdvancementCriterion {

    public static final Codec<EntityKilledCriterion> CODEC = new Codec<>() {
        @Override
        public <D> EntityKilledCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Key entityId = Codecs.KEY.decode(ops, map.get(ops.createString("entity")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new EntityKilledCriterion(entityId, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, EntityKilledCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("entity"), Codecs.KEY.encode(ops, value.entityId),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<EntityKilledCriterion> TYPE = () -> CODEC;

    private final Key entityId;
    private final int amount;

    public EntityKilledCriterion(Key entityId, int amount) {
        this.entityId = entityId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.ENTITIES_KILLED.get(entityId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }
}