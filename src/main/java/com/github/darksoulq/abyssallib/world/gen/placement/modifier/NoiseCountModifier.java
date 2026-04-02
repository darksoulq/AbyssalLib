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
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A placement modifier that duplicates the incoming position stream a variable number
 * of times based on a 2D simplex noise evaluation.
 * <p>
 * This allows features to generate in dense "patches" that organically fade into
 * sparse patches across the terrain, rather than having a static count per chunk.
 */
public class NoiseCountModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the noise count modifier.
     */
    public static final Codec<NoiseCountModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the noise count modifier.
         * @throws CodecException If any required fields are missing.
         */
        @Override
        public <D> NoiseCountModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            double frequency = Codecs.DOUBLE.decode(ops, map.get(ops.createString("frequency")));
            double threshold = Codecs.DOUBLE.decode(ops, map.get(ops.createString("threshold")));
            int countAbove = Codecs.INT.decode(ops, map.get(ops.createString("count_above")));
            int countBelow = Codecs.INT.decode(ops, map.get(ops.createString("count_below")));
            return new NoiseCountModifier(frequency, threshold, countAbove, countBelow);
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
        public <D> D encode(DynamicOps<D> ops, NoiseCountModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("frequency"), Codecs.DOUBLE.encode(ops, value.frequency));
            map.put(ops.createString("threshold"), Codecs.DOUBLE.encode(ops, value.threshold));
            map.put(ops.createString("count_above"), Codecs.INT.encode(ops, value.countAbove));
            map.put(ops.createString("count_below"), Codecs.INT.encode(ops, value.countBelow));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the noise count placement modifier.
     */
    public static final PlacementModifierType<NoiseCountModifier> TYPE = () -> CODEC;

    /** The frequency multiplier applied to coordinates before sampling the noise field. */
    private final double frequency;

    /** The noise value boundary determining which count is used. */
    private final double threshold;

    /** The number of positions to yield if the sampled noise is strictly greater than the threshold. */
    private final int countAbove;

    /** The number of positions to yield if the sampled noise is less than or equal to the threshold. */
    private final int countBelow;

    /** A lazily initialized noise generator tied to the world seed. */
    private transient SimplexNoiseGenerator noiseGenerator;

    /**
     * Constructs a new NoiseCountModifier.
     *
     * @param frequency  The frequency scale of the noise map.
     * @param threshold  The breakpoint value of the noise evaluation.
     * @param countAbove The multiplier applied when noise exceeds the threshold.
     * @param countBelow The multiplier applied when noise is below or equal to the threshold.
     */
    public NoiseCountModifier(double frequency, double threshold, int countAbove, int countBelow) {
        this.frequency = frequency;
        this.threshold = threshold;
        this.countAbove = countAbove;
        this.countBelow = countBelow;
    }

    /**
     * Evaluates the noise map at the provided coordinate and multiplies the position stream accordingly.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors duplicated by the calculated noise-dependent count.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        if (noiseGenerator == null) {
            noiseGenerator = new SimplexNoiseGenerator(context.level().getWorld());
        }

        return positions.flatMap(pos -> {
            double noise = noiseGenerator.noise(pos.getBlockX() * frequency, pos.getBlockZ() * frequency);
            int finalCount = noise > threshold ? countAbove : countBelow;
            return IntStream.range(0, finalCount).mapToObj(i -> pos.clone());
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this noise count modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}