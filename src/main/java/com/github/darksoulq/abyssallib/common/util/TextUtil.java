package com.github.darksoulq.abyssallib.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for text handling, including:
 * <ul>
 *   <li>JSON serialization/deserialization with {@link Gson}</li>
 *   <li>MiniMessage parsing with {@link MiniMessage}</li>
 *   <li>String/array conversions</li>
 * </ul>
 */
public class TextUtil {
    /**
     * List of registered Gson type adapters for custom serialization and deserialization.
     */
    private static final List<TypeAdapterRegistration<?>> ADAPTERS = new ArrayList<>();

    /**
     * The shared {@link Gson} instance configured with registered type adapters.
     * <p>
     * Rebuilt whenever new type adapters are registered.
     */
    public static Gson GSON = new Gson();

    /**
     * The shared {@link MiniMessage} instance used for parsing MiniMessage text.
     */
    public static final MiniMessage MM = MiniMessage.miniMessage();

    /**
     * Builds the global {@link Gson} instance using all registered type adapters.
     * <p>
     * This method is automatically called when registering new adapters.
     */
    public static void buildGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                .setPrettyPrinting();

        for (TypeAdapterRegistration<?> reg : ADAPTERS) {
            if (reg.hierarchy) {
                builder.registerTypeHierarchyAdapter(reg.clazz, reg.adapter);
            } else {
                builder.registerTypeAdapter(reg.clazz, reg.adapter);
            }
        }

        GSON = builder.create();
    }

    /**
     * Registers a type adapter for a specific class and rebuilds the global {@link Gson} instance.
     *
     * @param clazz   the class to register the adapter for
     * @param adapter the type adapter instance
     * @param <T>     the class type
     */
    public static <T> void registerTypeAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, false));
        buildGson();
    }

    /**
     * Registers a type adapter for a class hierarchy and rebuilds the global {@link Gson} instance.
     *
     * @param clazz   the base class of the hierarchy
     * @param adapter the type adapter instance
     * @param <T>     the base class type
     */
    public static <T> void registerTypeHierarchyAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, true));
        buildGson();
    }

    /**
     * Converts a {@code String[]} into a {@link List}.
     *
     * @param arr the string array
     * @return a list backed by the array
     */
    public static List<String> convertToList(String[] arr) {
        return Arrays.asList(arr);
    }

    /**
     * Converts a {@link List} of strings into a {@code String[]} array.
     *
     * @param list the list of strings
     * @return a new array containing the elements of the list
     */
    public static String[] convertToArray(List<String> list) {
        return list.toArray(String[]::new);
    }

    /**
     * Parses a MiniMessage string into a {@link Component}.
     *
     * @param mmText the MiniMessage string
     * @return the parsed {@link Component}
     */
    public static Component parse(String mmText) {
        return MM.deserialize(mmText);
    }

    /**
     * Parses a MiniMessage string with custom {@link TagResolver}s into a {@link Component}.
     *
     * @param mmText    the MiniMessage string
     * @param resolvers the tag resolvers to apply
     * @return the parsed {@link Component}
     */
    public static Component parse(String mmText, TagResolver... resolvers) {
        return MM.deserialize(mmText, resolvers);
    }

    /**
     * Internal record of a registered Gson type adapter.
     *
     * @param <T> the class type
     */
    private record TypeAdapterRegistration<T>(Class<T> clazz, Object adapter, boolean hierarchy) {
        /**
         * Creates a new type adapter registration.
         *
         * @param clazz     the target class
         * @param adapter   the type adapter
         * @param hierarchy {@code true} if registered as a hierarchy adapter
         */
        private TypeAdapterRegistration {
        }
        }
}
