package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface DataComponentType<C extends DataComponent<?>> {
    Codec<C> codec();

    @Nullable
    default C createFromValue(Object value) {
        return null;
    }

    static <C extends DataComponent<?>> DataComponentType<C> simple(Codec<C> codec) {
        return () -> codec;
    }

    static <C extends DataComponent<?>, V> DataComponentType<C> valued(Codec<C> codec, Function<V, C> factory) {
        return new DataComponentType<>() {
            @Override
            public Codec<C> codec() {
                return codec;
            }

            @Override
            @SuppressWarnings("unchecked")
            public C createFromValue(Object value) {
                return factory.apply((V) value);
            }
        };
    }
}