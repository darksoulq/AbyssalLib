package com.github.darksoulq.abyssallib.server.translation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A provider that supplies custom MiniMessage strings dynamically for translatable keys globally.
 * <p>
 * This interface is used to inject custom text representations for any translated component
 * system-wide, taking priority over the standard language file dictionaries.
 * </p>
 */
@FunctionalInterface
public interface GlobalTranslationProvider {

    /**
     * Resolves a custom MiniMessage string for the given translation key and viewing player.
     *
     * @param key    The translation key being resolved (e.g., "message.welcome.format").
     * @param player The player viewing the translated component, or null if unavailable.
     * @return A MiniMessage string representing the resolved text, or null if this provider doesn't handle the given key.
     */
    @Nullable String resolve(@NotNull String key, @Nullable Player player);
}