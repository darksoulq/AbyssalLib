package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;

public interface WrappedLocatable<T> extends Locatable {
    @NotNull T element();
}