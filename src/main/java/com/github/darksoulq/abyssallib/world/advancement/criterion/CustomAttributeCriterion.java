package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.attribute.Attribute;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Map;

public class CustomAttributeCriterion implements AdvancementCriterion {

    public static final Codec<CustomAttributeCriterion> CODEC = new Codec<>() {
        @Override
        public <D> CustomAttributeCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Key attrKey = Codecs.KEY.decode(ops, map.get(ops.createString("attribute")));
            double threshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("threshold")));
            return new CustomAttributeCriterion(attrKey, threshold);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomAttributeCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("attribute"), Codecs.KEY.encode(ops, value.attrKey),
                ops.createString("threshold"), Codecs.DOUBLE.encode(ops, value.threshold)
            ));
        }
    };

    public static final CriterionType<CustomAttributeCriterion> TYPE = () -> CODEC;

    private final Key attrKey;
    private final double threshold;

    public CustomAttributeCriterion(Key attrKey, double threshold) {
        this.attrKey = attrKey;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Attribute attribute = Registries.ATTRIBUTES.get(attrKey.asString());
        if (attribute == null) {
            return false;
        }

        EntityAttributes attributes = EntityAttributes.of(player);
        return attributes.getValue(attribute) >= threshold;
    }
}