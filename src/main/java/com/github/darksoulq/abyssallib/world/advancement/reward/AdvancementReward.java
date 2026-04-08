package com.github.darksoulq.abyssallib.world.advancement.reward;

import org.bukkit.entity.Player;

/**
 * Represents a benefit or action granted to a player upon the completion of an advancement.
 * Rewards can range from simple experience points to complex item distributions or
 * custom command executions.
 */
public interface AdvancementReward {

    /**
     * Retrieves the reward type definition associated with this instance.
     * This is used to identify the logic and handle polymorphic serialization.
     *
     * @return
     * The {@link RewardType} characterizing this specific reward.
     */
    RewardType<?> getType();

    /**
     * Grants the reward to the specified player.
     * This method is triggered automatically by the progress tracker when an
     * advancement reaches a completed state.
     *
     * @param player
     * The {@link Player} who completed the advancement.
     */
    void grant(Player player);
}