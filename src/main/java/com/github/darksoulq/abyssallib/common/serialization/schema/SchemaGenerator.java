package com.github.darksoulq.abyssallib.common.serialization.schema;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates schema representations from codec definitions.
 */
public class SchemaGenerator implements CodecVisitor<SchemaNode> {

    /**
     * Generates a schema from the supplied codec.
     *
     * @param codec root codec
     * @return generated schema
     */
    public static SchemaNode generate(Codec<?> codec) {
        return codec.accept(new SchemaGenerator());
    }

    @Override
    public SchemaNode visitPrimitive(String type) {
        return new SchemaNode.PrimitiveSchema(type);
    }

    @Override
    public SchemaNode visitRecord(Codec<?> codec) {
        return new SchemaNode.RecordSchema(Collections.emptyMap());
    }

    @Override
    public SchemaNode visitList(Codec<?> elementCodec) {
        return new SchemaNode.ListSchema(elementCodec.accept(this));
    }

    @Override
    public SchemaNode visitMap(Codec<?> keyCodec, Codec<?> valueCodec) {
        return new SchemaNode.MapSchema(keyCodec.accept(this), valueCodec.accept(this));
    }

    @Override
    public SchemaNode visitEnum(Class<? extends Enum<?>> enumClass) {
        List<String> values = new ArrayList<>();
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            values.add(constant.name());
        }
        return new SchemaNode.EnumSchema(values);
    }

    @Override
    public SchemaNode visitOptional(Codec<?> wrapped) {
        return new SchemaNode.OptionalSchema(wrapped.accept(this));
    }

    @Override
    public SchemaNode visitEither(Codec<?> left, Codec<?> right) {
        return new SchemaNode.EitherSchema(left.accept(this), right.accept(this));
    }

    @Override
    public SchemaNode visitDispatch(String discriminator) {
        return new SchemaNode.DispatchSchema(discriminator, Collections.emptyMap());
    }

    @Override
    public SchemaNode visitOneOf(List<Codec<?>> codecs) {
        List<SchemaNode> variants = new ArrayList<>();
        for (Codec<?> c : codecs) {
            variants.add(c.accept(this));
        }
        return new SchemaNode.OneOfSchema(variants);
    }

    @Override
    public SchemaNode visitTuple(List<Codec<?>> codecs) {
        List<SchemaNode> elements = new ArrayList<>();
        for (Codec<?> c : codecs) {
            elements.add(c.accept(this));
        }
        return new SchemaNode.TupleSchema(elements);
    }

    @Override
    public SchemaNode visitUnknown(String description) {
        return new SchemaNode.UnknownSchema(description);
    }
}