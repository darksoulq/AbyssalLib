package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PotionEffectCriterion implements AdvancementCriterion {

    public static final Codec<PotionEffectCriterion> CODEC = new Codec<>() {
        @Override
        public <D> PotionEffectCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            PotionEffectType type = ExtraCodecs.POTION_EFFECT_TYPE.decode(ops, map.get(ops.createString("effect")));
            return new PotionEffectCriterion(type);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, PotionEffectCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("effect"), ExtraCodecs.POTION_EFFECT_TYPE.encode(ops, value.effect)
            ));
        }
    };

    public static final CriterionType<PotionEffectCriterion> TYPE = () -> CODEC;

    private final PotionEffectType effect;

    public PotionEffectCriterion(PotionEffectType effect) {
        this.effect = effect;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return player.hasPotionEffect(effect);
    }
}