package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import org.bukkit.entity.Player;

/**
 * An advancement criterion evaluating a custom statistic tracked in the AbyssalLib database.
 */
public class CustomStatisticCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the custom statistic criterion.
     */
    public static final Codec<CustomStatisticCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Statistic.CODEC.fieldOf("statistic").forGetter(CustomStatisticCriterion.class, p -> p.stat),
        Codecs.INT.fieldOf("threshold").forGetter(CustomStatisticCriterion.class, p -> p.threshold)
    ).apply(instance, CustomStatisticCriterion::new)).describe("CustomStatisticCriterion");

    /**
     * The registered type definition for the custom statistic criterion.
     */
    public static final CriterionType<CustomStatisticCriterion> TYPE = () -> CODEC;

    private final Statistic stat;
    private final int threshold;

    /**
     * Constructs a new CustomStatisticCriterion.
     *
     * @param stat      The statistic to track.
     * @param threshold The required value.
     */
    public CustomStatisticCriterion(Statistic stat, int threshold) {
        this.stat = stat;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player has accumulated the required statistic amount.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        return PlayerStatistics.of(player).get(stat) >= threshold;
    }
}