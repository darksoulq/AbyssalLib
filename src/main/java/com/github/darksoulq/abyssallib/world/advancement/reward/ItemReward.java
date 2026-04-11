package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemReward implements AdvancementReward {

    public static final Codec<ItemReward> CODEC = new Codec<>() {
        @Override
        public <D> ItemReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            ItemStack item = Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("item")));
            return new ItemReward(item);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("item"), Codecs.ITEM_STACK.encode(ops, value.item)
            ));
        }
    };

    public static final RewardType<ItemReward> TYPE = () -> CODEC;

    private final ItemStack item;

    public ItemReward(ItemStack item) {
        this.item = item;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        player.getInventory().addItem(item).values().forEach(remaining -> 
            player.getWorld().dropItem(player.getLocation(), remaining)
        );
    }
}