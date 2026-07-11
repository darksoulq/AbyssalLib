package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;

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
    public static final Codec<NoiseCountModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.DOUBLE.fieldOf("frequency").forGetter(NoiseCountModifier.class, p -> p.frequency),
        Codecs.DOUBLE.fieldOf("threshold").forGetter(NoiseCountModifier.class, p -> p.threshold),
        Codecs.INT.fieldOf("count_above").forGetter(NoiseCountModifier.class, p -> p.countAbove),
        Codecs.INT.fieldOf("count_below").forGetter(NoiseCountModifier.class, p -> p.countBelow)
    ).apply(instance, NoiseCountModifier::new)).describe("NoiseCountModifier");

    /**
     * The registered type definition for the noise count placement modifier.
     */
    public static final PlacementModifierType<NoiseCountModifier> TYPE = () -> CODEC;

    /**
     * The frequency multiplier applied to coordinates before sampling the noise field.
     */
    private final double frequency;

    /**
     * The noise value boundary determining which count is used.
     */
    private final double threshold;

    /**
     * The number of positions to yield if the sampled noise is strictly greater than the threshold.
     */
    private final int countAbove;

    /**
     * The number of positions to yield if the sampled noise is less than or equal to the threshold.
     */
    private final int countBelow;

    /**
     * A lazily initialized noise generator tied to the world seed.
     */
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
            noiseGenerator = new SimplexNoiseGenerator(context.level().world());
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