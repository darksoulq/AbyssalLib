package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Defines the registration metadata and serialization logic for a specific {@link AdvancementReward}.
 *
 * @param <T>
 * The specific implementation type of the advancement reward.
 */
public interface RewardType<T extends AdvancementReward> {

    /**
     * Retrieves the codec responsible for converting this reward type to and from
     * serialized data formats.
     *
     * @return
     * The {@link Codec} instance for reward implementation {@code T}.
     */
    Codec<T> getCodec();
}