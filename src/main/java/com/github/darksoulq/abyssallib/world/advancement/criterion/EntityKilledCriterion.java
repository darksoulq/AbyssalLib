package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Set;

/**
 * An advancement criterion tracking the amount of a specific entity type killed by the player.
 */
public class EntityKilledCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the entity killed criterion.
     */
    public static final Codec<EntityKilledCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("entity").forGetter(EntityKilledCriterion.class, p -> p.entityId),
        Codecs.INT.fieldOf("amount").forGetter(EntityKilledCriterion.class, p -> p.amount)
    ).apply(instance, EntityKilledCriterion::new)).describe("EntityKilledCriterion");

    /**
     * The registered type definition for the entity killed criterion.
     */
    public static final CriterionType<EntityKilledCriterion> TYPE = () -> CODEC;

    private final Key entityId;
    private final int amount;

    /**
     * Constructs a new EntityKilledCriterion.
     *
     * @param entityId The key of the entity to track.
     * @param amount   The amount required.
     */
    public EntityKilledCriterion(Key entityId, int amount) {
        this.entityId = entityId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player has killed the required amount of the target entity.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.ENTITIES_KILLED.get(entityId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }

    @Override
    public Set<Class<? extends Event>> getTargetEvents() {
        return Set.of(PlayerStatisticChangeEvent.class);
    }
}