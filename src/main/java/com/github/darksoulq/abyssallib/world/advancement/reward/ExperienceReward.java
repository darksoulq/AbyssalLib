package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;

import java.util.Map;

public class ExperienceReward implements AdvancementReward {

    public static final Codec<ExperienceReward> CODEC = new Codec<>() {
        @Override
        public <D> ExperienceReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new ExperienceReward(amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ExperienceReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final RewardType<ExperienceReward> TYPE = () -> CODEC;

    private final int amount;

    public ExperienceReward(int amount) {
        this.amount = amount;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        player.giveExp(amount);
    }
}