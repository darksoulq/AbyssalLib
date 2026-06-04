package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.HeightMap;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on their vertical proximity
 * to a selected world heightmap (surface).
 * <p>
 * If the incoming vector's Y-coordinate does not fall within the configured
 * relative bounds, the vector is discarded.
 */
public class SurfaceRelativeThresholdModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the surface relative threshold modifier.
     */
    public static final Codec<SurfaceRelativeThresholdModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(HeightMap.class).fieldOf("heightmap").forGetter(SurfaceRelativeThresholdModifier.class, p -> p.heightmap),
        Codecs.INT.fieldOf("min_inclusive").forGetter(SurfaceRelativeThresholdModifier.class, p -> p.minInclusive),
        Codecs.INT.fieldOf("max_inclusive").forGetter(SurfaceRelativeThresholdModifier.class, p -> p.maxInclusive)
    ).apply(instance, SurfaceRelativeThresholdModifier::new)).describe("SurfaceRelativeThresholdModifier");

    /**
     * The registered type definition for the surface relative threshold placement modifier.
     */
    public static final PlacementModifierType<SurfaceRelativeThresholdModifier> TYPE = () -> CODEC;

    /** The heightmap criteria used to locate the surface block. */
    private final HeightMap heightmap;

    /** The minimum allowed relative offset from the surface (inclusive). */
    private final int minInclusive;

    /** The maximum allowed relative offset from the surface (inclusive). */
    private final int maxInclusive;

    /**
     * Constructs a new SurfaceRelativeThresholdModifier.
     *
     * @param heightmap    The target surface heightmap (e.g., OCEAN_FLOOR, WORLD_SURFACE).
     * @param minInclusive The lowest relative offset allowed.
     * @param maxInclusive The highest relative offset allowed.
     */
    public SurfaceRelativeThresholdModifier(HeightMap heightmap, int minInclusive, int maxInclusive) {
        this.heightmap = heightmap;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    /**
     * Filters the incoming positions by checking if their absolute Y-coordinate falls within
     * the allowed bounds relative to the surface Y-coordinate at their X/Z location.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream of vectors that met the surface proximity condition.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            int surfaceY = context.level().getHighestBlockY(pos.getBlockX(), pos.getBlockZ(), heightmap);
            int minAllowedY = surfaceY + minInclusive;
            int maxAllowedY = surfaceY + maxInclusive;

            return pos.getBlockY() >= minAllowedY && pos.getBlockY() <= maxAllowedY;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this surface relative threshold modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}