package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A block state provider that always returns a single, unchanging block state.
 */
public class SimpleBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the simple provider.
     */
    public static final Codec<SimpleBlockStateProvider> CODEC = new Codec<>() {

        /**
         * Decodes the provider from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the simple block state provider.
         * @throws CodecException If the state field is missing.
         */
        @Override
        public <D> SimpleBlockStateProvider decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            BlockInfo state = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("state")));
            return new SimpleBlockStateProvider(state);
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
        public <D> D encode(DynamicOps<D> ops, SimpleBlockStateProvider value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("state"), ExtraCodecs.BLOCK_INFO.encode(ops, value.state));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the simple block state provider.
     */
    public static final BlockStateProviderType<SimpleBlockStateProvider> TYPE = () -> CODEC;

    /** The constant block state to return. */
    private final BlockInfo state;

    /**
     * Constructs a new SimpleBlockStateProvider.
     *
     * @param state The static block info to provide.
     */
    public SimpleBlockStateProvider(BlockInfo state) {
        this.state = state;
    }

    /**
     * Returns the configured static block state regardless of location or randomness.
     *
     * @param random   The random source.
     * @param location The placement location.
     * @return The static block info.
     */
    @Override
    public BlockInfo getState(Random random, Location location) {
        return state;
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
}