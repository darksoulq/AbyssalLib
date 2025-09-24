package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public abstract class Tag<T> {
    public final Identifier id;
    protected final Set<String> values = new HashSet<>();
    protected final Set<Tag<T>> included = new HashSet<>();

    public Tag(Identifier id) {
        this.id = id;
    }

    public abstract void add(T value);
    public void add(String id) {
        values.add(id);
    }
    public void include(Tag<T> tag) {
        included.add(tag);
    }
    public abstract boolean contains(T value);
    public abstract Set<T> getAll();

    public Set<String> getValues() {
        return values;
    }
    public Set<Tag<T>> getIncluded() {
        return included;
    }
}
