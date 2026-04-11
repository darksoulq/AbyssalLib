package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.Collection;
import java.util.Map;

public class LootTableReward implements AdvancementReward {

    public static final Codec<LootTableReward> CODEC = new Codec<>() {
        @Override
        public <D> LootTableReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String key = Codecs.STRING.decode(ops, map.get(ops.createString("loot_table")));
            LootTable table = Bukkit.getLootTable(NamespacedKey.fromString(key));
            return new LootTableReward(table);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootTableReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("loot_table"), Codecs.STRING.encode(ops, value.table.getKey().toString())
            ));
        }
    };

    public static final RewardType<LootTableReward> TYPE = () -> CODEC;

    private final LootTable table;

    public LootTableReward(LootTable table) {
        this.table = table;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        if (table == null) return;
        LootContext context = new LootContext.Builder(player.getLocation()).lootedEntity(player).build();
        Collection<ItemStack> items = table.populateLoot(new java.util.Random(), context);
        for (ItemStack item : items) {
            player.getInventory().addItem(item).values().forEach(remaining -> 
                player.getWorld().dropItem(player.getLocation(), remaining)
            );
        }
    }
}