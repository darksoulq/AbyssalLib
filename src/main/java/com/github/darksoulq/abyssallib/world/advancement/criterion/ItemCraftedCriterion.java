package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemCraftedCriterion implements AdvancementCriterion {

    public static final Codec<ItemCraftedCriterion> CODEC = new Codec<>() {
        @Override
        public <D> ItemCraftedCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            ItemPredicate predicate = ItemPredicate.CODEC.decode(ops, map.get(ops.createString("predicate")));
            int amount = Codecs.INT.decode(ops, map.get(ops.createString("amount")));
            return new ItemCraftedCriterion(predicate, amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemCraftedCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("predicate"), ItemPredicate.CODEC.encode(ops, value.predicate),
                ops.createString("amount"), Codecs.INT.encode(ops, value.amount)
            ));
        }
    };

    public static final CriterionType<ItemCraftedCriterion> TYPE = () -> CODEC;

    private final ItemPredicate predicate;
    private final int amount;

    public ItemCraftedCriterion(ItemPredicate predicate, int amount) {
        this.predicate = predicate;
        this.amount = amount;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        return false;
    }

    @Override
    public boolean isMet(Player player, Event event) {
        if (event instanceof CraftItemEvent craftEvent) {
            ItemStack result = craftEvent.getRecipe().getResult();
            return predicate.test(result);
        }
        return false;
    }
}