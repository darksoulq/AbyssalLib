package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

public record PlacementContext(WorldGenAccess level, int chunkX, int chunkZ, Random random) {
    public int getMinBuildHeight() {
        return level.getWorld().getMinHeight();
    }

    public int getHeight() {
        return level.getWorld().getMaxHeight();
    }
    
    public Location toLocation(Vector pos) {
        return new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
}