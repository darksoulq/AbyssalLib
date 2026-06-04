package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import org.bukkit.entity.Player;

/**
 * An advancement reward that grants a fixed amount of experience points to the player.
 */
public class ExperienceReward implements AdvancementReward {

    /**
     * The codec used for serializing and deserializing the experience reward.
     */
    public static final Codec<ExperienceReward> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("amount").forGetter(ExperienceReward.class, p -> p.amount)
    ).apply(instance, ExperienceReward::new)).describe("ExperienceReward");

    /**
     * The registered type definition for the experience reward.
     */
    public static final RewardType<ExperienceReward> TYPE = () -> CODEC;

    private final int amount;

    /**
     * Constructs a new ExperienceReward.
     *
     * @param amount The integer amount of experience points to grant.
     */
    public ExperienceReward(int amount) {
        this.amount = amount;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Adds the experience amount directly to the player.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        player.giveExp(amount);
    }
}