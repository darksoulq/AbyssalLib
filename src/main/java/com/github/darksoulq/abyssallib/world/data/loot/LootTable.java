package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a complete loot table that can generate a list of items based on a provided context.
 * <p>
 * A loot table consists of multiple {@link LootPool}s. It can also be configured to
 * override or merge with a vanilla Minecraft loot table identifier.
 */
public class LootTable {
    /** The list of loot pools that make up this table. */
    private final List<LootPool> pools;
    /** The strategy used when this table interacts with other loot tables. */
    private final MergeStrategy mergeStrategy;
    /** The optional vanilla loot table ID this table targets (e.g., "minecraft:chests/simple_dungeon"). */
    private final String vanillaId;

    /**
     * Constructs a new LootTable.
     *
     * @param pools         The list of {@link LootPool}s to evaluate.
     * @param mergeStrategy The {@link MergeStrategy} for table composition.
     * @param vanillaId     The optional vanilla identifier, may be {@code null}.
     */
    public LootTable(List<LootPool> pools, MergeStrategy mergeStrategy, @Nullable String vanillaId) {
        this.pools = pools;
        this.mergeStrategy = mergeStrategy;
        this.vanillaId = vanillaId;
    }

    /**
     * Generates a list of items by evaluating all pools within this table.
     *
     * @param context The {@link LootContext} containing environment and player data.
     * @return A {@link List} of generated {@link ItemStack}s.
     */
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> items = new ArrayList<>();
        for (LootPool pool : pools) {
            pool.generate(context, items::add);
        }
        return items;
    }

    /** @return The strategy used for merging loot data. */
    public MergeStrategy getMergeStrategy() {
        return mergeStrategy;
    }

    /** @return The targeted vanilla loot ID, or {@code null} if none. */
    @Nullable
    public String getVanillaId() {
        return vanillaId;
    }

    /** Codec for serializing and deserializing {@link LootTable} instances. */
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