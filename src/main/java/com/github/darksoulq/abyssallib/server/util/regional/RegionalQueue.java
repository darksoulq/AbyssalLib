package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionalQueue<E extends Locatable> implements Queue<E> {

    private final Map<RegionKey, Queue<E>> regions;
    private final boolean concurrent;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    public RegionalQueue() {
        this(false);
    }

    public RegionalQueue(boolean forceConcurrent) {
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private Queue<E> getOrCreateRegionQueue(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new ConcurrentLinkedQueue<>() : new LinkedList<>());
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
        Queue<E> queue = regions.get(key);
        return queue != null && queue.contains(o);
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        List<E> combined = new ArrayList<>(size());
        for (Queue<E> queue : regions.values()) {
            combined.addAll(queue);
        }
        return combined.iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        Object @NotNull [] result = new Object[size()];
        int idx = 0;
        for (Queue<E> queue : regions.values()) {
            for (E element : queue) {
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
        for (Queue<E> queue : regions.values()) {
            for (E element : queue) {
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
        if (getOrCreateRegionQueue(key).add(e)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return false;
        RegionKey key = RegionKey.of(locatable);
        Queue<E> queue = regions.get(key);
        if (queue != null && queue.remove(o)) {
            totalSize.decrementAndGet();
            if (queue.isEmpty()) regions.remove(key);
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
        Iterator<Map.Entry<RegionKey, Queue<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Queue<E>> entry = it.next();
            Queue<E> queue = entry.getValue();
            int startSize = queue.size();
            if (queue.retainAll(c)) {
                modified = true;
                removedCount += (startSize - queue.size());
            }
            if (queue.isEmpty()) it.remove();
        }
        totalSize.addAndGet(-removedCount);
        return modified;
    }

    @Override
    public void clear() {
        regions.clear();
        totalSize.set(0);
    }

    @Override
    public boolean offer(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        if (getOrCreateRegionQueue(key).offer(e)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public E remove() {
        E element = poll();
        if (element == null) throw new NoSuchElementException();
        return element;
    }

    @Override
    @Nullable
    public E poll() {
        Iterator<Map.Entry<RegionKey, Queue<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Queue<E>> entry = it.next();
            Queue<E> queue = entry.getValue();
            E element = queue.poll();
            if (element != null) {
                totalSize.decrementAndGet();
                if (queue.isEmpty()) it.remove();
                return element;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public E element() {
        E element = peek();
        if (element == null) throw new NoSuchElementException();
        return element;
    }

    @Override
    @Nullable
    public E peek() {
        for (Queue<E> queue : regions.values()) {
            E element = queue.peek();
            if (element != null) return element;
        }
        return null;
    }

    @NotNull
    public Queue<E> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        Queue<E> queue = regions.get(key);
        if (queue == null) return concurrent ? new ConcurrentLinkedQueue<>() : new LinkedList<>();
        return concurrent ? new ConcurrentLinkedQueue<>(queue) : new LinkedList<>(queue);
    }
}