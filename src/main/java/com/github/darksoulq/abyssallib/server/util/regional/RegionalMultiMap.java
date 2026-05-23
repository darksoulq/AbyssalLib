package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionalMultiMap<K extends Locatable, V> {

    private final Map<RegionKey, Map<K, Collection<V>>> regions;
    private final boolean concurrent;
    private final boolean useSet;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    public RegionalMultiMap() {
        this(false, false);
    }

    public RegionalMultiMap(boolean forceConcurrent) {
        this(forceConcurrent, false);
    }

    public RegionalMultiMap(boolean forceConcurrent, boolean useSet) {
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.useSet = useSet;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private Map<K, Collection<V>> getOrCreateRegionMap(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new ConcurrentHashMap<>() : new HashMap<>());
    }

    @NotNull
    private Collection<V> createCollection() {
        if (concurrent) {
            return useSet ? ConcurrentHashMap.newKeySet() : new CopyOnWriteArrayList<>();
        } else {
            return useSet ? new HashSet<>() : new ArrayList<>();
        }
    }

    public int size() {
        return totalSize.get();
    }

    public boolean isEmpty() {
        return totalSize.get() == 0;
    }

    public boolean containsKey(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, Collection<V>> map = regions.get(rKey);
        return map != null && map.containsKey(key);
    }

    public boolean containsValue(@Nullable Object value) {
        for (Map<K, Collection<V>> map : regions.values()) {
            for (Collection<V> collection : map.values()) {
                if (collection.contains(value)) return true;
            }
        }
        return false;
    }

    public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, Collection<V>> map = regions.get(rKey);
        if (map != null) {
            Collection<V> collection = map.get(key);
            return collection != null && collection.contains(value);
        }
        return false;
    }

    public boolean put(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        RegionKey rKey = RegionKey.of(key);
        Map<K, Collection<V>> map = getOrCreateRegionMap(rKey);
        Collection<V> collection = map.computeIfAbsent(key, k -> createCollection());
        if (collection.add(value)) {
            totalSize.incrementAndGet();
            return true;
        }
        return false;
    }

    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, Collection<V>> map = regions.get(rKey);
        if (map != null) {
            Collection<V> collection = map.get(key);
            if (collection != null && collection.remove(value)) {
                totalSize.decrementAndGet();
                if (collection.isEmpty()) {
                    map.remove(key);
                    if (map.isEmpty()) {
                        regions.remove(rKey);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @NotNull
    public Collection<V> removeAll(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return emptyCollection();
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, Collection<V>> map = regions.get(rKey);
        if (map != null) {
            Collection<V> collection = map.remove(key);
            if (collection != null) {
                int removedCount = collection.size();
                totalSize.addAndGet(-removedCount);
                if (map.isEmpty()) {
                    regions.remove(rKey);
                }
                return unmodifiableCollection(collection);
            }
        }
        return emptyCollection();
    }

    @NotNull
    public Collection<V> get(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return emptyCollection();
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, Collection<V>> map = regions.get(rKey);
        if (map != null) {
            Collection<V> collection = map.get(key);
            if (collection != null) {
                return unmodifiableCollection(collection);
            }
        }
        return emptyCollection();
    }

    public void clear() {
        regions.clear();
        totalSize.set(0);
    }

    @NotNull
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(size());
        for (Map<K, Collection<V>> map : regions.values()) {
            keys.addAll(map.keySet());
        }
        return keys;
    }

    @NotNull
    public Collection<V> values() {
        Collection<V> allValues = new ArrayList<>(size());
        for (Map<K, Collection<V>> map : regions.values()) {
            for (Collection<V> collection : map.values()) {
                allValues.addAll(collection);
            }
        }
        return allValues;
    }

    @NotNull
    public Collection<Map.Entry<K, V>> entries() {
        Collection<Map.Entry<K, V>> entries = new ArrayList<>(size());
        for (Map<K, Collection<V>> map : regions.values()) {
            for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
                K key = entry.getKey();
                for (V value : entry.getValue()) {
                    entries.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
                }
            }
        }
        return entries;
    }

    @NotNull
    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> combinedMap = new HashMap<>();
        for (Map<K, Collection<V>> map : regions.values()) {
            for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
                combinedMap.put(entry.getKey(), unmodifiableCollection(entry.getValue()));
            }
        }
        return combinedMap;
    }

    @NotNull
    public Map<K, Collection<V>> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        Map<K, Collection<V>> map = regions.get(key);
        if (map == null) return Collections.emptyMap();
        
        Map<K, Collection<V>> result = new HashMap<>();
        for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
            result.put(entry.getKey(), unmodifiableCollection(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    @NotNull
    private Collection<V> unmodifiableCollection(@NotNull Collection<V> collection) {
        return useSet ? Collections.unmodifiableSet((Set<V>) collection) : Collections.unmodifiableList((List<V>) collection);
    }

    @NotNull
    private Collection<V> emptyCollection() {
        return useSet ? Collections.emptySet() : Collections.emptyList();
    }
}