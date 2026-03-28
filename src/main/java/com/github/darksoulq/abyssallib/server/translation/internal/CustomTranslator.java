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
 * This class unpacks and stores translation keys in a thread-safe nested map, allowing for
 * dynamic registration and merging of localized strings across multiple files targeting the
 * same Locale. It serves as the bridge between raw property files and the high-level
 * component rendering system.
 * </p>
 */
public class CustomTranslator implements Translator {

    /**
     * The unique {@link Key} identifying this translation source within Adventure.
     */
    private static final Key KEY = Key.key("abyssallib", "translator");

    /**
     * A thread-safe nested map storing parsed translation key-value pairs indexed by their respective {@link Locale}.
     */
    private final Map<Locale, Map<String, String>> dictionary = new ConcurrentHashMap<>();

    /**
     * Retrieves the unique identifier for this translator instance.
     *
     * @return The translation key associated with this source.
     */
    @Override
    public @NotNull Key name() {
        return KEY;
    }

    /**
     * Attempts to translate a key into a {@link MessageFormat} for a specific locale.
     *
     * @param key    The translation key to look up.
     * @param locale The target {@link Locale} for the translation.
     * @return A {@link MessageFormat} containing the translated pattern, or {@code null} if missing.
     */
    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        String format = getRawTranslation(key, locale);
        if (format != null) {
            return new MessageFormat(format, locale);
        }
        return null;
    }

    /**
     * Retrieves the raw string format from the stored dictionary bypassing {@link MessageFormat} parsing.
     * This prevents unintended alteration of formatting characters such as single quotes and brackets.
     * Falls back to the Locale.US dictionary if the key is missing in the target locale.
     *
     * @param key    The translation key to look up.
     * @param locale The target {@link Locale} for the translation.
     * @return The raw, unparsed localized string, or {@code null} if missing.
     */
    public @Nullable String getRawTranslation(@NotNull String key, @NotNull Locale locale) {
        Map<String, String> localeMap = dictionary.get(locale);
        if (localeMap != null && localeMap.containsKey(key)) {
            return localeMap.get(key);
        }

        if (!locale.equals(Locale.US)) {
            Map<String, String> fallbackMap = dictionary.get(Locale.US);
            if (fallbackMap != null && fallbackMap.containsKey(key)) {
                return fallbackMap.get(key);
            }
        }

        return null;
    }

    /**
     * Unpacks a {@link ResourceBundle} and merges its keys into the specific {@link Locale} dictionary.
     * Existing keys will be overwritten if a duplicate is found in the new bundle.
     *
     * @param locale The locale associated with the bundle.
     * @param bundle The {@link ResourceBundle} containing the translation keys and values.
     */
    public void register(@NotNull Locale locale, @NotNull ResourceBundle bundle) {
        Map<String, String> localeMap = dictionary.computeIfAbsent(locale, k -> new ConcurrentHashMap<>());
        for (String key : bundle.keySet()) {
            localeMap.put(key, bundle.getString(key));
        }
    }

    /**
     * Clears all registered translation dictionaries from memory.
     */
    public void unregisterAll() {
        dictionary.clear();
    }
}