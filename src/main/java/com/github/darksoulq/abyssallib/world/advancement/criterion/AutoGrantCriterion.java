package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;

public class AutoGrantCriterion implements AdvancementCriterion {

    public static final Codec<AutoGrantCriterion> CODEC = new Codec<>() {
        @Override
        public <D> AutoGrantCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            return new AutoGrantCriterion();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, AutoGrantCriterion value) throws CodecException {
            return ops.empty();
        }
    };

    public static final CriterionType<AutoGrantCriterion> TYPE = () -> CODEC;

    public AutoGrantCriterion() {}

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return true;
    }
}