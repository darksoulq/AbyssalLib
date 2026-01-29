package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.feature.ConfiguredFeature;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public record PlacedFeature(ConfiguredFeature<?, ?> feature, List<PlacementModifier> placement) {

    public boolean place(WorldGenAccess level, Random random, int chunkX, int chunkZ) {
        Stream<Vector> positions = Stream.of(new Vector(chunkX * 16, 0, chunkZ * 16));
        PlacementContext context = new PlacementContext(level, chunkX, chunkZ, random);

        for (PlacementModifier modifier : placement) {
            positions = modifier.getPositions(context, positions);
        }

        boolean success = false;
        for (Vector pos : positions.toList()) {
            if (feature.place(level, context.toLocation(pos), random)) {
                success = true;
            }
        }
        return success;
    }
}