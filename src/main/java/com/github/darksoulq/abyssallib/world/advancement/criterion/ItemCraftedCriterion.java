package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Map;

public class ItemCraftedCriterion implements AdvancementCriterion {

    public static final Codec<ItemCraftedCriterion> CODEC = new Codec<>() {
        @Override
        public <D> ItemCraftedCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            Key itemId = Codecs.KEY.decode(ops, map.get(ops.createString("item")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new ItemCraftedCriterion(itemId, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemCraftedCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("item"), Codecs.KEY.encode(ops, value.itemId),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<ItemCraftedCriterion> TYPE = () -> CODEC;

    private final Key itemId;
    private final int amount;

    public ItemCraftedCriterion(Key itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        Statistic stat = Statistics.ITEMS_CRAFTED.get(itemId);
        return PlayerStatistics.of(player).get(stat) >= amount;
    }
}