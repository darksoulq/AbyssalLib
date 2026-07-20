package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Set;

/**
 * An advancement criterion evaluating a standard vanilla Minecraft statistic.
 */
public class StatisticCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the vanilla statistic criterion.
     */
    public static final Codec<StatisticCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(Statistic.class).fieldOf("statistic").forGetter(StatisticCriterion.class, p -> p.statistic),
        Codecs.INT.fieldOf("threshold").forGetter(StatisticCriterion.class, p -> p.threshold)
    ).apply(instance, StatisticCriterion::new)).describe("StatisticCriterion");

    /**
     * The registered type definition for the vanilla statistic criterion.
     */
    public static final CriterionType<StatisticCriterion> TYPE = () -> CODEC;

    private final Statistic statistic;
    private final int threshold;

    /**
     * Constructs a new StatisticCriterion.
     *
     * @param statistic The vanilla statistic to track.
     * @param threshold The required value threshold.
     */
    public StatisticCriterion(Statistic statistic, int threshold) {
        this.statistic = statistic;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player's vanilla statistic value meets or exceeds the required threshold.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        return player.getStatistic(statistic) >= threshold;
    }
}