package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

/**
 * An advancement criterion ensuring a player currently has a specific potion effect active.
 */
public class PotionEffectCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the potion effect criterion.
     */
    public static final Codec<PotionEffectCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        ExtraCodecs.POTION_EFFECT_TYPE.fieldOf("effect").forGetter(PotionEffectCriterion.class, p -> p.effect)
    ).apply(instance, PotionEffectCriterion::new)).describe("PotionEffectCriterion");

    /**
     * The registered type definition for the potion effect criterion.
     */
    public static final CriterionType<PotionEffectCriterion> TYPE = () -> CODEC;

    private final PotionEffectType effect;

    /**
     * Constructs a new PotionEffectCriterion.
     *
     * @param effect The required potion effect type.
     */
    public PotionEffectCriterion(PotionEffectType effect) {
        this.effect = effect;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player currently has the target potion effect active.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        return player.hasPotionEffect(effect);
    }

    @Override
    public Set<Class<? extends Event>> getTargetEvents() {
        return Set.of(EntityPotionEffectEvent.class);
    }
}