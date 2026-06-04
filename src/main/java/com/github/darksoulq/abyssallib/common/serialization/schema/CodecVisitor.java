package com.github.darksoulq.abyssallib.common.serialization.schema;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

import java.util.List;

/**
 * Visitor interface used to traverse codec structures and produce a result.
 *
 * @param <R> result type produced during traversal
 */
public interface CodecVisitor<R> {

    /**
     * Visits a primitive codec type.
     *
     * @param type primitive type identifier
     * @return visitor result
     */
    R visitPrimitive(String type);

    /**
     * Visits a record codec.
     *
     * @param codec record codec being visited
     * @return visitor result
     */
    R visitRecord(Codec<?> codec);

    /**
     * Visits a list codec.
     *
     * @param elementCodec codec describing list elements
     * @return visitor result
     */
    R visitList(Codec<?> elementCodec);

    /**
     * Visits a map codec.
     *
     * @param keyCodec codec describing map keys
     * @param valueCodec codec describing map values
     * @return visitor result
     */
    R visitMap(Codec<?> keyCodec, Codec<?> valueCodec);

    /**
     * Visits an enum codec.
     *
     * @param enumClass enum type represented by the codec
     * @return visitor result
     */
    R visitEnum(Class<? extends Enum<?>> enumClass);

    /**
     * Visits an optional codec.
     *
     * @param wrapped wrapped codec
     * @return visitor result
     */
    R visitOptional(Codec<?> wrapped);

    /**
     * Visits a codec representing one of two possible types.
     *
     * @param left left codec
     * @param right right codec
     * @return visitor result
     */
    R visitEither(Codec<?> left, Codec<?> right);

    /**
     * Visits a dispatch codec.
     *
     * @param discriminator field used to determine the target variant
     * @return visitor result
     */
    R visitDispatch(String discriminator);

    /**
     * Visits a codec that may match one of several alternatives.
     *
     * @param codecs alternative codecs
     * @return visitor result
     */
    R visitOneOf(List<Codec<?>> codecs);

    /**
     * Visits a codec representing a fixed ordered sequence of values.
     *
     * @param codecs codecs describing tuple elements
     * @return visitor result
     */
    R visitTuple(List<Codec<?>> codecs);

    /**
     * Visits an unknown or unsupported codec type.
     *
     * @param description description of the codec
     * @return visitor result
     */
    R visitUnknown(String description);
}