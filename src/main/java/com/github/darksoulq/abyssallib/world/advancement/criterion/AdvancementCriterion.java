package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Represents a logical condition that a player must fulfill to progress in an advancement.
 * Criteria can be evaluated statically or triggered by specific server events.
 */
public interface AdvancementCriterion {

    /**
     * Polymorphic codec governing precise evaluation boundaries mapped accurately.
     */
    Codec<AdvancementCriterion> CODEC = Codec.dispatch(
        AdvancementCriterion.class,
        "type",
        Codecs.STRING,
        criterion -> {
            String typeId = Registries.CRITERION.getId(criterion.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered advancement criterion type");
            }
            return typeId;
        },
        typeId -> {
            CriterionType<?> type = Registries.CRITERION.get(typeId);
            if (type == null) {
                return Codec.error("Unknown advancement criterion type: " + typeId);
            }
            return type.getCodec().unchecked();
        }
    ).describe("AdvancementCriterion");

    /**
     * Retrieves the type definition associated with this specific criterion instance.
     * This is used for identifying the logic and serialization codec.
     *
     * @return The {@link CriterionType} characterizing this instance.
     */
    CriterionType<?> getType();

    /**
     * Evaluates whether the player currently meets the conditions of this criterion.
     * This is typically used for continuous or state-based checks.
     *
     * @param player The {@link Player} to evaluate.
     * @return True if the condition is met, false otherwise.
     */
    boolean isMet(Player player);

    /**
     * Evaluates whether the player meets the conditions based on a specific event trigger.
     *
     * @param player The {@link Player} involved in the event.
     * @param event  The {@link Event} that triggered this evaluation.
     * @return True if the event satisfies the criterion requirements.
     */
    default boolean isMet(Player player, Event event) {
        return isMet(player);
    }
}