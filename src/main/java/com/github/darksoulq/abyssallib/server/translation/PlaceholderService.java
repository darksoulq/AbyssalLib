package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.server.util.HookConstants;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderService {

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

    public static String apply(@Nullable Player player, @NotNull String text) {
        if (player == null || !HookConstants.isEnabled(HookConstants.Plugin.PLACEHOLDER_API)) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}