package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import org.bukkit.entity.Player;

/**
 * An advancement criterion that automatically evaluates to true.
 * Used for granting initial root advancements or immediate rewards.
 */
public class AutoGrantCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the auto-grant criterion.
     */
    public static final Codec<AutoGrantCriterion> CODEC = Codec.unit(AutoGrantCriterion::new).describe("AutoGrantCriterion");

    /**
     * The registered type definition for the auto-grant criterion.
     */
    public static final CriterionType<AutoGrantCriterion> TYPE = () -> CODEC;

    /**
     * Constructs a new AutoGrantCriterion.
     */
    public AutoGrantCriterion() {}

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Always evaluates to true.
     *
     * @param player The player to evaluate.
     * @return true.
     */
    @Override
    public boolean isMet(Player player) {
        return true;
    }
}