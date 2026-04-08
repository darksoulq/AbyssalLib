package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PotionEffectCriterion implements AdvancementCriterion {

    public static final Codec<PotionEffectCriterion> CODEC = new Codec<>() {
        @Override
        public <D> PotionEffectCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String name = Codecs.STRING.decode(ops, map.get(ops.createString("effect")));
            PotionEffectType type = org.bukkit.Registry.POTION_EFFECT_TYPE.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
            return new PotionEffectCriterion(type);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, PotionEffectCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("effect"), Codecs.STRING.encode(ops, value.effect.getKey().getKey())
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