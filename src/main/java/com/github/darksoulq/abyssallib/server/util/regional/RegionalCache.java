package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RegionalCache<K extends Locatable, V> implements Map<K, V> {

    private static final class CacheNode<V> {
        final V value;
        final long expiryTime;

        CacheNode(@NotNull V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired(long now) {
            return now >= expiryTime;
        }
    }

    private final Map<RegionKey, Map<K, CacheNode<V>>> regions;
    private final boolean concurrent;
    private final AtomicInteger totalSize = new AtomicInteger(0);
    private final long defaultDurationMillis;

    public RegionalCache(long defaultDuration, @NotNull TimeUnit unit) {
        this(defaultDuration, unit, false);
    }

    public RegionalCache(long defaultDuration, @NotNull TimeUnit unit, boolean forceConcurrent) {
        Objects.requireNonNull(unit);
        this.defaultDurationMillis = unit.toMillis(defaultDuration);
        this.concurrent = RegionalCollections.IS_FOLIA || forceConcurrent;
        this.regions = this.concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @NotNull
    private Map<K, CacheNode<V>> getOrCreateRegionMap(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        return regions.computeIfAbsent(key, k -> concurrent ? new ConcurrentHashMap<>() : new HashMap<>());
    }

    private void pruneRegion(@NotNull RegionKey rKey, @NotNull Map<K, CacheNode<V>> map, long now) {
        int removedCount = 0;
        Iterator<Map.Entry<K, CacheNode<V>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isExpired(now)) {
                it.remove();
                removedCount++;
            }
        }
        if (removedCount > 0) {
            totalSize.addAndGet(-removedCount);
            if (map.isEmpty()) {
                regions.remove(rKey);
            }
        }
    }

    private void pruneAll(long now) {
        Iterator<Map.Entry<RegionKey, Map<K, CacheNode<V>>>> it = regions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RegionKey, Map<K, CacheNode<V>>> entry = it.next();
            pruneRegion(entry.getKey(), entry.getValue(), now);
        }
    }

    @Override
    public int size() {
        pruneAll(System.currentTimeMillis());
        return totalSize.get();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map == null) return false;
        
        long now = System.currentTimeMillis();
        CacheNode<V> node = map.get(key);
        if (node == null) return false;
        
        if (node.isExpired(now)) {
            map.remove(key);
            totalSize.decrementAndGet();
            if (map.isEmpty()) regions.remove(rKey);
            return false;
        }
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        long now = System.currentTimeMillis();
        pruneAll(now);
        for (Map<K, CacheNode<V>> map : regions.values()) {
            for (CacheNode<V> node : map.values()) {
                if (Objects.equals(node.value, value)) return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public V get(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return null;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map == null) return null;

        long now = System.currentTimeMillis();
        CacheNode<V> node = map.get(key);
        if (node == null) return null;

        if (node.isExpired(now)) {
            map.remove(key);
            totalSize.decrementAndGet();
            if (map.isEmpty()) regions.remove(rKey);
            return null;
        }
        return node.value;
    }

    @Override
    @Nullable
    public V put(@NotNull K key, @NotNull V value) {
        return put(key, value, defaultDurationMillis, TimeUnit.MILLISECONDS);
    }

    @Nullable
    public V put(@NotNull K key, @NotNull V value, long duration, @NotNull TimeUnit unit) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(unit);
        
        long now = System.currentTimeMillis();
        long expiry = now + unit.toMillis(duration);
        CacheNode<V> newNode = new CacheNode<>(value, expiry);
        
        RegionKey rKey = RegionKey.of(key);
        Map<K, CacheNode<V>> map = getOrCreateRegionMap(rKey);
        CacheNode<V> oldNode = map.put(key, newNode);
        
        if (oldNode == null) {
            totalSize.incrementAndGet();
            return null;
        } else if (oldNode.isExpired(now)) {
            return null;
        }
        return oldNode.value;
    }

    @Override
    @Nullable
    public V remove(@Nullable Object key) {
        if (!(key instanceof Locatable locatable)) return null;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map != null) {
            long now = System.currentTimeMillis();
            CacheNode<V> removed = map.remove(key);
            if (removed != null) {
                totalSize.decrementAndGet();
                if (map.isEmpty()) regions.remove(rKey);
                return removed.isExpired(now) ? null : removed.value;
            }
        }
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m);
        long now = System.currentTimeMillis();
        long expiry = now + defaultDurationMillis;
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            
            CacheNode<V> newNode = new CacheNode<>(value, expiry);
            RegionKey rKey = RegionKey.of(key);
            Map<K, CacheNode<V>> map = getOrCreateRegionMap(rKey);
            
            if (map.put(key, newNode) == null) {
                totalSize.incrementAndGet();
            }
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
        pruneAll(System.currentTimeMillis());
        Set<K> keys = new HashSet<>(size());
        for (Map<K, CacheNode<V>> map : regions.values()) {
            keys.addAll(map.keySet());
        }
        return keys;
    }

    @Override
    @NotNull
    public Collection<V> values() {
        pruneAll(System.currentTimeMillis());
        List<V> vals = new ArrayList<>(size());
        for (Map<K, CacheNode<V>> map : regions.values()) {
            for (CacheNode<V> node : map.values()) {
                vals.add(node.value);
            }
        }
        return vals;
    }

    @Override
    @NotNull
    public Set<Entry<K, V>> entrySet() {
        pruneAll(System.currentTimeMillis());
        Set<Entry<K, V>> entries = new HashSet<>(size());
        for (Map<K, CacheNode<V>> map : regions.values()) {
            for (Entry<K, CacheNode<V>> entry : map.entrySet()) {
                entries.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue().value));
            }
        }
        return entries;
    }

    @Override
    @Nullable
    public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        V v = get(key);
        return v != null ? v : defaultValue;
    }

    @Override
    public void forEach(@NotNull BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        long now = System.currentTimeMillis();
        pruneAll(now);
        for (Map<K, CacheNode<V>> map : regions.values()) {
            for (Entry<K, CacheNode<V>> entry : map.entrySet()) {
                action.accept(entry.getKey(), entry.getValue().value);
            }
        }
    }

    @Override
    public void replaceAll(@NotNull BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        long now = System.currentTimeMillis();
        long expiry = now + defaultDurationMillis;
        pruneAll(now);
        
        for (Map<K, CacheNode<V>> map : regions.values()) {
            for (Entry<K, CacheNode<V>> entry : map.entrySet()) {
                K key = entry.getKey();
                V newValue = function.apply(key, entry.getValue().value);
                Objects.requireNonNull(newValue);
                map.put(key, new CacheNode<>(newValue, expiry));
            }
        }
    }

    @Override
    @Nullable
    public V putIfAbsent(@NotNull K key, @NotNull V value) {
        V existing = get(key);
        if (existing == null) {
            put(key, value);
            return null;
        }
        return existing;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (!(key instanceof Locatable locatable)) return false;
        RegionKey rKey = RegionKey.of(locatable);
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map != null) {
            long now = System.currentTimeMillis();
            CacheNode<V> node = map.get(key);
            if (node != null && !node.isExpired(now) && Objects.equals(node.value, value)) {
                map.remove(key);
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
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map != null) {
            long now = System.currentTimeMillis();
            CacheNode<V> node = map.get(key);
            if (node != null && !node.isExpired(now) && Objects.equals(node.value, oldValue)) {
                map.put(key, new CacheNode<>(newValue, now + defaultDurationMillis));
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public V replace(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        
        RegionKey rKey = RegionKey.of(key);
        Map<K, CacheNode<V>> map = regions.get(rKey);
        if (map != null) {
            long now = System.currentTimeMillis();
            CacheNode<V> node = map.get(key);
            if (node != null && !node.isExpired(now)) {
                map.put(key, new CacheNode<>(value, now + defaultDurationMillis));
                return node.value;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public V computeIfAbsent(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);
        V existing = get(key);
        if (existing == null) {
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            }
        }
        return existing;
    }

    @NotNull
    public Map<K, V> getRegion(@NotNull RegionKey key) {
        Objects.requireNonNull(key);
        Map<K, CacheNode<V>> map = regions.get(key);
        if (map == null) return Collections.emptyMap();
        
        long now = System.currentTimeMillis();
        pruneRegion(key, map, now);
        
        Map<K, V> result = new HashMap<>();
        for (Entry<K, CacheNode<V>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().value);
        }
        return Collections.unmodifiableMap(result);
    }
}