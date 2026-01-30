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

/**
 * The central management class for server-side translations and component rendering.
 * <p>
 * This class hooks into Adventure's {@link GlobalTranslator} to provide a custom
 * translation source. It handles the conversion of translation keys into rendered
 * {@link Component}s based on a user's {@link Locale}.
 */
public final class ServerTranslator {
    /** The custom translation source containing loaded language data. */
    private static final CustomTranslator TRANSLATOR = new CustomTranslator();

    /** The renderer used to transform translatable components into localized text. */
    private static final TranslatableComponentRenderer<Locale> RENDERER = TranslatableComponentRenderer.usingTranslationSource(GlobalTranslator.translator());

    /**
     * Initializes the translation system by registering the custom source
     * and performing an initial language load.
     */
    public static void init() {
        GlobalTranslator.translator().addSource(TRANSLATOR);
        reload();
    }

    /**
     * Reloads language files from the disk into the active translation source.
     */
    public static void reload() {
        LanguageLoader.load(TRANSLATOR);
    }

    /**
     * Translates a component for a specific locale.
     *
     * @param component The {@link Component} to translate.
     * @param locale    The target {@link Locale}. Defaults to {@link Locale#US} if null.
     * @return The rendered localized {@link Component}.
     */
    public static Component translate(@Nullable Component component, @Nullable Locale locale) {
        if (component == null) return null;
        return RENDERER.render(component, locale != null ? locale : Locale.US);
    }

    /**
     * Translates a component for a specific player based on their client language settings.
     *
     * @param component The {@link Component} to translate.
     * @param player    The {@link Player} whose locale will be used.
     * @return The rendered localized {@link Component}.
     */
    public static Component translate(@Nullable Component component, @Nullable Player player) {
        return translate(component, player != null ? player.locale() : Locale.US);
    }

    /**
     * Directly renders a translation key into a Component for a player.
     * <p>
     * This method fetches the raw pattern from the translator, resolves placeholders
     * (including PAPI), and parses the result using MiniMessage.
     *
     * @param key            The translation key (e.g., "messages.welcome").
     * @param player         The {@link Player} context for locale and placeholders.
     * @param extraResolvers Additional {@link TagResolver}s for custom tags.
     * @return A fully rendered {@link Component}.
     */
    public static Component render(@NotNull String key, @Nullable Player player, @NotNull TagResolver... extraResolvers) {
        Locale locale = player != null ? player.locale() : Locale.US;
        MessageFormat format = TRANSLATOR.translate(key, locale);
        String raw = format != null ? format.toPattern() : key;

        TagResolver[] resolvers = new TagResolver[extraResolvers.length + 1];
        resolvers[0] = PlaceholderService.resolve(player);
        System.arraycopy(extraResolvers, 0, resolvers, 1, extraResolvers.length);

        return MiniMessageBridge.parse(raw, resolvers);
    }

    /**
     * @return The internal {@link CustomTranslator} source.
     */
    public static CustomTranslator getSource() {
        return TRANSLATOR;
    }
}