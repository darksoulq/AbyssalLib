package com.github.darksoulq.abyssallib.server.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TypedRegistry<T, Y> extends Registry<T> {

    private final Map<Y, List<T>> byType = new HashMap<>();
    private final Function<T, Y> typeExtractor;

    public TypedRegistry(Function<T, Y> typeExtractor) {
        this.typeExtractor = typeExtractor;
    }

    @Override
    public void register(String id, T object) {
        if (contains(id)) {
            super.register(id, object);
            return;
        }
        super.register(id, object);
        byType.computeIfAbsent(typeExtractor.apply(object), k -> new ArrayList<>()).add(object);
    }

    @Override
    public T remove(String id) {
        T removed = super.remove(id);
        if (removed != null) {
            Y type = typeExtractor.apply(removed);
            List<T> list = byType.get(type);
            if (list != null) {
                list.remove(removed);
                if (list.isEmpty()) {
                    byType.remove(type);
                }
            }
        }
        return removed;
    }

    @Override
    public void clear() {
        super.clear();
        byType.clear();
    }

    @SuppressWarnings("unchecked")
    public <R extends T> List<R> getByType(Y type) {
        List<T> list = byType.get(type);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList((List<R>) (List<?>) list);
    }
}