package com.github.darksoulq.abyssallib.world.gen.state.provider;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Location;

import java.util.Map;
import java.util.Random;

/**
 * The base class for all block state providers.
 * <p>
 * Providers are used by features to dynamically determine which block state
 * to place at a specific coordinate, allowing for randomization, weighting,
 * and noise-driven terrain composition.
 */
public abstract class BlockStateProvider {

    /**
     * Polymorphic codec for serializing and deserializing any block state provider implementation.
     */
    public static final Codec<BlockStateProvider> CODEC = new Codec<>() {

        /**
         * Decodes a specific BlockStateProvider based on its registered type identifier.
         *
         * @param ops   The dynamic operations logic used to parse the input.
         * @param input The serialized input data representing the provider.
         * @param <D>   The type of the serialized data.
         * @return The decoded block state provider instance.
         * @throws CodecException If the type is missing or unknown.
         */
        @Override
        public <D> BlockStateProvider decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for BlockStateProvider"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type'");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            BlockStateProviderType<?> type = Registries.BLOCK_STATE_PROVIDERS.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown block state provider type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a BlockStateProvider, injecting its registered type ID into the resulting data.
         *
         * @param ops   The dynamic operations logic used to construct the output.
         * @param value The provider instance to encode.
         * @param <D>   The target type for the serialized data.
         * @return The encoded data object containing both provider data and its type identifier.
         * @throws CodecException If the provider type is not registered.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, BlockStateProvider value) throws CodecException {
            BlockStateProviderType<BlockStateProvider> type = (BlockStateProviderType<BlockStateProvider>) value.getType();
            String typeId = Registries.BLOCK_STATE_PROVIDERS.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered block state provider type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Provider codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Determines the specific block info to place at the given location.
     *
     * @param random   The deterministic random source.
     * @param location The absolute location where the block will be placed.
     * @return The selected block info.
     */
    public abstract BlockInfo getState(Random random, Location location);

    /**
     * Retrieves the provider type used for identifying this specific implementation.
     *
     * @return The block state provider type associated with this instance.
     */
    public abstract BlockStateProviderType<?> getType();
}