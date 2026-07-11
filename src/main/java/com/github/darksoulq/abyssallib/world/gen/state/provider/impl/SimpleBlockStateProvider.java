package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;

import java.util.Random;

/**
 * A block state provider that always returns a single, unchanging block state.
 */
public class SimpleBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the simple provider.
     */
    public static final Codec<SimpleBlockStateProvider> CODEC = RecordBuilder.create(instance -> instance.group(
        ExtraCodecs.BLOCK_INFO.fieldOf("state").forGetter(SimpleBlockStateProvider.class, p -> p.state)
    ).apply(instance, SimpleBlockStateProvider::new)).describe("SimpleBlockStateProvider");

    /**
     * The registered type definition for the simple block state provider.
     */
    public static final BlockStateProviderType<SimpleBlockStateProvider> TYPE = () -> CODEC;

    /**
     * The constant block state to return.
     */
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