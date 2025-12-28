package com.github.darksoulq.abyssallib.world.multiblock.internal;

import org.bukkit.Location;

public record BlockKey(String world, int x, int y, int z) {
    public static BlockKey from(Location loc) {
        return new BlockKey(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}