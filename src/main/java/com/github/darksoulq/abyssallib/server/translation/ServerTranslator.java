package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.common.color.MiniMessageBridge;
import com.github.darksoulq.abyssallib.server.translation.internal.CustomTranslator;
import com.github.darksoulq.abyssallib.server.translation.internal.LanguageLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;

/**
 * A central utility for server-side translations, custom MiniMessage rendering, and localized item processing.
 * <p>
 * This class hooks into Adventure's GlobalTranslator to provide custom, on-the-fly component translations.
 * It also supports custom injected item providers to allow developers to replace translatable keys with code-defined MiniMessage strings.
 * </p>
 */
public final class ServerTranslator {

    /**
     * The custom underlying translator source for mapping translation keys to patterns.
     */
    private static final CustomTranslator TRANSLATOR = new CustomTranslator();

    /**
     * The renderer responsible for taking a translatable component and a locale, and returning a fully resolved localized component.
     */
    private static final TranslatableComponentRenderer<Locale> RENDERER = TranslatableComponentRenderer.usingTranslationSource(GlobalTranslator.translator());

    /**
     * A registry mapping plugins to their internal resource file paths to be loaded during translation reloads.
     */
    private static final Map<Plugin, List<String>> RESOURCE_FILES = new HashMap<>();

    /**
     * A list of external file paths registered to be loaded into the custom translator.
     */
    private static final List<Path> PATH_FILES = new ArrayList<>();

    /**
     * A list of active providers capable of replacing translation keys with dynamic MiniMessage text for item components.
     */
    private static final List<ItemTranslationProvider> ITEM_PROVIDERS = new ArrayList<>();

    /**
     * Initializes the server translator, adding its custom translator to the global translation source, and reloads languages.
     */
    public static void init() {
        GlobalTranslator.translator().addSource(TRANSLATOR);
        reload();
    }

    /**
     * Reloads all translation entries from the designated language files and plugin resources.
     */
    public static void reload() {
        LanguageLoader.load(TRANSLATOR);
        PATH_FILES.forEach(p -> LanguageLoader.loadFile(p, TRANSLATOR));
        RESOURCE_FILES.forEach((p, l) -> l.forEach(s -> LanguageLoader.loadResource(p, s, TRANSLATOR)));
    }

    /**
     * Registers a custom item translation provider.
     * Providers can define and resolve specific keys on items.
     *
     * @param provider The item translation provider to register.
     */
    public static void registerItemProvider(ItemTranslationProvider provider) {
        ITEM_PROVIDERS.add(provider);
    }

    /**
     * Translates the provided component using a specific locale.
     *
     * @param component The component to translate.
     * @param locale    The target locale. If null, {@link Locale#US} is used as the fallback.
     * @return The translated component.
     */
    public static Component translate(@Nullable Component component, @Nullable Locale locale) {
        if (component == null) return null;
        return RENDERER.render(component, locale != null ? locale : Locale.US);
    }

    /**
     * Translates the provided component tailored to a player's current locale.
     *
     * @param component The component to translate.
     * @param player    The target player to determine the locale. If null, {@link Locale#US} is used.
     * @return The translated component.
     */
    public static Component translate(@Nullable Component component, @Nullable Player player) {
        return translate(component, player != null ? player.locale() : Locale.US);
    }

    /**
     * Recursively processes an item's component (such as its name or a line of lore),
     * attempting to intercept and replace translatable keys using the registered item translation providers.
     * If no translation is found by providers or the server, it will remain a translatable component for the client to handle.
     *
     * @param component The root component to process.
     * @param player    The player viewing the item, or null if unavailable.
     * @param item      The item stack associated with this component translation.
     * @param context   The exact component context (e.g. NAME, CUSTOM_NAME, LORE).
     * @return The fully resolved component with key replacements applied, or null if the initial component is null.
     */
    public static Component translateItemComponent(@Nullable Component component, @Nullable Player player, @NotNull ItemStack item, @NotNull ItemTranslationContext context) {
        if (component == null) return null;
        return processComponent(component, player, item, context);
    }

    /**
     * Recursively scans the component tree for translatable components, replacing their keys via
     * the item translation providers or standard rendering. Missing keys are preserved as TranslatableComponents.
     *
     * @param component The component tree element to process.
     * @param player    The viewing player, or null if unavailable.
     * @param item      The associated item stack.
     * @param context   The component context (name or lore).
     * @return The processed and potentially altered component tree element.
     */
    private static Component processComponent(Component component, Player player, ItemStack item, ItemTranslationContext context) {
        if (component instanceof net.kyori.adventure.text.TranslatableComponent translatable) {
            String key = translatable.key();
            String resolvedMmString = null;

            for (ItemTranslationProvider provider : ITEM_PROVIDERS) {
                resolvedMmString = provider.resolve(key, item, player, context);
                if (resolvedMmString != null) break;
            }

            Component rendered;
            if (resolvedMmString != null) {
                rendered = MiniMessageBridge.parse(resolvedMmString, PlaceholderService.resolve(player));
            } else {
                Locale locale = player != null ? player.locale() : Locale.US;
                MessageFormat format = TRANSLATOR.translate(key, locale);

                if (format != null) {
                    rendered = MiniMessageBridge.parse(format.toPattern(), PlaceholderService.resolve(player));
                } else {
                    List<TranslationArgument> processedArgs = new ArrayList<>();
                    for (TranslationArgument arg : translatable.arguments()) {
                        Object val = arg.value();
                        if (val instanceof Component componentArg) {
                            processedArgs.add(TranslationArgument.component(processComponent(componentArg, player, item, context)));
                        } else {
                            processedArgs.add(arg);
                        }
                    }
                    rendered = translatable.arguments(processedArgs);
                }
            }

            List<Component> children = new ArrayList<>();
            for (Component child : translatable.children()) {
                children.add(processComponent(child, player, item, context));
            }
            return rendered.style(translatable.style().merge(rendered.style())).children(children);
        }

        if (!component.children().isEmpty()) {
            List<Component> children = new ArrayList<>();
            for (Component child : component.children()) {
                children.add(processComponent(child, player, item, context));
            }
            return component.children(children);
        }

        return component;
    }

    /**
     * Renders a translation key directly into a component, evaluating any applicable player placeholders or styles.
     * If the key cannot be found, it is returned as a raw translatable component to be handled by the client.
     *
     * @param key            The translation key to render.
     * @param player         The viewing player, which determines the locale and placeholder data.
     * @param extraResolvers Additional standard or custom resolvers to apply alongside standard parsing.
     * @return The fully rendered component instance, or a translatable component if the key doesn't exist.
     */
    public static Component render(@NotNull String key, @Nullable Player player, @NotNull TagResolver... extraResolvers) {
        Locale locale = player != null ? player.locale() : Locale.US;
        MessageFormat format = TRANSLATOR.translate(key, locale);

        if (format == null) {
            return Component.translatable(key);
        }

        TagResolver[] resolvers = new TagResolver[extraResolvers.length + 1];
        resolvers[0] = PlaceholderService.resolve(player);
        System.arraycopy(extraResolvers, 0, resolvers, 1, extraResolvers.length);

        return MiniMessageBridge.parse(format.toPattern(), resolvers);
    }

    /**
     * Registers and loads translations from a specific external system file path.
     *
     * @param path The system path pointing to the translation file or directory.
     */
    public static void loadFile(Path path) {
        LanguageLoader.loadFile(path, TRANSLATOR);
        PATH_FILES.add(path);
    }

    /**
     * Registers and loads translations from a bundled internal plugin resource.
     *
     * @param plugin       The plugin instance that owns the internal resource.
     * @param resourcePath The path pointing to the translation file within the plugin's jar.
     */
    public static void loadResource(Plugin plugin, String resourcePath) {
        LanguageLoader.loadResource(plugin, resourcePath, TRANSLATOR);
        RESOURCE_FILES.computeIfAbsent(plugin, p -> new ArrayList<>()).add(resourcePath);
    }

    /**
     * Retrieves the custom underlying translation source used by the ServerTranslator.
     *
     * @return The {@link CustomTranslator} instance.
     */
    public static CustomTranslator getSource() {
        return TRANSLATOR;
    }
}