package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.config.DataPath;

import java.util.Optional;
import java.util.function.Function;

/**
 * A wrapper around a serialized value and its associated {@link DynamicOps}
 * implementation.
 * <p>
 * This class provides a fluent API for querying, modifying, and converting
 * dynamic data structures without repeatedly passing the operations instance.
 *
 * @param <T> the serialized value type
 */
public record Dynamic<T>(DynamicOps<T> ops, T value) {

    /**
     * Creates a new dynamic wrapper.
     *
     * @param ops   the operations implementation associated with the value
     * @param value the wrapped serialized value
     */
    public Dynamic {
    }

    /**
     * Returns the operations implementation associated with this value.
     *
     * @return the operations implementation
     */
    @Override
    public DynamicOps<T> ops() {
        return ops;
    }

    /**
     * Returns the wrapped serialized value.
     *
     * @return the wrapped value
     */
    @Override
    public T value() {
        return value;
    }

    /**
     * Retrieves a nested value using a path expression.
     *
     * @param path the path expression to resolve
     * @return the nested value wrapped as a {@code Dynamic}, if present
     */
    public Optional<Dynamic<T>> get(String path) {
        return ops.query(value, path).map(t -> new Dynamic<>(ops, t));
    }

    /**
     * Retrieves a nested value using a compiled path.
     *
     * @param path the compiled path to resolve
     * @return the nested value wrapped as a {@code Dynamic}, if present
     */
    public Optional<Dynamic<T>> get(DataPath path) {
        return ops.query(value, path).map(t -> new Dynamic<>(ops, t));
    }

    /**
     * Sets a value at the specified path.
     *
     * @param path  the destination path
     * @param value the value to store
     * @return a new dynamic instance containing the updated structure
     */
    public Dynamic<T> set(String path, T value) {
        return new Dynamic<>(ops, ops.set(this.value, path, value));
    }

    /**
     * Sets a value at the specified path using another dynamic wrapper.
     *
     * @param path    the destination path
     * @param dynamic the dynamic containing the value to store
     * @return a new dynamic instance containing the updated structure
     */
    public Dynamic<T> set(String path, Dynamic<T> dynamic) {
        return set(path, dynamic.value());
    }

    /**
     * Applies a transformation to the value located at the specified path.
     *
     * @param path   the target path
     * @param editor the function used to transform the value
     * @return a new dynamic instance containing the updated structure
     */
    public Dynamic<T> edit(String path, Function<T, T> editor) {
        return new Dynamic<>(ops, ops.edit(value, path, editor));
    }

    /**
     * Removes the value located at the specified path.
     *
     * @param path the path of the value to remove
     * @return a new dynamic instance containing the updated structure
     */
    public Dynamic<T> remove(String path) {
        return new Dynamic<>(ops, ops.remove(value, path));
    }

    /**
     * Converts the wrapped value to a different serialized format.
     *
     * @param outOps the target operations implementation
     * @param <R>    the target serialized value type
     * @return a dynamic wrapper containing the converted value
     */
    public <R> Dynamic<R> convert(DynamicOps<R> outOps) {
        return new Dynamic<>(outOps, ops.convertTo(outOps, value));
    }
}