package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import org.bukkit.entity.Player;

import java.util.Map;

public class CustomAttributeCriterion implements AdvancementCriterion {

    public static final Codec<CustomAttributeCriterion> CODEC = new Codec<>() {
        @Override
        public <D> CustomAttributeCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String attrKey = Codecs.STRING.decode(ops, map.get(ops.createString("attribute")));
            double threshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("threshold")));
            return new CustomAttributeCriterion(attrKey, threshold);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomAttributeCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("attribute"), Codecs.STRING.encode(ops, value.attrKey),
                ops.createString("threshold"), Codecs.DOUBLE.encode(ops, value.threshold)
            ));
        }
    };

    public static final CriterionType<CustomAttributeCriterion> TYPE = () -> CODEC;

    private final String attrKey;
    private final double threshold;

    public CustomAttributeCriterion(String attrKey, double threshold) {
        this.attrKey = attrKey;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Map<String, String> attrs = EntityAttributes.of(player).getAllAttributes();
        if (!attrs.containsKey(attrKey)) return false;
        try {
            double val = Double.parseDouble(attrs.get(attrKey));
            return val >= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}