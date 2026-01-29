package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.common.color.MiniMessageBridge;
import com.github.darksoulq.abyssallib.server.translation.internal.CustomTranslator;
import com.github.darksoulq.abyssallib.server.translation.internal.LanguageLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public final class ServerTranslator {
    private static final CustomTranslator TRANSLATOR = new CustomTranslator();
    private static final TranslatableComponentRenderer<Locale> RENDERER = TranslatableComponentRenderer.usingTranslationSource(GlobalTranslator.translator());

    public static void init() {
        GlobalTranslator.translator().addSource(TRANSLATOR);
        reload();
    }

    public static void reload() {
        LanguageLoader.load(TRANSLATOR);
    }

    public static Component translate(@Nullable Component component, @Nullable Locale locale) {
        if (component == null) return null;
        return RENDERER.render(component, locale != null ? locale : Locale.US);
    }

    public static Component translate(@Nullable Component component, @Nullable Player player) {
        return translate(component, player != null ? player.locale() : Locale.US);
    }

    public static Component render(@NotNull String key, @Nullable Player player, @NotNull TagResolver... extraResolvers) {
        Locale locale = player != null ? player.locale() : Locale.US;
        MessageFormat format = TRANSLATOR.translate(key, locale);
        String raw = format != null ? format.toPattern() : key;

        TagResolver[] resolvers = new TagResolver[extraResolvers.length + 1];
        resolvers[0] = PlaceholderService.resolve(player);
        System.arraycopy(extraResolvers, 0, resolvers, 1, extraResolvers.length);

        return MiniMessageBridge.parse(raw, resolvers);
    }

    public static CustomTranslator getSource() {
        return TRANSLATOR;
    }
}