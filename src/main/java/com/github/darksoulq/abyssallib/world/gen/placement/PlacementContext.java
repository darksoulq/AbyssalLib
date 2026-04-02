package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * A record containing context information required during the placement phase.
 *
 * @param level  The world generation access wrapper providing safe world modification.
 * @param chunkX The X coordinate of the chunk being processed.
 * @param chunkZ The Z coordinate of the chunk being processed.
 * @param random The seeded random source for deterministic placement.
 */
public record PlacementContext(WorldGenAccess level, int chunkX, int chunkZ, Random random) {

    /**
     * Retrieves the minimum build height of the current world context.
     *
     * @return The minimum build height.
     */
    public int getMinBuildHeight() {
        return level.getWorld().getMinHeight();
    }

    /**
     * Retrieves the maximum build height of the current world context.
     *
     * @return The maximum build height.
     */
    public int getHeight() {
        return level.getWorld().getMaxHeight();
    }

    /**
     * Converts a vector position into a Bukkit Location using the world context.
     *
     * @param pos The vector position.
     * @return A standard Bukkit Location.
     */
    public Location toLocation(Vector pos) {
        return new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
}