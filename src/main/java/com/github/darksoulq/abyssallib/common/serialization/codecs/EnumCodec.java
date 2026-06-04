package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.Optional;

/**
 * Codec for Java enum types.
 *
 * @param <E> Enum type.
 */
public class EnumCodec<E extends Enum<E>> implements Codec<E> {
    private final Class<E> enumClass;

    /**
     * Creates an enum codec.
     *
     * @param enumClass Target enum class.
     */
    public EnumCodec(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public <D> DataResult<E> decode(DynamicOps<D> ops, D input) {
        Optional<String> strOpt = ops.getStringValue(input);
        if (strOpt.isEmpty()) {
            return DataResult.error(DataError.typeMismatch("string", "unknown"));
        }

        String name = strOpt.get();
        try {
            return DataResult.success(Enum.valueOf(enumClass, name));
        } catch (IllegalArgumentException e) {
            return DataResult.error(DataError.unknownEnum(name, enumClass.getSimpleName()));
        }
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, E value) {
        return DataResult.success(ops.createString(value.name()));
    }

    @Override
    public String describe() {
        return "Enum[" + enumClass.getSimpleName() + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitEnum(enumClass);
    }
}