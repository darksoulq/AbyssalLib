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

public class CustomTranslator implements Translator {
    private static final Key KEY = Key.key("abyssallib", "translator");
    private final Map<Locale, ResourceBundle> bundles = new ConcurrentHashMap<>();

    @Override
    public @NotNull Key name() {
        return KEY;
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        ResourceBundle bundle = getBundle(locale);
        if (bundle != null && bundle.containsKey(key)) {
            String format = bundle.getString(key);
            return new MessageFormat(format, locale);
        }
        return null;
    }

    public void register(Locale locale, ResourceBundle bundle) {
        bundles.put(locale, bundle);
    }

    public void unregisterAll() {
        bundles.clear();
    }

    @Nullable
    private ResourceBundle getBundle(Locale locale) {
        ResourceBundle bundle = bundles.get(locale);
        if (bundle == null && !locale.equals(Locale.US)) {
            return bundles.get(Locale.US);
        }
        return bundle;
    }
}