package com.github.darksoulq.abyssallib.server.translation;

/**
 * Represents the context in which an item's component is being translated.
 * <p>
 * This allows translation providers to differentiate between an item's default name,
 * a player-set custom name, or lines within the item's lore.
 * </p>
 */
public enum ItemTranslationContext {
    /**
     * Indicates that the default, unedited name of the item is being translated.
     */
    NAME,

    /**
     * Indicates that the custom, player-set name of the item is being translated.
     */
    CUSTOM_NAME,

    /**
     * Indicates that a line within the item's lore is being translated.
     */
    LORE
}