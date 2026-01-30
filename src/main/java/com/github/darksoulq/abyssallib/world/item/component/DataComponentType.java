package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents the definition and serialization logic for a specific type of {@link DataComponent}.
 * <p>
 * This interface acts as a factory and a registry key, providing the necessary {@link Codec}
 * to transform component data to and from various formats (NBT, JSON, etc.).
 * </p>
 *
 * @param <C> The specific implementation of {@link DataComponent} associated with this type.
 */
public interface DataComponentType<C extends DataComponent<?>> {

    /**
     * Retrieves the codec used for serializing and deserializing this component type.
     *
     * @return The {@link Codec} instance for components of type {@code C}.
     */
    Codec<C> codec();

    /**
     * Attempts to create a new component instance directly from a raw value.
     * <p>
     * This is primarily utilized by the library's internal systems to wrap vanilla
     * Minecraft data components or raw values into the AbyssalLib component system.
     * </p>
     *
     * @param value The raw value to wrap (e.g., an Integer for MaxStackSize).
     * @return A new instance of {@code C}, or {@code null} if this type does not support direct factory creation.
     */
    @Nullable
    default C createFromValue(Object value) {
        return null;
    }

    /**
     * Creates a simple DataComponentType that relies solely on its codec for instantiation.
     *
     * @param <C>   The component type.
     * @param codec The {@link Codec} to handle data transformation.
     * @return A new {@link DataComponentType} instance.
     */
    static <C extends DataComponent<?>> DataComponentType<C> simple(Codec<C> codec) {
        return () -> codec;
    }

    /**
     * Creates a valued DataComponentType with a factory function for direct instantiation.
     * <p>
     * This variant is specifically intended for components that map to vanilla Minecraft
     * data components, allowing the library to quickly wrap NMS values into the API.
     * </p>
     *
     * @param <C>     The component type.
     * @param <V>     The raw value type (e.g., Boolean, String, or custom object).
     * @param codec   The {@link Codec} for the component.
     * @param factory A {@link Function} that accepts a raw value of type {@code V} and returns a component of type {@code C}.
     * @return A new {@link DataComponentType} instance with factory support.
     */
    static <C extends DataComponent<?>, V> DataComponentType<C> valued(Codec<C> codec, Function<V, C> factory) {
        return new DataComponentType<>() {
            /**
             * @return The provided serialization codec.
             */
            @Override
            public Codec<C> codec() {
                return codec;
            }

            /**
             * Casts the provided object and applies the factory function to create a component.
             *
             * @param value The raw input value.
             * @return The instantiated component.
             */
            @Override
            @SuppressWarnings("unchecked")
            public C createFromValue(Object value) {
                return factory.apply((V) value);
            }
        };
    }
}