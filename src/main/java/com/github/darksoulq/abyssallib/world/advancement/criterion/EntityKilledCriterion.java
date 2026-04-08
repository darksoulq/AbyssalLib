package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Map;

public class EntityKilledCriterion implements AdvancementCriterion {

    public static final Codec<EntityKilledCriterion> CODEC = new Codec<>() {
        @Override
        public <D> EntityKilledCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            EntityType type = Codec.enumCodec(EntityType.class).decode(ops, map.get(ops.createString("entity_type")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new EntityKilledCriterion(type, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, EntityKilledCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("entity_type"), Codec.enumCodec(EntityType.class).encode(ops, value.type),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<EntityKilledCriterion> TYPE = () -> CODEC;

    private final EntityType type;
    private final int amount;

    public EntityKilledCriterion(EntityType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return player.getStatistic(Statistic.KILL_ENTITY, type) >= amount;
    }
}