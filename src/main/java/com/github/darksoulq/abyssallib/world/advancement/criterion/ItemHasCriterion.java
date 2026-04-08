package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemHasCriterion implements AdvancementCriterion {

    public static final Codec<ItemHasCriterion> CODEC = new Codec<>() {
        @Override
        public <D> ItemHasCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            ItemPredicate predicate = ItemPredicate.CODEC.decode(ops, map.get(ops.createString("predicate")));
            return new ItemHasCriterion(predicate);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemHasCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("predicate"), ItemPredicate.CODEC.encode(ops, value.predicate)
            ));
        }
    };

    public static final CriterionType<ItemHasCriterion> TYPE = () -> CODEC;

    private final ItemPredicate predicate;

    public ItemHasCriterion(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isMet(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && predicate.test(item)) {
                return true;
            }
        }
        return false;
    }
}