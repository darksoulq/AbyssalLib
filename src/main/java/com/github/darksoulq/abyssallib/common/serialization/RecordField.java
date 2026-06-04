package com.github.darksoulq.abyssallib.common.serialization;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a fully bound field within a structured record codec.
 *
 * @param <O> The parent object type.
 * @param <T> The type of the field's value.
 */
public class RecordField<O, T> {
    private final String name;
    private final List<String> aliases;
    private final Codec<T> codec;
    private final Function<O, T> getter;
    private final boolean optional;
    private final T defaultValue;

    /**
     * Constructs the finalized record mapping.
     *
     * @param name         The structural identifier for the field map key.
     * @param aliases      Legacy identification alternatives fallback map mapping nodes safely structurally defining targets.
     * @param codec        The target codec resolving the data.
     * @param getter       The extraction function routing data from the parent instance.
     * @param optional     Whether the field's absence should be tolerated.
     * @param defaultValue The fallback primitive value to utilize if missing.
     */
    public RecordField(String name, List<String> aliases, Codec<T> codec, Function<O, T> getter, boolean optional, T defaultValue) {
        this.name = name;
        this.aliases = aliases;
        this.codec = codec;
        this.getter = getter;
        this.optional = optional;
        this.defaultValue = defaultValue;
    }

    /**
     * @return The structural identifier for this field.
     */
    public String getName() {
        return name;
    }

    /**
     * Safely executes decoding logic against a provided map structure.
     * Handles missing key mitigation logic natively.
     *
     * @param <D> The underlying data format wrapper.
     * @param ops The dynamic operations layer.
     * @param map The map structure being targeted for decoding.
     * @return A DataResult detailing success or decoding failure.
     */
    public <D> DataResult<T> decodeFromMap(DynamicOps<D> ops, Map<D, D> map) {
        D node = map.get(ops.createString(name));

        if (node == null) {
            for (String alias : aliases) {
                node = map.get(ops.createString(alias));
                if (node != null) break;
            }
        }

        if (node == null) {
            if (optional) {
                return codec.decode(ops, ops.empty());
            } else if (defaultValue != null) {
                return DataResult.success(defaultValue);
            } else {
                return DataResult.error(DataError.missingField(name));
            }
        }
        return codec.decode(ops, node);
    }

    /**
     * Safely triggers encoding logic isolating the field from the parent object.
     *
     * @param <D>   The underlying data format wrapper.
     * @param ops   The dynamic operations layer.
     * @param value The parent instance containing the target field.
     * @return A DataResult containing the encoded node.
     */
    public <D> DataResult<D> encodeToMap(DynamicOps<D> ops, O value) {
        T fieldVal = getter.apply(value);
        return codec.encode(ops, fieldVal);
    }
}