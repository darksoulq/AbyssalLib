package com.github.darksoulq.abyssallib.world.gen.state.provider;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.state.provider.impl.NoiseThresholdBlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.impl.RotatedBlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.impl.SimpleBlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.impl.WeightedBlockStateProvider;

public class BlockStateProviders {
    public static final DeferredRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDERS = DeferredRegistry.create(Registries.BLOCK_STATE_PROVIDERS, AbyssalLib.PLUGIN_ID);

    public static final BlockStateProviderType<?> SIMPLE = BLOCK_STATE_PROVIDERS.register("simple", id -> SimpleBlockStateProvider.TYPE);
    public static final BlockStateProviderType<?> WEIGHTED = BLOCK_STATE_PROVIDERS.register("weighted", id -> WeightedBlockStateProvider.TYPE);
    public static final BlockStateProviderType<?> NOISE_THRESHOLD = BLOCK_STATE_PROVIDERS.register("noise_threshold", id -> NoiseThresholdBlockStateProvider.TYPE);
    public static final BlockStateProviderType<?> ROTATED = BLOCK_STATE_PROVIDERS.register("rotated", id -> RotatedBlockStateProvider.TYPE);
}