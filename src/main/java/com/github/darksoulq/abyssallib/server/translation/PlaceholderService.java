package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.server.util.HookConstants;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides integration between the translation system and PlaceholderAPI (PAPI).
 * <p>
 * This service allows the use of PAPI placeholders within MiniMessage tags
 * via the {@code <papi:placeholder_name>} syntax.
 */
public final class PlaceholderService {

    /**
     * Creates a {@link TagResolver} that handles the {@code papi} tag.
     * <p>
     * Usage in MiniMessage: {@code <papi:player_name>}
     *
     * @param player The {@link Player} context for placeholder parsing.
     * @return A {@link TagResolver} if PAPI is enabled and player is non-null,
     * otherwise an empty resolver.
     */
    public static TagResolver resolve(@Nullable Player player) {
        if (player == null || !HookConstants.isEnabled(HookConstants.Plugin.PLACEHOLDER_API)) {
            return TagResolver.empty();
        }

        return TagResolver.resolver("papi", (queue, ctx) -> {
            String placeholder = queue.popOr("papi tag requires a placeholder argument").value();
            String parsed = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
            return Tag.selfClosingInserting(Component.text(parsed));
        });
    }

    /**
     * Applies PlaceholderAPI parsing to a raw string.
     *
     * @param player The {@link Player} context.
     * @param text   The raw text containing {@code %placeholder%} markers.
     * @return The parsed string, or the original text if PAPI is unavailable.
     */
    public static String apply(@Nullable Player player, @NotNull String text) {
        if (player == null || !HookConstants.isEnabled(HookConstants.Plugin.PLACEHOLDER_API)) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}