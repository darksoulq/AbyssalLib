package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on 2D simplex noise.
 */
public class NoiseThresholdModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the noise threshold modifier.
     */
    public static final Codec<NoiseThresholdModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the noise threshold modifier.
         * @throws CodecException If the required fields are missing.
         */
        @Override
        public <D> NoiseThresholdModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            double frequency = Codecs.DOUBLE.decode(ops, map.get(ops.createString("frequency")));
            double threshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("threshold")));
            boolean aboveThreshold = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("above_threshold")));
            return new NoiseThresholdModifier(frequency, threshold, aboveThreshold);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, NoiseThresholdModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("frequency"), Codecs.DOUBLE.encode(ops, value.frequency));
            map.put(ops.createString("threshold"), Codecs.DOUBLE.encode(ops, value.threshold));
            map.put(ops.createString("above_threshold"), Codecs.BOOLEAN.encode(ops, value.aboveThreshold));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the noise threshold placement modifier.
     */
    public static final PlacementModifierType<NoiseThresholdModifier> TYPE = () -> CODEC;

    /** The frequency multiplier applied to coordinates before sampling noise. */
    private final double frequency;

    /** The threshold value to compare the sampled noise against. */
    private final double threshold;

    /** Whether the noise value must be above or below the threshold. */
    private final boolean aboveThreshold;

    /** A lazily initialized noise generator tied to the world seed. */
    private transient SimplexNoiseGenerator noiseGenerator;

    /**
     * Constructs a new NoiseThresholdModifier.
     *
     * @param frequency      The sampling frequency.
     * @param threshold      The noise threshold.
     * @param aboveThreshold True if the noise must exceed the threshold, false if it must be below.
     */
    public NoiseThresholdModifier(double frequency, double threshold, boolean aboveThreshold) {
        this.frequency = frequency;
        this.threshold = threshold;
        this.aboveThreshold = aboveThreshold;
    }

    /**
     * Filters the incoming positions by evaluating a 2D simplex noise value at the given coordinates.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream containing only vectors that passed the noise check.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        if (noiseGenerator == null) {
            noiseGenerator = new SimplexNoiseGenerator(context.level().getWorld());
        }

        return positions.filter(pos -> {
            double noise = noiseGenerator.noise(pos.getBlockX() * frequency, pos.getBlockZ() * frequency);
            if (aboveThreshold) {
                return noise > threshold;
            } else {
                return noise < threshold;
            }
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this noise threshold modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}