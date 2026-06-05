package com.github.darksoulq.abyssallib.common.serialization.schema;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node within a generated schema model.
 */
public interface SchemaNode {

    /**
     * Converts this schema node into a standardized serialized representation
     * using the provided dynamic operations.
     *
     * @param ops the serialization operations provider
     * @param <T> the target output type
     * @return the structured representation of this schema
     */
    <T> T to(DynamicOps<T> ops);

    /**
     * Represents a primitive schema type.
     *
     * @param type primitive type identifier
     */
    record PrimitiveSchema(String type) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            return ops.createString(type);
        }
    }

    /**
     * Represents a structured record schema.
     *
     * @param fields record fields indexed by name
     */
    record RecordSchema(Map<String, FieldSchema> fields) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            fields.forEach((k, v) -> map.put(ops.createString(k), v.to(ops)));
            return ops.createMap(map);
        }
    }

    /**
     * Describes a field within a record schema.
     *
     * @param name field name
     * @param type field schema
     * @param optional whether the field is optional
     * @param defaultValue default value for the field
     * @param description field description
     */
    record FieldSchema(String name, SchemaNode type, boolean optional, Object defaultValue, String description) {
        /**
         * Converts this field schema into a structural representation.
         *
         * @param ops the serialization operations provider
         * @param <T> the target output type
         * @return the serialized field
         */
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), type.to(ops));
            map.put(ops.createString("optional"), ops.createBoolean(optional));

            if (defaultValue != null) {
                map.put(ops.createString("default"), ops.createString(defaultValue.toString()));
            }
            if (description != null && !description.isEmpty()) {
                map.put(ops.createString("description"), ops.createString(description));
            }
            return ops.createMap(map);
        }
    }

    /**
     * Represents a list schema.
     *
     * @param elementType schema of list elements
     */
    record ListSchema(SchemaNode elementType) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("list"));
            map.put(ops.createString("elements"), elementType.to(ops));
            return ops.createMap(map);
        }
    }

    /**
     * Represents a map schema.
     *
     * @param keyType schema of map keys
     * @param valueType schema of map values
     */
    record MapSchema(SchemaNode keyType, SchemaNode valueType) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("map"));
            map.put(ops.createString("keys"), keyType.to(ops));
            map.put(ops.createString("values"), valueType.to(ops));
            return ops.createMap(map);
        }
    }

    /**
     * Represents an enumeration schema.
     *
     * @param values allowed enum values
     */
    record EnumSchema(List<String> values) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("enum"));

            List<T> serializedValues = values.stream().map(ops::createString).toList();
            map.put(ops.createString("values"), ops.createList(serializedValues));

            return ops.createMap(map);
        }
    }

    /**
     * Represents a polymorphic schema selected by a discriminator field.
     *
     * @param discriminator field used to select a variant
     * @param variants available schema variants
     */
    record DispatchSchema(String discriminator, Map<String, SchemaNode> variants) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("polymorphic"));
            map.put(ops.createString("discriminator"), ops.createString(discriminator));

            Map<T, T> serializedVariants = new LinkedHashMap<>();
            variants.forEach((k, v) -> serializedVariants.put(ops.createString(k), v.to(ops)));
            map.put(ops.createString("variants"), ops.createMap(serializedVariants));

            return ops.createMap(map);
        }
    }

    /**
     * Represents an optional schema value.
     *
     * @param wrapped wrapped schema
     */
    record OptionalSchema(SchemaNode wrapped) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("optional"));
            map.put(ops.createString("wrapped"), wrapped.to(ops));
            return ops.createMap(map);
        }
    }

    /**
     * Represents a schema that may be one of two types.
     *
     * @param left left schema
     * @param right right schema
     */
    record EitherSchema(SchemaNode left, SchemaNode right) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("either"));
            map.put(ops.createString("left"), left.to(ops));
            map.put(ops.createString("right"), right.to(ops));
            return ops.createMap(map);
        }
    }

    /**
     * Represents a schema that may match one of several alternatives.
     *
     * @param variants alternative schema variants
     */
    record OneOfSchema(List<SchemaNode> variants) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("one_of"));

            List<T> serializedVariants = variants.stream().map(v -> v.to(ops)).toList();
            map.put(ops.createString("variants"), ops.createList(serializedVariants));

            return ops.createMap(map);
        }
    }

    /**
     * Represents a fixed ordered sequence of schema elements.
     *
     * @param elements tuple element schemas
     */
    record TupleSchema(List<SchemaNode> elements) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("tuple"));

            List<T> serializedElements = elements.stream().map(e -> e.to(ops)).toList();
            map.put(ops.createString("elements"), ops.createList(serializedElements));

            return ops.createMap(map);
        }
    }

    /**
     * Represents a schema whose structure cannot be determined.
     *
     * @param description description of the unknown schema
     */
    record UnknownSchema(String description) implements SchemaNode {
        @Override
        public <T> T to(DynamicOps<T> ops) {
            Map<T, T> map = new LinkedHashMap<>();
            map.put(ops.createString("type"), ops.createString("unknown"));
            map.put(ops.createString("description"), ops.createString(description));
            return ops.createMap(map);
        }
    }
}