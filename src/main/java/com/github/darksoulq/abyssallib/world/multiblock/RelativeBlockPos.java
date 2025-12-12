package com.github.darksoulq.abyssallib.world.multiblock;

import java.util.Objects;

public record RelativeBlockPos(int x, int y, int z) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelativeBlockPos(int x1, int y1, int z1))) return false;
        return x == x1 && y == y1 && z == z1;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
