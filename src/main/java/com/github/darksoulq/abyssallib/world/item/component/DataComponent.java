package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Objects;

/**
 * An abstract base class representing a single piece of metadata attached to an item or block.
 * <p>
 * DataComponents are used to store structured, type-safe information (such as names, custom markers,
 * or durability).
 *
 * @param <T> The type of the value stored within this component.
 */
public abstract class DataComponent<T> {
    /** * The internal value held by this component.
     * This may represent a primitive, a complex object, or an Adventure Component.
     */
    protected final T value;

    /**
     * Constructs a new DataComponent with the specified value.
     *
     * @param value The initial value to be stored in this component.
     */
    public DataComponent(T value) {
        this.value = value;
    }

    /**
     * Retrieves the specific type definition associated with this component instance.
     * <p>
     * This is used by the registry and serialization systems to identify how to
     * handle the component's data.
     *
     * @return The {@link DataComponentType} representing this component's category.
     */
    public abstract DataComponentType<?> getType();

    /**
     * Gets the current value stored in this component.
     *
     * @return The value of type {@code T}.
     */
    public T getValue() {
        return value;
    }

    /**
     * Compares this component against another object for equality.
     * <p>
     * Two components are considered equal if they share the exact same type
     * and their internal values are logically equivalent.
     *
     * @param obj The reference object with which to compare.
     * @return {@code true} if this object is the same as the {@code obj} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DataComponent<?> that = (DataComponent<?>) obj;
        return Objects.equals(getType(), that.getType()) && Objects.equals(value, that.value);
    }

    /**
     * Returns a hash code value for the component.
     *
     * @return A hash code value derived from the type and the internal value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getType(), value);
    }

    /**
     * Returns a string representation of the DataComponent.
     * <p>
     * This method attempts to resolve the component's unique identifier from
     * {@link Registries#DATA_COMPONENT_TYPES}. If not found, it falls back
     * to the simple name of the type class.
     *
     * @return A formatted string containing the component ID and its value.
     */
    @Override
    public String toString() {
        String id = Registries.DATA_COMPONENT_TYPES.getId(getType());
        return "DataComponent[" + (id != null ? id : getType().getClass().getSimpleName()) + "]{value=" + value + "}";
    }
}