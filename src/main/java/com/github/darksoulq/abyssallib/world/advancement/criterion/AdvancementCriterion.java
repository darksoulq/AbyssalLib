package com.github.darksoulq.abyssallib.world.advancement.criterion;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Represents a logical condition that a player must fulfill to progress in an advancement.
 * Criteria can be evaluated statically or triggered by specific server events.
 */
public interface AdvancementCriterion {

    /**
     * Retrieves the type definition associated with this specific criterion instance.
     * This is used for identifying the logic and serialization codec.
     *
     * @return
     * The {@link CriterionType} characterizing this instance.
     */
    CriterionType<?> getType();

    /**
     * Evaluates whether the player currently meets the conditions of this criterion.
     * This is typically used for continuous or state-based checks.
     *
     * @param player
     * The {@link Player} to evaluate.
     * @return
     * True if the condition is met, false otherwise.
     */
    boolean isMet(Player player);

    /**
     * Evaluates whether the player meets the conditions based on a specific event trigger.
     *
     * @param player
     * The {@link Player} involved in the event.
     * @param event
     * The {@link Event} that triggered this evaluation.
     * @return
     * True if the event satisfies the criterion requirements.
     */
    default boolean isMet(Player player, Event event) {
        return isMet(player);
    }
}