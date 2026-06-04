package com.github.darksoulq.abyssallib.common.serialization.schema;

import java.util.List;
import java.util.Map;

/**
 * Represents a node within a generated schema model.
 */
public interface SchemaNode {

    /**
     * Represents a primitive schema type.
     *
     * @param type primitive type identifier
     */
    record PrimitiveSchema(String type) implements SchemaNode {}

    /**
     * Represents a structured record schema.
     *
     * @param fields record fields indexed by name
     */
    record RecordSchema(Map<String, FieldSchema> fields) implements SchemaNode {}

    /**
     * Describes a field within a record schema.
     *
     * @param name field name
     * @param type field schema
     * @param optional whether the field is optional
     * @param defaultValue default value for the field
     * @param description field description
     */
    record FieldSchema(String name, SchemaNode type, boolean optional, Object defaultValue, String description) {}

    /**
     * Represents a list schema.
     *
     * @param elementType schema of list elements
     */
    record ListSchema(SchemaNode elementType) implements SchemaNode {}

    /**
     * Represents a map schema.
     *
     * @param keyType schema of map keys
     * @param valueType schema of map values
     */
    record MapSchema(SchemaNode keyType, SchemaNode valueType) implements SchemaNode {}

    /**
     * Represents an enumeration schema.
     *
     * @param values allowed enum values
     */
    record EnumSchema(List<String> values) implements SchemaNode {}

    /**
     * Represents a polymorphic schema selected by a discriminator field.
     *
     * @param discriminator field used to select a variant
     * @param variants available schema variants
     */
    record DispatchSchema(String discriminator, Map<String, SchemaNode> variants) implements SchemaNode {}

    /**
     * Represents an optional schema value.
     *
     * @param wrapped wrapped schema
     */
    record OptionalSchema(SchemaNode wrapped) implements SchemaNode {}

    /**
     * Represents a schema that may be one of two types.
     *
     * @param left left schema
     * @param right right schema
     */
    record EitherSchema(SchemaNode left, SchemaNode right) implements SchemaNode {}

    /**
     * Represents a schema that may match one of several alternatives.
     *
     * @param variants alternative schema variants
     */
    record OneOfSchema(List<SchemaNode> variants) implements SchemaNode {}

    /**
     * Represents a fixed ordered sequence of schema elements.
     *
     * @param elements tuple element schemas
     */
    record TupleSchema(List<SchemaNode> elements) implements SchemaNode {}

    /**
     * Represents a schema whose structure cannot be determined.
     *
     * @param description description of the unknown schema
     */
    record UnknownSchema(String description) implements SchemaNode {}
}