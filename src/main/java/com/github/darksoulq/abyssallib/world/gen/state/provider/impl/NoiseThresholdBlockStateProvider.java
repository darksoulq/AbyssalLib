package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.Random;

/**
 * A block state provider that alternates between two block states based on 2D simplex noise.
 * <p>
 * This is used to create organic clusters, such as large patches of coarse dirt
 * intertwined naturally with regular dirt or grass.
 */
public class NoiseThresholdBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the noise threshold provider.
     */
    public static final Codec<NoiseThresholdBlockStateProvider> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.DOUBLE.fieldOf("scale").forGetter(NoiseThresholdBlockStateProvider.class, p -> p.scale),
        Codecs.DOUBLE.fieldOf("threshold").forGetter(NoiseThresholdBlockStateProvider.class, p -> p.threshold),
        ExtraCodecs.BLOCK_INFO.fieldOf("normal_state").forGetter(NoiseThresholdBlockStateProvider.class, p -> p.normalState),
        ExtraCodecs.BLOCK_INFO.fieldOf("high_state").forGetter(NoiseThresholdBlockStateProvider.class, p -> p.highState)
    ).apply(instance, NoiseThresholdBlockStateProvider::new)).describe("NoiseThresholdBlockStateProvider");

    /**
     * The registered type definition for the noise threshold block state provider.
     */
    public static final BlockStateProviderType<NoiseThresholdBlockStateProvider> TYPE = () -> CODEC;

    /**
     * The frequency multiplier applied to coordinates before sampling.
     */
    private final double scale;

    /**
     * The breakpoint value evaluating which state is returned.
     */
    private final double threshold;

    /**
     * The block state returned if the evaluated noise is less than or equal to the threshold.
     */
    private final BlockInfo normalState;

    /**
     * The block state returned if the evaluated noise exceeds the threshold.
     */
    private final BlockInfo highState;

    /**
     * A lazily initialized noise generator tied to the world seed.
     */
    private transient SimplexNoiseGenerator noiseGenerator;

    /**
     * Constructs a new NoiseThresholdBlockStateProvider.
     *
     * @param scale       The sampling frequency.
     * @param threshold   The noise threshold boundary.
     * @param normalState The state below or at the threshold.
     * @param highState   The state above the threshold.
     */
    public NoiseThresholdBlockStateProvider(double scale, double threshold, BlockInfo normalState, BlockInfo highState) {
        this.scale = scale;
        this.threshold = threshold;
        this.normalState = normalState;
        this.highState = highState;
    }

    /**
     * Evaluates 2D simplex noise at the given location to determine the correct block state.
     *
     * @param random   The random source.
     * @param location The placement location.
     * @return The selected block info based on the noise map.
     */
    @Override
    public BlockInfo getState(Random random, Location location) {
        if (noiseGenerator == null && location.getWorld() != null) {
            noiseGenerator = new SimplexNoiseGenerator(location.getWorld());
        }

        if (noiseGenerator == null) {
            return normalState;
        }

        double noise = noiseGenerator.noise(location.getBlockX() * scale, location.getBlockZ() * scale);
        return noise > threshold ? highState : normalState;
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