package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RegionalHashMap<K extends Locatable, V> implements Map<K, V> {

    private final Map<RegionKey, Map<K, V>> regions;
    private final boolean concurrent;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    public RegionalHashMap() {
        this(false);
    }

    public RegionalHashMap(boolean forceConcurrent) {
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private Map<K, V> getOrCreateRegionMap(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new ConcurrentHashMap<>() : new HashMap<>());
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
    public boolean containsKey(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, V> map = regions.get(rKey);
        return map != null && map.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        for (Map<K, V> map : regions.values()) {
            if (map.containsValue(value)) return true;
        }
        return false;
    }

    @Override
    @Nullable
    public V get(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return null;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, V> map = regions.get(rKey);
        return map != null ? map.get(key) : null;
    }

    @Override
    @Nullable
    public V put(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = getOrCreateRegionMap(rKey);
        int oldSize = map.size();
        V previous = map.put(key, value);
        if (map.size() > oldSize) {
            totalSize.incrementAndGet();
        }
        return previous;
    }

    @Override
    @Nullable
    public V remove(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return null;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, V> map = regions.get(rKey);
        if (map != null) {
            int oldSize = map.size();
            V removed = map.remove(key);
            if (map.size() < oldSize) {
                totalSize.decrementAndGet();
                if (map.isEmpty()) regions.remove(rKey);
            }
            return removed;
        }
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m);
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        regions.clear();
        totalSize.set(0);
    }

    @Override
    @NotNull
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(size());
        for (Map<K, V> map : regions.values()) {
            keys.addAll(map.keySet());
        }
        return keys;
    }

    @Override
    @NotNull
    public Collection<V> values() {
        List<V> vals = new ArrayList<>(size());
        for (Map<K, V> map : regions.values()) {
            vals.addAll(map.values());
        }
        return vals;
    }

    @Override
    @NotNull
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>(size());
        for (Map<K, V> map : regions.values()) {
            entries.addAll(map.entrySet());
        }
        return entries;
    }

    @Override
    @Nullable
    public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        V v = get(key);
        return (v != null || containsKey(key)) ? v : defaultValue;
    }

    @Override
    public void forEach(@NotNull BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map<K, V> map : regions.values()) {
            map.forEach(action);
        }
    }

    @Override
    public void replaceAll(@NotNull BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        for (Map<K, V> map : regions.values()) {
            map.replaceAll(function);
        }
    }

    @Override
    @Nullable
    public V putIfAbsent(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = getOrCreateRegionMap(rKey);
        int oldSize = map.size();
        V previous = map.putIfAbsent(key, value);
        if (map.size() > oldSize) totalSize.incrementAndGet();
        return previous;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, V> map = regions.get(rKey);
        if (map != null) {
            int oldSize = map.size();
            if (map.remove(key, value)) {
                totalSize.decrementAndGet();
                if (map.isEmpty()) regions.remove(rKey);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean replace(@NotNull K key, @NotNull V oldValue, @NotNull V newValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = regions.get(rKey);
        return map != null && map.replace(key, oldValue, newValue);
    }

    @Override
    @Nullable
    public V replace(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = regions.get(rKey);
        return map != null ? map.replace(key, value) : null;
    }

    @Override
    @Nullable
    public V computeIfAbsent(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = getOrCreateRegionMap(rKey);
        int oldSize = map.size();
        V result = map.computeIfAbsent(key, mappingFunction);
        if (map.size() > oldSize) totalSize.incrementAndGet();
        return result;
    }

    @Override
    @Nullable
    public V computeIfPresent(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = regions.get(rKey);
        if (map != null) {
            int oldSize = map.size();
            V result = map.computeIfPresent(key, remappingFunction);
            if (map.size() < oldSize) {
                totalSize.decrementAndGet();
                if (map.isEmpty()) regions.remove(rKey);
            }
            return result;
        }
        return null;
    }

    @Override
    @Nullable
    public V compute(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = getOrCreateRegionMap(rKey);
        int oldSize = map.size();
        V result = map.compute(key, remappingFunction);
        int newSize = map.size();
        if (newSize != oldSize) totalSize.addAndGet(newSize - oldSize);
        if (map.isEmpty()) regions.remove(rKey);
        return result;
    }

    @Override
    @Nullable
    public V merge(@NotNull K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(remappingFunction);
        RegionKey rKey = RegionKey.of(key);
        Map<K, V> map = getOrCreateRegionMap(rKey);
        int oldSize = map.size();
        V result = map.merge(key, value, remappingFunction);
        int newSize = map.size();
        if (newSize != oldSize) totalSize.addAndGet(newSize - oldSize);
        if (map.isEmpty()) regions.remove(rKey);
        return result;
    }

    @NotNull
    public Map<K, V> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        Map<K, V> map = regions.get(key);
        return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
    }
}