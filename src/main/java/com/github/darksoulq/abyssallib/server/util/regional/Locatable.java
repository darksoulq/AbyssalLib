package com.github.darksoulq.abyssallib.server.util.regional;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Locatable {

    @Nullable
    default Location getLocation() {
        return null;
    }

    @Nullable
    default Chunk getChunk() {
        return null;
    }

    @NotNull
    static <T> WrappedLocatable<T> of(@NotNull Location location, @NotNull T element) {
        return new LocationWrapper<>(location, element);
    }

    @NotNull
    static <T> WrappedLocatable<T> of(@NotNull Chunk chunk, @NotNull T element) {
        return new ChunkWrapper<>(chunk, element);
    }
}