package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PotionEffectReward implements AdvancementReward {

    public static final Codec<PotionEffectReward> CODEC = new Codec<>() {
        @Override
        public <D> PotionEffectReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String name = Codecs.STRING.decode(ops, map.get(ops.createString("effect")));
            PotionEffectType type = org.bukkit.Registry.POTION_EFFECT_TYPE.get(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
            int duration = Codecs.INT.decode(ops, map.get(ops.createString("duration")));
            int amplifier = Codecs.INT.decode(ops, map.get(ops.createString("amplifier")));
            return new PotionEffectReward(new PotionEffect(type, duration, amplifier));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, PotionEffectReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("effect"), Codecs.STRING.encode(ops, value.effect.getType().getKey().getKey()),
                ops.createString("duration"), Codecs.INT.encode(ops, value.effect.getDuration()),
                ops.createString("amplifier"), Codecs.INT.encode(ops, value.effect.getAmplifier())
            ));
        }
    };

    public static final RewardType<PotionEffectReward> TYPE = () -> CODEC;

    private final PotionEffect effect;

    public PotionEffectReward(PotionEffect effect) {
        this.effect = effect;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        player.addPotionEffect(effect);
    }
}