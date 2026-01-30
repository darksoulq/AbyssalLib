package com.github.darksoulq.abyssallib.world.multiblock;

import java.util.Objects;

/**
 * Represents a position in 3D space relative to a multiblock origin.
 * <p>
 * This record is primarily used to define patterns and layouts within a
 * multiblock structure, allowing for coordinate transformations such as
 * rotation and mirroring.
 * @param x The relative X coordinate.
 * @param y The relative Y coordinate.
 * @param z The relative Z coordinate.
 */
public record RelativeBlockPos(int x, int y, int z) {

    /**
     * Compares this relative position to another object for equality.
     *
     * @param o The object to compare with.
     * @return {@code true} if the coordinates match; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelativeBlockPos(int x1, int y1, int z1))) return false;
        return x == x1 && y == y1 && z == z1;
    }

    /**
     * Generates a hash code based on the X, Y, and Z coordinates.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}