package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPropertyEntity<T> {

    private final T type;

    public AbstractPropertyEntity(T type) {
        this.type = type;
    }

    public T getType() {
        return type;
    }

    public void serverTick() {}
    public void randomTick() {}
    public void onLoad() {}
    public void onSave() {}

    public <D> D serialize(DynamicOps<D> ops) throws Exception {
        Map<D, D> map = new LinkedHashMap<>();
        Class<?> cls = getClass();
        while (cls != AbstractPropertyEntity.class && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Object obj = field.get(this);

                if (obj instanceof Property<?> prop) {
                    D encoded = prop.encode(ops);
                    map.put(ops.createString(field.getName()), encoded);
                }
            }
            cls = cls.getSuperclass();
        }
        return ops.createMap(map);
    }

    public <D> void deserialize(DynamicOps<D> ops, D input) throws Exception {
        Map<D, D> map = ops.getMap(input).orElse(Map.of());
        Class<?> cls = getClass();
        while (cls != AbstractPropertyEntity.class && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Object obj = field.get(this);

                if (!(obj instanceof Property<?> prop)) continue;

                D encoded = map.get(ops.createString(field.getName()));
                if (encoded != null) {
                    prop.decode(ops, encoded);
                }
            }
            cls = cls.getSuperclass();
        }
    }
}