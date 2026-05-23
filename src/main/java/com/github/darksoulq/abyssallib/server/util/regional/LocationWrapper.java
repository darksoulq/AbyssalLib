package com.github.darksoulq.abyssallib.server.util.regional;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record LocationWrapper<T>(@NotNull Location location, @NotNull T element) implements WrappedLocatable<T> {
    @Override
    @NotNull
    public Location getLocation() {
        return location;
    }
}