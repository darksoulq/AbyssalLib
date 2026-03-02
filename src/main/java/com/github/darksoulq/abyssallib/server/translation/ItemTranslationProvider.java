package com.github.darksoulq.abyssallib.server.translation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A provider that supplies custom MiniMessage strings dynamically for translatable keys in items.
 * <p>
 * This interface is used to inject custom text representations directly into an item's name or lore via code.
 * </p>
 */
@FunctionalInterface
public interface ItemTranslationProvider {

    /**
     * Resolves a custom MiniMessage string for the given translation key, item, and player within the specified context.
     *
     * @param key     The translation key being resolved (e.g., "component.lore.item_level").
     * @param item    The item stack currently being translated.
     * @param player  The player viewing the item, or null if unavailable.
     * @param context The specific context (name, custom name, or lore) where this translation is applied.
     * @return A MiniMessage string representing the resolved text, or null if this provider doesn't handle the given key.
     */
    @Nullable String resolve(@NotNull String key, @NotNull ItemStack item, @Nullable Player player, @NotNull ItemTranslationContext context);
}