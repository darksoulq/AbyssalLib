package com.github.darksoulq.abyssallib.common.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A builder for defining a field within a structured record codec.
 *
 * @param <T> The type of the field's value.
 */
public class FieldBuilder<T> {
    private final String name;
    private final Codec<T> codec;
    private final boolean optional;
    private final T defaultValue;
    private final List<String> aliases = new ArrayList<>();

    /**
     * Constructs the intermediary field builder.
     *
     * @param name         The structural identifier for the field map key.
     * @param codec        The target codec resolving the data.
     * @param optional     Whether the field's absence should be tolerated.
     * @param defaultValue The fallback primitive value to utilize if missing.
     */
    public FieldBuilder(String name, Codec<T> codec, boolean optional, T defaultValue) {
        this.name = name;
        this.codec = codec;
        this.optional = optional;
        this.defaultValue = defaultValue;
    }

    /**
     * Registers alternative string paths to parse seamlessly mapping backward-compatibility configurations efficiently.
     *
     * @param alternatives Collection encapsulating legacy iteration node parsing path parameters cleanly.
     * @return Chaining reference modifying parent structural definitions properly mapped safely.
     */
    public FieldBuilder<T> alias(String... alternatives) {
        this.aliases.addAll(Arrays.asList(alternatives));
        return this;
    }

    /**
     * Binds this field definition to a getter function of the parent object.
     *
     * @param <O>    The parent object type.
     * @param getter The function used to retrieve the field's value from the parent.
     * @return A completed record field definition ready for use in a RecordBuilder.
     */
    public <O> RecordField<O, T> forGetter(Function<O, T> getter) {
        return new RecordField<>(name, aliases, codec, getter, optional, defaultValue);
    }

    /**
     * Binds this field definition to a getter function of the parent object, anchoring the type context.
     *
     * @param type   The class literal of the parent object.
     * @param <O>    The parent object type.
     * @param getter The function used to retrieve the field's value from the parent.
     * @return A completed record field definition ready for use in a RecordBuilder.
     */
    public <O> RecordField<O, T> forGetter(Class<O> type, Function<O, T> getter) {
        return new RecordField<>(name, aliases, codec, getter, optional, defaultValue);
    }
}