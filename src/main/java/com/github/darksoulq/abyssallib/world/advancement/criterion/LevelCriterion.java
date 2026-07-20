package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.Set;

/**
 * An advancement criterion ensuring a player reaches a specified vanilla XP level.
 */
public class LevelCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the level criterion.
     */
    public static final Codec<LevelCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("level").forGetter(LevelCriterion.class, p -> p.level)
    ).apply(instance, LevelCriterion::new)).describe("LevelCriterion");

    /**
     * The registered type definition for the level criterion.
     */
    public static final CriterionType<LevelCriterion> TYPE = () -> CODEC;

    private final int level;

    /**
     * Constructs a new LevelCriterion.
     *
     * @param level The required vanilla XP level.
     */
    public LevelCriterion(int level) {
        this.level = level;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player's XP level meets or exceeds the required level.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        return player.getLevel() >= level;
    }

    @Override
    public Set<Class<? extends Event>> getTargetEvents() {
        return Set.of(PlayerLevelChangeEvent.class);
    }
}