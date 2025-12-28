package com.github.darksoulq.abyssallib.world.multiblock;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultiblockEntity {
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();
    private final Multiblock multiblock;

    public MultiblockEntity(Multiblock multiblock) {
        this.multiblock = multiblock;
    }

    public Multiblock getMultiblock() {
        return multiblock;
    }

    public void serverTick() {}
    public void randomTick() {}
    public void onLoad() {}
    public void onSave() {}

    private List<Field> getCachedFields() {
        return FIELD_CACHE.computeIfAbsent(getClass(), clazz -> {
            List<Field> fields = new ArrayList<>();
            Class<?> c = clazz;
            while (c != MultiblockEntity.class && c != Object.class) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    if (Property.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        fields.add(field);
                    }
                }
                c = c.getSuperclass();
            }
            return fields;
        });
    }

    public <D> D serialize(DynamicOps<D> ops) throws Exception {
        Map<D, D> map = new LinkedHashMap<>();
        for (Field field : getCachedFields()) {
            Object obj = field.get(this);
            Property<?> prop = (Property<?>) obj;
            D encoded = prop.encode(ops);
            map.put(ops.createString(field.getName()), encoded);
        }
        return ops.createMap(map);
    }

    public <D> void deserialize(DynamicOps<D> ops, D input) throws Exception {
        Map<D, D> map = ops.getMap(input).orElse(Map.of());
        for (Field field : getCachedFields()) {
            Object obj = field.get(this);
            Property<?> prop = (Property<?>) obj;
            D encoded = map.get(ops.createString(field.getName()));
            if (encoded != null) {
                prop.decode(ops, encoded);
            }
        }
    }
}
