package com.github.darksoulq.abyssallib.world.gen.state.provider;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Location;

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
    public static final Codec<BlockStateProvider> CODEC = Codec.dispatch(
        BlockStateProvider.class,
        "type",
        Codecs.STRING,
        provider -> {
            String typeId = Registries.BLOCK_STATE_PROVIDERS.getId(provider.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered block state provider type");
            }
            return typeId;
        },
        typeId -> {
            BlockStateProviderType<?> type = Registries.BLOCK_STATE_PROVIDERS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown block state provider type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("BlockStateProvider");

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