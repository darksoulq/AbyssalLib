package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionalDeque<E extends Locatable> implements Deque<E> {

    private final Map<RegionKey, Deque<E>> regions;
    private final boolean concurrent;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    public RegionalDeque() {
        this(false);
    }

    public RegionalDeque(boolean forceConcurrent) {
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private Deque<E> getOrCreateRegionDeque(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new ConcurrentLinkedDeque<>() : new LinkedList<>());
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
        Deque<E> deque = regions.get(key);
        return deque != null && deque.contains(o);
    }

    @Override
    public void addFirst(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        getOrCreateRegionDeque(key).addFirst(e);
        totalSize.incrementAndGet();
    }

    @Override
    public void addLast(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        getOrCreateRegionDeque(key).addLast(e);
        totalSize.incrementAndGet();
    }

    @Override
    public boolean offerFirst(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        if (getOrCreateRegionDeque(key).offerFirst(e)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean offerLast(@NotNull E e) {
        Objects.requireNonNull(e);
        RegionKey key = RegionKey.of(e);
        if (getOrCreateRegionDeque(key).offerLast(e)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public E removeFirst() {
        E e = pollFirst();
        if (e == null) throw new NoSuchElementException();
        return e;
    }

    @Override
    @NotNull
    public E removeLast() {
        E e = pollLast();
        if (e == null) throw new NoSuchElementException();
        return e;
    }

    @Override
    @Nullable
    public E pollFirst() {
        Iterator<Map.Entry<RegionKey, Deque<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Deque<E>> entry = it.next();
            Deque<E> deque = entry.getValue();
            E e = deque.pollFirst();
            if (e != null) {
                totalSize.decrementAndGet();
                if (deque.isEmpty()) it.remove();
                return e;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public E pollLast() {
        Iterator<Map.Entry<RegionKey, Deque<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Deque<E>> entry = it.next();
            Deque<E> deque = entry.getValue();
            E e = deque.pollLast();
            if (e != null) {
                totalSize.decrementAndGet();
                if (deque.isEmpty()) it.remove();
                return e;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public E getFirst() {
        E e = peekFirst();
        if (e == null) throw new NoSuchElementException();
        return e;
    }

    @Override
    @NotNull
    public E getLast() {
        E e = peekLast();
        if (e == null) throw new NoSuchElementException();
        return e;
    }

    @Override
    @Nullable
    public E peekFirst() {
        for (Deque<E> deque : regions.values()) {
            E e = deque.peekFirst();
            if (e != null) return e;
        }
        return null;
    }

    @Override
    @Nullable
    public E peekLast() {
        for (Deque<E> deque : regions.values()) {
            E e = deque.peekLast();
            if (e != null) return e;
        }
        return null;
    }

    @Override
    public boolean removeFirstOccurrence(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return false;
        RegionKey key = RegionKey.of(locatable);
        Deque<E> deque = regions.get(key);
        if (deque != null && deque.removeFirstOccurrence(o)) {
            totalSize.decrementAndGet();
            if (deque.isEmpty()) regions.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(@Nullable Object o) {
        if (!(o instanceof Locatable locatable)) return false;
        RegionKey key = RegionKey.of(locatable);
        Deque<E> deque = regions.get(key);
        if (deque != null && deque.removeLastOccurrence(o)) {
            totalSize.decrementAndGet();
            if (deque.isEmpty()) regions.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(@NotNull E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean offer(@NotNull E e) {
        return offerLast(e);
    }

    @Override
    @NotNull
    public E remove() {
        return removeFirst();
    }

    @Override
    @Nullable
    public E poll() {
        return pollFirst();
    }

    @Override
    @NotNull
    public E element() {
        return getFirst();
    }

    @Override
    @Nullable
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(@NotNull E e) {
        addFirst(e);
    }

    @Override
    @NotNull
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return removeFirstOccurrence(o);
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
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        int removedCount = 0;
        Iterator<Map.Entry<RegionKey, Deque<E>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Deque<E>> entry = it.next();
            Deque<E> deque = entry.getValue();
            int startSize = deque.size();
            if (deque.retainAll(c)) {
                modified = true;
                removedCount += (startSize - deque.size());
            }
            if (deque.isEmpty()) it.remove();
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
    @NotNull
    public Iterator<E> iterator() {
        return toCombinedList().iterator();
    }

    @Override
    @NotNull
    public Iterator<E> descendingIterator() {
        List<E> combined = toCombinedList();
        Collections.reverse(combined);
        return combined.iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return toCombinedList().toArray();
    }

    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        Objects.requireNonNull(a);
        return toCombinedList().toArray(a);
    }

    @NotNull
    public Deque<E> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        Deque<E> deque = regions.get(key);
        if (deque == null) return concurrent ? new ConcurrentLinkedDeque<>() : new LinkedList<>();
        return concurrent ? new ConcurrentLinkedDeque<>(deque) : new LinkedList<>(deque);
    }

    @NotNull
    private List<E> toCombinedList() {
        List<E> combined = new ArrayList<>(size());
        for (Deque<E> deque : regions.values()) {
            combined.addAll(deque);
        }
        return combined;
    }
}