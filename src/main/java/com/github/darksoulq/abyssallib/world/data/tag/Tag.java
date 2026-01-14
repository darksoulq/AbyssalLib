package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public abstract class Tag<T, D> {
    public final Identifier id;
    protected final Set<T> values = new HashSet<>();
    protected final Set<Tag<T, D>> included = new HashSet<>();

    public Tag(Identifier id) {
        this.id = id;
    }

    public void add(T value) {
        this.values.add(value);
    }
    public void include(Tag<T, D> tag) {
        this.included.add(tag);
    }

    public abstract boolean contains(D input);
    public abstract Set<T> getAll();

    public Set<T> getValues() {
        return values;
    }
    public Set<Tag<T, D>> getIncluded() {
        return included;
    }
}