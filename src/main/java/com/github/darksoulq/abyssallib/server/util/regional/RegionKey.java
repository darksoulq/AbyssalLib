package com.github.darksoulq.abyssallib.server.util.regional;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record RegionKey(@NotNull UUID world, int chunkX, int chunkZ) {
    
    @NotNull
    public static RegionKey of(@NotNull Locatable locatable) {
        Objects.requireNonNull(locatable);
        
        Location loc = locatable.getLocation();
        if (loc != null) {
            return new RegionKey(loc.getWorld().getUID(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        }
        
        Chunk chunk = locatable.getChunk();
        if (chunk != null) {
            return new RegionKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        }
        
        throw new IllegalArgumentException();
    }
}