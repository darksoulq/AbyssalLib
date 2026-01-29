package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Objects;

public abstract class DataComponent<T> {
    protected final T value;

    public DataComponent(T value) {
        this.value = value;
    }

    public abstract DataComponentType<?> getType();

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DataComponent<?> that = (DataComponent<?>) obj;
        return Objects.equals(getType(), that.getType()) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), value);
    }

    @Override
    public String toString() {
        String id = Registries.DATA_COMPONENT_TYPES.getId(getType());
        return "DataComponent[" + (id != null ? id : getType().getClass().getSimpleName()) + "]{value=" + value + "}";
    }
}