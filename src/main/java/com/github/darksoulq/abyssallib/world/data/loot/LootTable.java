package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class LootTable {
    private final List<LootPool> pools;
    private final MergeStrategy mergeStrategy;
    private final String vanillaId;

    public LootTable(List<LootPool> pools, MergeStrategy mergeStrategy, @Nullable String vanillaId) {
        this.pools = pools;
        this.mergeStrategy = mergeStrategy;
        this.vanillaId = vanillaId;
    }

    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> items = new ArrayList<>();
        for (LootPool pool : pools) {
            pool.generate(context, items::add);
        }
        return items;
    }

    public MergeStrategy getMergeStrategy() {
        return mergeStrategy;
    }

    @Nullable
    public String getVanillaId() {
        return vanillaId;
    }

    public static final Codec<LootTable> CODEC = new Codec<>() {
        @Override
        public <D> LootTable decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<LootPool> pools = LootPool.CODEC.list().decode(ops, map.get(ops.createString("pools")));
            MergeStrategy strategy = Codec.enumCodec(MergeStrategy.class).orElse(MergeStrategy.NONE).decode(ops, map.get(ops.createString("merge_strategy")));
            Optional<String> vid = Codecs.STRING.optional().decode(ops, map.get(ops.createString("vanilla_id")));
            return new LootTable(pools, strategy, vid.orElse(null));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootTable value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("pools"), LootPool.CODEC.list().encode(ops, value.pools));
            map.put(ops.createString("merge_strategy"), Codec.enumCodec(MergeStrategy.class).encode(ops, value.mergeStrategy));
            if (value.vanillaId != null) {
                map.put(ops.createString("vanilla_id"), Codecs.STRING.encode(ops, value.vanillaId));
            }
            return ops.createMap(map);
        }
    };
}