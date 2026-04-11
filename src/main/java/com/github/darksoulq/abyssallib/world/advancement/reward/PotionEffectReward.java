package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PotionEffectReward implements AdvancementReward {

    public static final Codec<PotionEffectReward> CODEC = ExtraCodecs.POTION_EFFECT.xmap(
        PotionEffectReward::new,
        reward -> reward.effect
    );

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