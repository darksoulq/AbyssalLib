package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Location;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.HashMap;
import java.util.Map;
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
    public static final Codec<NoiseThresholdBlockStateProvider> CODEC = new Codec<>() {

        /**
         * Decodes the provider from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the noise threshold block state provider.
         * @throws CodecException If required fields are missing.
         */
        @Override
        public <D> NoiseThresholdBlockStateProvider decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            double scale = Codecs.DOUBLE.decode(ops, map.get(ops.createString("scale")));
            double threshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("threshold")));
            BlockInfo normalState = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("normal_state")));
            BlockInfo highState = ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("high_state")));
            return new NoiseThresholdBlockStateProvider(scale, threshold, normalState, highState);
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
        public <D> D encode(DynamicOps<D> ops, NoiseThresholdBlockStateProvider value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("scale"), Codecs.DOUBLE.encode(ops, value.scale));
            map.put(ops.createString("threshold"), Codecs.DOUBLE.encode(ops, value.threshold));
            map.put(ops.createString("normal_state"), ExtraCodecs.BLOCK_INFO.encode(ops, value.normalState));
            map.put(ops.createString("high_state"), ExtraCodecs.BLOCK_INFO.encode(ops, value.highState));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the noise threshold block state provider.
     */
    public static final BlockStateProviderType<NoiseThresholdBlockStateProvider> TYPE = () -> CODEC;

    /** The frequency multiplier applied to coordinates before sampling. */
    private final double scale;

    /** The breakpoint value evaluating which state is returned. */
    private final double threshold;

    /** The block state returned if the evaluated noise is less than or equal to the threshold. */
    private final BlockInfo normalState;

    /** The block state returned if the evaluated noise exceeds the threshold. */
    private final BlockInfo highState;

    /** A lazily initialized noise generator tied to the world seed. */
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