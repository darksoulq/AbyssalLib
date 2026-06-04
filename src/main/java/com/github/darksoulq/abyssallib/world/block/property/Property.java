package com.github.darksoulq.abyssallib.world.block.property;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

/**
 * A generic container for a block or entity property that supports automatic serialization.
 * <p>
 * Each property is associated with a {@link Codec} to handle the conversion
 * between the object-oriented value and a serialized format.
 *
 * @param <T> the type of the value held by this property
 */
public class Property<T> {

    /**
     * The codec used for encoding and decoding this property's value.
     */
    private final Codec<T> codec;

    /**
     * The current value of this property.
     */
    private T value;

    /**
     * The default value assigned during instantiation, used for resets or fallback.
     */
    private final T defaultValue;

    /**
     * Constructs a new Property with a codec and an initial value.
     *
     * @param codec        the codec for serialization
     * @param initialValue the starting value and default value
     */
    public Property(Codec<T> codec, T initialValue) {
        this.codec = codec;
        this.value = initialValue;
        this.defaultValue = initialValue;
    }

    /**
     * Retrieves the current value of the property.
     *
     * @return the current value
     */
    public T get() {
        return value;
    }

    /**
     * Retrieves the default value defined at creation.
     *
     * @return the default value
     */
    public T getDefault() {
        return defaultValue;
    }

    /**
     * Updates the current value of the property.
     *
     * @param value the new value to set
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Encodes the current value into a serialized format.
     *
     * @param ops the dynamic operations logic
     * @param <D> the data format type
     * @return a DataResult containing the encoded data object
     */
    public <D> DataResult<D> encode(DynamicOps<D> ops) {
        return codec.encode(ops, value);
    }

    /**
     * Decodes a value from a serialized format and updates the property.
     *
     * @param ops   the dynamic operations logic
     * @param input the serialized data object
     * @param <D>   the data format type
     * @return a DataResult representing the success or partial failure of the operation
     */
    public <D> DataResult<Void> decode(DynamicOps<D> ops, D input) {
        DataResult<T> result = codec.decode(ops, input);
        if (result.isError()) {
            return DataResult.error(result.dataError().get());
        }

        this.value = result.getOrThrow();

        if (result.isPartial()) {
            return DataResult.partial(null, result.warnings());
        }

        return DataResult.success(null);
    }
}