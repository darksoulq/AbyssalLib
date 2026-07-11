package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on 2D simplex noise.
 */
public class NoiseThresholdModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the noise threshold modifier.
     */
    public static final Codec<NoiseThresholdModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.DOUBLE.fieldOf("frequency").forGetter(NoiseThresholdModifier.class, p -> p.frequency),
        Codecs.DOUBLE.fieldOf("threshold").forGetter(NoiseThresholdModifier.class, p -> p.threshold),
        Codecs.BOOLEAN.fieldOf("above_threshold").forGetter(NoiseThresholdModifier.class, p -> p.aboveThreshold)
    ).apply(instance, NoiseThresholdModifier::new)).describe("NoiseThresholdModifier");

    /**
     * The registered type definition for the noise threshold placement modifier.
     */
    public static final PlacementModifierType<NoiseThresholdModifier> TYPE = () -> CODEC;

    /**
     * The frequency multiplier applied to coordinates before sampling noise.
     */
    private final double frequency;

    /**
     * The threshold value to compare the sampled noise against.
     */
    private final double threshold;

    /**
     * Whether the noise value must be above or below the threshold.
     */
    private final boolean aboveThreshold;

    /**
     * A lazily initialized noise generator tied to the world seed.
     */
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
            noiseGenerator = new SimplexNoiseGenerator(context.level().world());
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