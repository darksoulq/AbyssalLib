package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A block state provider that selects a block state from a pool based on relative weights.
 */
public class WeightedBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the weighted provider.
     */
    public static final Codec<WeightedBlockStateProvider> CODEC = RecordBuilder.create(instance -> instance.group(
        Entry.CODEC.list().optionalFieldOf("entries", Collections.emptyList()).forGetter(WeightedBlockStateProvider.class, p -> p.entries)
    ).apply(instance, WeightedBlockStateProvider::new)).describe("WeightedBlockStateProvider");

    /**
     * The registered type definition for the weighted block state provider.
     */
    public static final BlockStateProviderType<WeightedBlockStateProvider> TYPE = () -> CODEC;

    /** The pool of weighted block states. */
    private final List<Entry> entries;

    /** The cached sum of all entry weights. */
    private final int totalWeight;

    /**
     * Constructs a new WeightedBlockStateProvider.
     *
     * @param entries The list of weighted entries to select from.
     */
    public WeightedBlockStateProvider(List<Entry> entries) {
        this.entries = entries;
        this.totalWeight = entries.stream().mapToInt(Entry::weight).sum();
    }

    /**
     * Randomly selects a block state from the pool based on relative weight.
     *
     * @param random   The random source.
     * @param location The placement location.
     * @return The selected block info, or null if the pool is empty.
     */
    @Override
    public BlockInfo getState(Random random, Location location) {
        if (totalWeight <= 0 || entries.isEmpty()) return null;

        int roll = random.nextInt(totalWeight);
        int current = 0;

        for (Entry entry : entries) {
            current += entry.weight();
            if (roll < current) {
                return entry.state();
            }
        }

        return entries.get(entries.size() - 1).state();
    }

    /**
     * Retrieves the specific type definition for this provider.
     *
     * @return The block state provider type.
     */
    @Override
    public BlockStateProviderType<?> getType() {
        return TYPE;
    }

    /**
     * Represents a single weighted option in the provider pool.
     *
     * @param state  The block info to return.
     * @param weight The relative probability weight of this entry.
     */
    public record Entry(BlockInfo state, int weight) {

        /**
         * The codec for serializing and deserializing a weighted entry.
         */
        public static final Codec<Entry> CODEC = RecordBuilder.create(instance -> instance.group(
            ExtraCodecs.BLOCK_INFO.fieldOf("state").forGetter(Entry::state),
            Codecs.INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(instance, Entry::new)).describe("WeightedEntry");
    }
}