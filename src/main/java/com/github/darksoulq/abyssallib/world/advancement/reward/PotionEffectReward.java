package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * An advancement reward that applies a specific status effect to the player.
 */
public class PotionEffectReward implements AdvancementReward {

    /**
     * The codec used for serializing and deserializing the potion effect reward.
     */
    public static final Codec<PotionEffectReward> CODEC = ExtraCodecs.POTION_EFFECT.xmap(
        PotionEffectReward::new,
        reward -> reward.effect
    ).describe("PotionEffectReward");

    /**
     * The registered type definition for the potion effect reward.
     */
    public static final RewardType<PotionEffectReward> TYPE = () -> CODEC;

    private final PotionEffect effect;

    /**
     * Constructs a new PotionEffectReward.
     *
     * @param effect The potion effect to apply.
     */
    public PotionEffectReward(PotionEffect effect) {
        this.effect = effect;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Applies the configured potion effect to the player.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        player.addPotionEffect(effect);
    }
}