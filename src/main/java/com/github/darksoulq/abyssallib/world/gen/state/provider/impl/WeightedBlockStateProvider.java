package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A block state provider that selects a block state from a pool based on relative weights.
 */
public class WeightedBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the weighted provider.
     */
    public static final Codec<WeightedBlockStateProvider> CODEC = new Codec<>() {

        /**
         * Decodes the provider from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the weighted block state provider.
         * @throws CodecException If the entries field is missing.
         */
        @Override
        public <D> WeightedBlockStateProvider decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<Entry> entries = Entry.CODEC.list().decode(ops, map.get(ops.createString("entries")));
            return new WeightedBlockStateProvider(entries);
        }

        /**
         * Encodes the provider into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The provider instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, WeightedBlockStateProvider value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("entries"), Entry.CODEC.list().encode(ops, value.entries));
            return ops.createMap(map);
        }
    };

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
        public static final Codec<Entry> CODEC = new Codec<>() {

            /**
             * Decodes an entry from a map.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new entry instance.
             * @throws CodecException If required fields are missing.
             */
            @Override
            public <D> Entry decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockInfo state = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("state")));
                int weight = Codecs.INT.decode(ops, map.get(ops.createString("weight")));
                return new Entry(state, weight);
            }

            /**
             * Encodes an entry into a map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The entry instance to encode.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Entry value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("state"), ExtraCodecs.BLOCK_INFO.encode(ops, value.state));
                map.put(ops.createString("weight"), Codecs.INT.encode(ops, value.weight));
                return ops.createMap(map);
            }
        };
    }
}