package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.feature.ConfiguredFeature;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Represents a feature that is ready to be placed in the world.
 * <p>
 * A PlacedFeature combines a {@link ConfiguredFeature} (the object to be placed)
 * with a list of {@link PlacementModifier}s (the instructions on where to place it).
 * </p>
 *
 * @param feature   The configured feature to generate.
 * @param placement The ordered list of modifiers to apply to the initial chunk position.
 */
public record PlacedFeature(ConfiguredFeature<?, ?> feature, List<PlacementModifier> placement) {

    /**
     * Triggers the placement pipeline for a specific chunk.
     * <p>
     * 1. Starts with a single vector at the chunk's origin (X, 0, Z).<br>
     * 2. Sequentially passes the stream through every {@link PlacementModifier}.<br>
     * 3. Executes {@link ConfiguredFeature#place} for every resulting position.
     * </p>
     *
     * @param level  The world generation accessor.
     * @param random The random source for this chunk.
     * @param chunkX The chunk X coordinate.
     * @param chunkZ The chunk Z coordinate.
     * @return {@code true} if the feature was successfully placed at least once.
     */
    public boolean place(WorldGenAccess level, Random random, int chunkX, int chunkZ) {
        // Start with a single point at the corner of the chunk
        Stream<Vector> positions = Stream.of(new Vector(chunkX * 16, 0, chunkZ * 16));
        PlacementContext context = new PlacementContext(level, chunkX, chunkZ, random);

        // Apply all modifiers to the position stream
        for (PlacementModifier modifier : placement) {
            positions = modifier.getPositions(context, positions);
        }

        // Final placement loop
        boolean success = false;
        for (Vector pos : positions.toList()) {
            if (feature.place(level, context.toLocation(pos), random)) {
                success = true;
            }
        }
        return success;
    }
}