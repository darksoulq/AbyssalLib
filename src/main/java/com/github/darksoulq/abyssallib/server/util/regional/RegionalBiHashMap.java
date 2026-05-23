package com.github.darksoulq.abyssallib.server.util.regional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RegionalBiHashMap<K extends Locatable, V extends Locatable> implements Map<K, V> {

    private final RegionalHashMap<K, V> forward;
    private final RegionalHashMap<V, K> backward;
    private final Object mutex;
    private volatile RegionalBiHashMap<V, K> inverse;

    public RegionalBiHashMap() {
        this(false);
    }

    public RegionalBiHashMap(boolean forceConcurrent) {
        this.forward = new RegionalHashMap<>(forceConcurrent);
        this.backward = new RegionalHashMap<>(forceConcurrent);
        this.mutex = (RegionalCollections.IS_FOLIA || forceConcurrent) ? new Object() : null;
    }

    private RegionalBiHashMap(@NotNull RegionalHashMap<K, V> forward, @NotNull RegionalHashMap<V, K> backward, @Nullable Object mutex, @NotNull RegionalBiHashMap<V, K> inverse) {
        this.forward = forward;
        this.backward = backward;
        this.mutex = mutex;
        this.inverse = inverse;
    }

    @NotNull
    public RegionalBiHashMap<V, K> inverse() {
        if (inverse == null) {
            if (mutex != null) {
                synchronized (mutex) {
                    if (inverse == null) {
                        inverse = new RegionalBiHashMap<>(backward, forward, mutex, this);
                    }
                }
            } else {
                inverse = new RegionalBiHashMap<>(backward, forward, mutex, this);
            }
        }
        return inverse;
    }

    @Override
    public int size() {
        return forward.size();
    }

    @Override
    public boolean isEmpty() {
        return forward.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return forward.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return backward.containsKey(value);
    }

    @Override
    @Nullable
    public V get(@Nullable Object key) {
        return forward.get(key);
    }

    @Override
    @Nullable
    public V put(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (mutex != null) {
            synchronized (mutex) {
                return internalPut(key, value);
            }
        }
        return internalPut(key, value);
    }

    @Nullable
    private V internalPut(@NotNull K key, @NotNull V value) {
        V oldVal = forward.get(key);
        if (oldVal != null) backward.remove(oldVal);
        K oldKey = backward.get(value);
        if (oldKey != null) forward.remove(oldKey);
        forward.put(key, value);
        backward.put(value, key);
        return oldVal;
    }

    @Override
    @Nullable
    public V remove(@Nullable Object key) {
        if (!(key instanceof Locatable)) return null;
        if (mutex != null) {
            synchronized (mutex) {
                return internalRemove(key);
            }
        }
        return internalRemove(key);
    }

    @Nullable
    private V internalRemove(@NotNull Object key) {
        V val = forward.remove(key);
        if (val != null) backward.remove(val);
        return val;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m);
        if (mutex != null) {
            synchronized (mutex) {
                for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
                    internalPut(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
                internalPut(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void clear() {
        if (mutex != null) {
            synchronized (mutex) {
                forward.clear();
                backward.clear();
            }
        } else {
            forward.clear();
            backward.clear();
        }
    }

    @Override
    @NotNull
    public Set<K> keySet() {
        return forward.keySet();
    }

    @Override
    @NotNull
    public Set<V> values() {
        return backward.keySet();
    }

    @Override
    @NotNull
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> original = forward.entrySet();
        Set<Entry<K, V>> wrapped = new HashSet<>(original.size());
        for (Entry<K, V> entry : original) {
            wrapped.add(new AbstractMap.SimpleImmutableEntry<>(entry));
        }
        return Collections.unmodifiableSet(wrapped);
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
        if (mutex != null) {
            synchronized (mutex) {
                forward.forEach(action);
            }
        } else {
            forward.forEach(action);
        }
    }

    @Override
    public void replaceAll(@NotNull BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        if (mutex != null) {
            synchronized (mutex) {
                internalReplaceAll(function);
            }
        } else {
            internalReplaceAll(function);
        }
    }

    private void internalReplaceAll(@NotNull BiFunction<? super K, ? super V, ? extends V> function) {
        for (Entry<K, V> entry : forward.entrySet()) {
            K key = entry.getKey();
            V newValue = function.apply(key, entry.getValue());
            if (newValue != null) {
                internalPut(key, newValue);
            } else {
                internalRemove(key);
            }
        }
    }

    @Override
    @Nullable
    public V putIfAbsent(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (mutex != null) {
            synchronized (mutex) {
                return internalPutIfAbsent(key, value);
            }
        }
        return internalPutIfAbsent(key, value);
    }

    @Nullable
    private V internalPutIfAbsent(@NotNull K key, @NotNull V value) {
        V current = forward.get(key);
        if (current == null) {
            internalPut(key, value);
            return null;
        }
        return current;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (!(key instanceof Locatable) || !(value instanceof Locatable)) return false;
        if (mutex != null) {
            synchronized (mutex) {
                return internalRemoveSpecific(key, value);
            }
        }
        return internalRemoveSpecific(key, value);
    }

    private boolean internalRemoveSpecific(@NotNull Object key, @NotNull Object value) {
        if (forward.remove(key, value)) {
            backward.remove(value, key);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(@NotNull K key, @NotNull V oldValue, @NotNull V newValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        if (mutex != null) {
            synchronized (mutex) {
                return internalReplaceSpecific(key, oldValue, newValue);
            }
        }
        return internalReplaceSpecific(key, oldValue, newValue);
    }

    private boolean internalReplaceSpecific(@NotNull K key, @NotNull V oldValue, @NotNull V newValue) {
        if (forward.replace(key, oldValue, newValue)) {
            backward.remove(oldValue);
            backward.put(newValue, key);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public V replace(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (mutex != null) {
            synchronized (mutex) {
                return internalReplace(key, value);
            }
        }
        return internalReplace(key, value);
    }

    @Nullable
    private V internalReplace(@NotNull K key, @NotNull V value) {
        if (forward.containsKey(key)) {
            return internalPut(key, value);
        }
        return null;
    }

    @Override
    @Nullable
    public V computeIfAbsent(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);
        if (mutex != null) {
            synchronized (mutex) {
                return internalComputeIfAbsent(key, mappingFunction);
            }
        }
        return internalComputeIfAbsent(key, mappingFunction);
    }

    @Nullable
    private V internalComputeIfAbsent(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        V val = forward.get(key);
        if (val == null) {
            V newVal = mappingFunction.apply(key);
            if (newVal != null) {
                internalPut(key, newVal);
                return newVal;
            }
        }
        return val;
    }

    @Override
    @Nullable
    public V computeIfPresent(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        if (mutex != null) {
            synchronized (mutex) {
                return internalComputeIfPresent(key, remappingFunction);
            }
        }
        return internalComputeIfPresent(key, remappingFunction);
    }

    @Nullable
    private V internalComputeIfPresent(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V val = forward.get(key);
        if (val != null) {
            V newVal = remappingFunction.apply(key, val);
            if (newVal != null) {
                internalPut(key, newVal);
                return newVal;
            } else {
                internalRemove(key);
                return null;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public V compute(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        if (mutex != null) {
            synchronized (mutex) {
                return internalCompute(key, remappingFunction);
            }
        }
        return internalCompute(key, remappingFunction);
    }

    @Nullable
    private V internalCompute(@NotNull K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V val = forward.get(key);
        V newVal = remappingFunction.apply(key, val);
        if (newVal != null) {
            internalPut(key, newVal);
            return newVal;
        } else if (val != null) {
            internalRemove(key);
        }
        return null;
    }

    @Override
    @Nullable
    public V merge(@NotNull K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(remappingFunction);
        if (mutex != null) {
            synchronized (mutex) {
                return internalMerge(key, value, remappingFunction);
            }
        }
        return internalMerge(key, value, remappingFunction);
    }

    @Nullable
    private V internalMerge(@NotNull K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        V val = forward.get(key);
        V newVal = (val == null) ? value : remappingFunction.apply(val, value);
        if (newVal != null) {
            internalPut(key, newVal);
        } else {
            internalRemove(key);
        }
        return newVal;
    }
}