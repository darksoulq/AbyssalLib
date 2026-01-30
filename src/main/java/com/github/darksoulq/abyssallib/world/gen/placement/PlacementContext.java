package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * A record containing context information required during the placement phase.
 *
 * @param level  The {@link WorldGenAccess} providing safe world modification.
 * @param chunkX The X coordinate of the chunk being processed.
 * @param chunkZ The Z coordinate of the chunk being processed.
 * @param random The seeded {@link Random} source for deterministic placement.
 */
public record PlacementContext(WorldGenAccess level, int chunkX, int chunkZ, Random random) {
    /**
     * @return The minimum build height of the current world.
     */
    public int getMinBuildHeight() {
        return level.getWorld().getMinHeight();
    }

    /**
     * @return The maximum build height (ceiling) of the current world.
     */
    public int getHeight() {
        return level.getWorld().getMaxHeight();
    }

    /**
     * Converts a vector position into a Bukkit {@link Location} using the world context.
     *
     * @param pos The vector position.
     * @return A Bukkit Location.
     */
    public Location toLocation(Vector pos) {
        return new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
}