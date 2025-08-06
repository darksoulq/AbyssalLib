package com.github.darksoulq.abyssallib.world.level.multiblock;

import java.util.Objects;

public record RelativeBlockPos(int x, int y, int z) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelativeBlockPos other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
