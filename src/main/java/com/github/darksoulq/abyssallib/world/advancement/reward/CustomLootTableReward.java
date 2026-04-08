package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CustomLootTableReward implements AdvancementReward {

    public static final Codec<CustomLootTableReward> CODEC = new Codec<>() {
        @Override
        public <D> CustomLootTableReward decode(DynamicOps<D> ops, D input) throws CodecException {
            if (ops.getStringValue(input).isPresent()) {
                return new CustomLootTableReward(ops.getStringValue(input).get(), null);
            }

            Map<D, D> map = ops.getMap(input).orElseThrow();
            if (map.containsKey(ops.createString("id"))) {
                String id = Codecs.STRING.decode(ops, map.get(ops.createString("id")));
                return new CustomLootTableReward(id, null);
            }

            LootTable table = LootTable.CODEC.decode(ops, map.get(ops.createString("table")));
            return new CustomLootTableReward(null, table);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomLootTableReward value) throws CodecException {
            if (value.id != null) {
                return ops.createString(value.id);
            }
            return ops.createMap(Map.of(
                ops.createString("table"), LootTable.CODEC.encode(ops, value.table)
            ));
        }
    };

    public static final RewardType<CustomLootTableReward> TYPE = () -> CODEC;

    private final String id;
    private final LootTable table;

    public CustomLootTableReward(String id, LootTable table) {
        this.id = id;
        this.table = table;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public void grant(Player player) {
        LootTable targetTable = this.table;
        if (targetTable == null && this.id != null) {
            targetTable = Registries.LOOT_TABLES.get(this.id);
        }

        if (targetTable != null) {
            LootContext context = LootContext.builder(player.getLocation()).looter(player).build();
            List<ItemStack> items = targetTable.generate(context);
            for (ItemStack item : items) {
                if (item != null && !item.getType().isAir()) {
                    player.getInventory().addItem(item).values().forEach(remaining ->
                        player.getWorld().dropItem(player.getLocation(), remaining)
                    );
                }
            }
        }
    }
}