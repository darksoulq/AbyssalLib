package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

public class RegionalArrayList<E extends Locatable> implements List<E> {

    private final Map<RegionKey, List<E>> regions;
    private final boolean concurrent;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    public RegionalArrayList() {
        this(false);
    }

    public RegionalArrayList(boolean forceConcurrent) {
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private List<E> getOrCreateRegionList(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new CopyOnWriteArrayList<>() : new ArrayList<>());
    }

    @Override
    public int size() {
        return totalSize.get();
    }

    @Override
    public boolean isEmpty() {
        return totalSize.get() == 0;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return false;
        RegionKey key = RegionKey.of(locatable);
        List<E> list = regions.get(key);
        return list != null && list.contains(o);
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return toCombinedList().iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        Object @NotNull [] result = new Object[size()];
        int idx = 0;
        for (List<E> list : regions.values()) {
            for (E element : list) {
                if (idx < result.length) result[idx++] = element;
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        Objects.requireNonNull(a);
        int size = size();
        T @NotNull [] r = a.length >= size ? a : (T @NotNull []) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int idx = 0;
        for (List<E> list : regions.values()) {
            for (E element : list) {
                if (idx < r.length) r[idx++] = (T) element;
            }
        }
        if (r.length > size) r[size] = null;
        return r;
    }

    @Override
    public boolean add(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        if (getOrCreateRegionList(key).add(e)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return false;
        RegionKey key = RegionKey.of(locatable);
        List<E> list = regions.get(key);
        if (list != null && list.remove(o)) {
            totalSize.decrementAndGet();
            if (list.isEmpty()) regions.remove(key);
            return true;
        }
        return false;
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
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
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
    public boolean retainAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        int removedCount = 0;
        Iterator<Map.Entry<RegionKey, List<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, List<E>> entry = it.next();
            List<E> list = entry.getValue();
            int startSize = list.size();
            if (list.retainAll(c)) {
                modified = true;
                removedCount += (startSize - list.size());
            }
            if (list.isEmpty()) it.remove();
        }
        totalSize.addAndGet(-removedCount);
        return modified;
    }

    @Override
    public void replaceAll(@NotNull UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        for (List<E> list : regions.values()) {
            list.replaceAll(operator);
        }
    }

    @Override
    public void sort(@Nullable Comparator<? super E> c) {
        for (List<E> list : regions.values()) {
            list.sort(c);
        }
    }

    @Override
    public void clear() {
        regions.clear();
        totalSize.set(0);
    }

    @Override
    @NotNull
    public E get(int index) {
        if (index < 0 || index >= totalSize.get()) throw new IndexOutOfBoundsException();
        int current = 0;
        for (List<E> list : regions.values()) {
            if (index < current + list.size()) {
                return list.get(index - current);
            }
            current += list.size();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    @NotNull
    public E set(int index, @NotNull E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, @NotNull E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public E remove(int index) {
        if (index < 0 || index >= totalSize.get()) throw new IndexOutOfBoundsException();
        int current = 0;
        Iterator<Map.Entry<RegionKey, List<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, List<E>> entry = it.next();
            List<E> list = entry.getValue();
            if (index < current + list.size()) {
                E removed = list.remove(index - current);
                totalSize.decrementAndGet();
                if (list.isEmpty()) it.remove();
                return removed;
            }
            current += list.size();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int indexOf(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return -1;
        int current = 0;
        for (List<E> list : regions.values()) {
            int idx = list.indexOf(o);
            if (idx != -1) return current + idx;
            current += list.size();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return indexOf(o);
    }

    @Override
    @NotNull
    public ListIterator<E> listIterator() {
        return toCombinedList().listIterator();
    }

    @Override
    @NotNull
    public ListIterator<E> listIterator(int index) {
        return toCombinedList().listIterator(index);
    }

    @Override
    @NotNull
    public List<E> subList(int fromIndex, int toIndex) {
        return toCombinedList().subList(fromIndex, toIndex);
    }

    @NotNull
    public List<E> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        List<E> list = regions.get(key);
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    @NotNull
    private List<E> toCombinedList() {
        List<E> combined = new ArrayList<>(size());
        for (List<E> list : regions.values()) {
            combined.addAll(list);
        }
        return combined;
    }
}