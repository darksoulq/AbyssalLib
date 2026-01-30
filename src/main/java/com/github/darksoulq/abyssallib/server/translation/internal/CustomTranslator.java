package com.github.darksoulq.abyssallib.server.translation.internal;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A custom implementation of Adventure's {@link Translator} interface.
 * <p>
 * This class stores {@link ResourceBundle}s in a thread-safe map, allowing for
 * dynamic registration and retrieval of localized strings. It serves as the
 * bridge between raw property files and the high-level component rendering system.
 */
public class CustomTranslator implements Translator {
    /** The unique {@link Key} identifying this translation source within Adventure. */
    private static final Key KEY = Key.key("abyssallib", "translator");

    /** * A thread-safe map storing {@link ResourceBundle} data indexed by their
     * respective {@link Locale}.
     */
    private final Map<Locale, ResourceBundle> bundles = new ConcurrentHashMap<>();

    /**
     * @return The unique identifier for this translator instance.
     */
    @Override
    public @NotNull Key name() {
        return KEY;
    }

    /**
     * Attempts to translate a key into a {@link MessageFormat} for a specific locale.
     * <p>
     * If the specified locale is not found, the system will attempt to fall
     * back to the {@link Locale#US} bundle.
     *
     * @param key    The translation key to look up.
     * @param locale The target {@link Locale} for the translation.
     * @return A {@link MessageFormat} containing the translated pattern,
     * or {@code null} if the key is missing from both the target and fallback bundles.
     */
    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        ResourceBundle bundle = getBundle(locale);
        if (bundle != null && bundle.containsKey(key)) {
            String format = bundle.getString(key);
            return new MessageFormat(format, locale);
        }
        return null;
    }

    /**
     * Registers a new {@link ResourceBundle} for a specific {@link Locale}.
     *
     * @param locale The locale associated with the bundle.
     * @param bundle The {@link ResourceBundle} containing the translation keys and values.
     */
    public void register(Locale locale, ResourceBundle bundle) {
        bundles.put(locale, bundle);
    }

    /**
     * Clears all registered translation bundles from memory.
     */
    public void unregisterAll() {
        bundles.clear();
    }

    /**
     * Retrieves the bundle for the requested locale, or the fallback US bundle.
     *
     * @param locale The requested {@link Locale}.
     * @return The matching {@link ResourceBundle}, the US fallback, or {@code null} if neither exist.
     */
    @Nullable
    private ResourceBundle getBundle(Locale locale) {
        ResourceBundle bundle = bundles.get(locale);
        if (bundle == null && !locale.equals(Locale.US)) {
            return bundles.get(Locale.US);
        }
        return bundle;
    }
}