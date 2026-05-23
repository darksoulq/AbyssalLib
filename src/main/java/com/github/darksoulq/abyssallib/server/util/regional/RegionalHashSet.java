package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class RegionalHashSet<E extends Locatable> implements Set<E> {

    private final RegionalHashMap<E, Object> map;
    private static final Object PRESENT = new Object();

    public RegionalHashSet() {
        this(false);
    }

    public RegionalHashSet(boolean forceConcurrent) {
        this.map = new RegionalHashMap<>(forceConcurrent);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return map.containsKey(o);
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        Objects.requireNonNull(a);
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(@NotNull E e) {
        Objects.requireNonNull(e);
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (E e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (Object o : c) {
            if (remove(o)) modified = true;
        }
        return modified;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    public Set<E> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return map.getRegion(key).keySet();
    }
}