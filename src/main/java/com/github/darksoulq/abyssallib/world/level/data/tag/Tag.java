package com.github.darksoulq.abyssallib.world.level.data.tag;

import java.util.Set;

public interface Tag<T> {
    String id();
    boolean contains(T entry);
    Set<T> values();
}
