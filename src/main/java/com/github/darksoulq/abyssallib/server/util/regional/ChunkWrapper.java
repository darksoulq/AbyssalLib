package com.github.darksoulq.abyssallib.server.util.regional;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record ChunkWrapper<T>(@NotNull Chunk chunk, @NotNull T element) implements WrappedLocatable<T> {
    @Override
    @NotNull
    public Chunk getChunk() {
        return chunk;
    }
}