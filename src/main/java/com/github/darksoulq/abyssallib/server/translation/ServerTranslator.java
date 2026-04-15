package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.common.color.MiniMessageBridge;
import com.github.darksoulq.abyssallib.server.placeholder.CustomPlaceholderResolver;
import com.github.darksoulq.abyssallib.server.translation.internal.CustomTranslator;
import com.github.darksoulq.abyssallib.server.translation.internal.LanguageLoader;
import com.github.darksoulq.abyssallib.world.item.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Utility class for managing server-side component translations and MiniMessage parsing.
 * <p>
 * This system decouples entirely from Adventure's standard GlobalTranslator to avert Server-Side Rendering conflicts.
 * It manually resolves formatting indexes, nested localized tags, and custom item-specific components prior to evaluating
 * the raw string through the overarching MiniMessage implementation.
 * </p>
 */
public final class ServerTranslator {

    /**
     * The custom source utilized for mapping dictionary translation keys directly to raw localized format patterns.
     */
    private static final CustomTranslator TRANSLATOR = new CustomTranslator();

    /**
     * A map storing target plugin instances paired to lists of internal resource paths defining language files.
     */
    private static final Map<Plugin, List<String>> RESOURCE_FILES = new HashMap<>();

    /**
     * A sequential list storing external system file paths containing parsed translation properties.
     */
    private static final List<Path> PATH_FILES = new ArrayList<>();

    /**
     * A sequential list of active providers utilized for dynamically substituting translation keys exclusively on item components.
     */
    private static final List<ItemTranslationProvider> ITEM_PROVIDERS = new ArrayList<>();

    /**
     * A sequential list of globally active providers mapped to override dictionary variables system-wide.
     */
    private static final List<GlobalTranslationProvider> GLOBAL_PROVIDERS = new ArrayList<>();

    /**
     * A sequential list of globally active TagResolvers applied to all translated components.
     */
    private static final List<TagResolver> GLOBAL_RESOLVERS = new ArrayList<>();

    /**
     * Initializes the server translation module and invokes the primary language loading sequence.
     */
    public static void init() {
        reload();
    }

    /**
     * Reloads all active translation configurations traversing registered system files and internally bundled plugin resources.
     */
    public static void reload() {
        LanguageLoader.load(TRANSLATOR);
        PATH_FILES.forEach(p -> LanguageLoader.loadFile(p, TRANSLATOR));
        RESOURCE_FILES.forEach((p, l) -> l.forEach(s -> LanguageLoader.loadResource(p, s, TRANSLATOR)));
    }

    /**
     * Registers a custom item translation provider empowering dynamic resolution of item-bound translatable component keys.
     *
     * @param provider The provider implementation added to the internal resolution registry.
     */
    public static void registerItemProvider(@NotNull ItemTranslationProvider provider) {
        ITEM_PROVIDERS.add(provider);
    }

    /**
     * Registers a global translation provider empowering dynamic resolution of component keys system-wide.
     *
     * @param provider The provider implementation added to the global resolution registry.
     */
    public static void registerGlobalProvider(@NotNull GlobalTranslationProvider provider) {
        GLOBAL_PROVIDERS.add(provider);
    }

    /**
     * Registers a global TagResolver that will be applied to all translated components.
     *
     * @param resolver The TagResolver to add to the global registry.
     */
    public static void registerGlobalResolver(@NotNull TagResolver resolver) {
        GLOBAL_RESOLVERS.add(resolver);
    }

    /**
     * Translates a provided raw component directly utilizing the specified system locale.
     *
     * @param component The raw translatable component requiring localization resolution.
     * @param locale    The language locale identifying the corresponding target dictionary entries.
     * @return A localized component resolving all internally mapped keys and parameters.
     */
    public static Component translate(@Nullable Component component, @Nullable Locale locale) {
        if (component == null) return null;
        return resolveComponent(component, null, locale != null ? locale : Locale.US, null, null);
    }

    /**
     * Translates a provided raw component adapting directly to an observing player's locale and placeholder contexts.
     *
     * @param component The raw translatable component requiring localization resolution.
     * @param player    The viewing player whose client locale and active placeholders execute against the text.
     * @return A localized component resolving mapped parameters accurately assigned to the viewer.
     */
    public static Component translate(@Nullable Component component, @Nullable Player player) {
        if (component == null) return null;
        return resolveComponent(component, player, player != null ? player.locale() : Locale.US, null, null);
    }

    /**
     * Contextually evaluates an item's specific translatable component conditionally applying registered item translation providers.
     *
     * @param component The raw item component requiring immediate localization processing.
     * @param player    The target viewer currently examining the inventory item stack.
     * @param item      The interactive item stack directly associated with the specific component context.
     * @param context   The specific area categorical classification rendering the component.
     * @return A natively localized component resolving keys distinctively relative to the encompassing item.
     */
    public static Component translateItemComponent(@Nullable Component component, @Nullable Player player, @NotNull ItemStack item, @NotNull ItemTranslationContext context) {
        if (component == null) return null;
        return resolveComponent(component, player, player != null ? player.locale() : Locale.US, item, context);
    }

    /**
     * Deeply processes a standard component tree recursively converting formatting indexes, assessing custom nested tags,
     * serializing parameters, and finally interpreting raw strings safely through MiniMessage evaluators.
     *
     * @param component The isolated element actively evaluated within the component tree structure.
     * @param player    The targeted audience viewer mapped against corresponding system placeholders.
     * @param locale    The active locale dictionary determining precise translated string output variations.
     * @param item      The relevant underlying item stack if rendering targets an item specific interface context.
     * @param context   The locational representation of the active item rendering phase.
     * @return The correctly evaluated and rendered functional component maintaining structural styling formats.
     */
    private static Component resolveComponent(@NotNull Component component, @Nullable Player player, @NotNull Locale locale, @Nullable ItemStack item, @Nullable ItemTranslationContext context) {
        if (component instanceof TranslatableComponent translatable) {
            String key = translatable.key();
            String pattern = null;

            if (item != null && context != null) {
                Item customItem = Item.resolve(item);
                if (customItem != null) {
                    for (ItemTranslationProvider provider : customItem.getTranslationProviders()) {
                        pattern = provider.resolve(key, item, player, context);
                        if (pattern != null) break;
                    }
                }
                if (pattern == null) {
                    for (ItemTranslationProvider provider : ITEM_PROVIDERS) {
                        pattern = provider.resolve(key, item, player, context);
                        if (pattern != null) break;
                    }
                }
            }

            if (pattern == null) {
                for (GlobalTranslationProvider provider : GLOBAL_PROVIDERS) {
                    pattern = provider.resolve(key, player);
                    if (pattern != null) break;
                }
            }

            if (pattern == null) {
                pattern = TRANSLATOR.getRawTranslation(key, locale);
            }

            if (pattern == null) {
                pattern = translatable.fallback();
            }

            if (pattern != null) {
                List<Component> args = extractArguments(translatable);

                StringBuilder sb = new StringBuilder(pattern);

                for (int i = 0; i < args.size(); i++) {
                    Component argComp = resolveComponent(args.get(i), player, locale, item, context);
                    String serialized = MiniMessage.miniMessage().serialize(argComp);

                    replaceInBuilder(sb, "{" + i + "}", serialized);
                    replaceInBuilder(sb, "%" + (i + 1) + "$s", serialized);
                    replaceInBuilder(sb, "%" + (i + 1) + "$d", serialized);
                }

                int sIdx;
                int argCounter = 0;
                while ((sIdx = sb.indexOf("%s")) != -1 && argCounter < args.size()) {
                    Component argComp = resolveComponent(args.get(argCounter), player, locale, item, context);
                    String serialized = MiniMessage.miniMessage().serialize(argComp);
                    sb.replace(sIdx, sIdx + 2, serialized);
                    argCounter++;
                }

                String parsed = sb.toString();

                for (int i = 0; i < args.size(); i++) {
                    Component argComp = resolveComponent(args.get(i), player, locale, item, context);
                    String serialized = MiniMessage.miniMessage().serialize(argComp);
                    parsed = parsed.replaceAll("\\{" + i + ",[^}]+\\}", Matcher.quoteReplacement(serialized));
                }

                List<TagResolver> resolvers = new ArrayList<>(GLOBAL_RESOLVERS);
                if (player != null) {
                    resolvers.add(PlaceholderService.resolve(player));
                }

                resolvers.add(GlyphService.resolve());
                resolvers.add(CustomPlaceholderResolver.resolve(player));

                resolvers.add(TagResolver.resolver(Set.of("tr", "translate"), (queue, ctx) -> {
                    if (!queue.hasNext()) return Tag.inserting(Component.empty());
                    String nestedKey = queue.pop().value();
                    return Tag.inserting(resolveComponent(Component.translatable(nestedKey), player, locale, item, context));
                }));

                Component rendered = MiniMessageBridge.parse(parsed, resolvers.toArray(new TagResolver[0]));
                List<Component> finalChildren = new ArrayList<>(rendered.children());
                for (Component child : translatable.children()) {
                    finalChildren.add(resolveComponent(child, player, locale, item, context));
                }

                return rendered.style(translatable.style().merge(rendered.style())).children(finalChildren);
            } else {
                List<TranslationArgument> processedArgs = new ArrayList<>();
                for (TranslationArgument arg : translatable.arguments()) {
                    if (arg.value() instanceof Component comp) {
                        processedArgs.add(TranslationArgument.component(resolveComponent(comp, player, locale, item, context)));
                    } else {
                        processedArgs.add(arg);
                    }
                }

                List<Component> finalChildren = new ArrayList<>();
                for (Component child : translatable.children()) {
                    finalChildren.add(resolveComponent(child, player, locale, item, context));
                }

                return translatable.arguments(processedArgs).children(finalChildren);
            }
        }

        if (!component.children().isEmpty()) {
            List<Component> children = new ArrayList<>();
            for (Component child : component.children()) {
                children.add(resolveComponent(child, player, locale, item, context));
            }
            return component.children(children);
        }

        return component;
    }

    /**
     * Targets and overwrites identified character sequences strictly and sequentially inside a designated builder scope.
     *
     * @param sb          The builder subject instance requiring character substitutions.
     * @param target      The specified constant substring actively targeted for deletion.
     * @param replacement The substituting text string replacing the targeted characters identically.
     */
    private static void replaceInBuilder(@NotNull StringBuilder sb, @NotNull String target, @NotNull String replacement) {
        int idx = sb.indexOf(target);
        while (idx != -1) {
            sb.replace(idx, idx + target.length(), replacement);
            idx = sb.indexOf(target, idx + replacement.length());
        }
    }

    /**
     * Accumulates completely formatted and safely isolated component arguments extracted safely from the parent instance.
     *
     * @param translatable The localized interface parent enforcing stored internal variables constraints.
     * @return A cleanly isolated listing encompassing all translated subset values mapped sequentially.
     */
    private static List<Component> extractArguments(@NotNull TranslatableComponent translatable) {
        List<Component> extracted = new ArrayList<>();
        try {
            for (TranslationArgument arg : translatable.arguments()) {
                if (arg.value() instanceof Component comp) {
                    extracted.add(comp);
                } else {
                    extracted.add(Component.text(String.valueOf(arg.value())));
                }
            }
        } catch (Throwable ignored) {}

        if (extracted.isEmpty()) {
            extracted.addAll(translatable.args());
        }
        return extracted;
    }

    /**
     * Instigates singular execution interpreting unique translation targets directly avoiding generalized component generation.
     *
     * @param key            The singular translation identifier accessing active application dictionary resources.
     * @param player         The viewing target allocating accurate visual logic placeholders against variables.
     * @param extraResolvers Sequential array storing externally mandated resolution mappings executed during translation.
     * @return Formatted static text structure mirroring underlying system configurations effectively.
     */
    public static Component render(@NotNull String key, @Nullable Player player, @NotNull TagResolver... extraResolvers) {
        Locale locale = player != null ? player.locale() : Locale.US;
        String pattern = null;

        for (GlobalTranslationProvider provider : GLOBAL_PROVIDERS) {
            pattern = provider.resolve(key, player);
            if (pattern != null) break;
        }

        if (pattern == null) {
            pattern = TRANSLATOR.getRawTranslation(key, locale);
        }

        if (pattern == null) {
            return Component.translatable(key);
        }

        List<TagResolver> resolvers = new ArrayList<>(GLOBAL_RESOLVERS);
        resolvers.addAll(Arrays.asList(extraResolvers));

        if (player != null) {
            resolvers.add(PlaceholderService.resolve(player));
        }

        resolvers.add(GlyphService.resolve());

        resolvers.add(TagResolver.resolver(Set.of("tr", "translate"), (queue, ctx) -> {
            if (!queue.hasNext()) return Tag.inserting(Component.empty());
            String nestedKey = queue.pop().value();
            return Tag.inserting(resolveComponent(Component.translatable(nestedKey), player, locale, null, null));
        }));

        return MiniMessageBridge.parse(pattern, resolvers.toArray(new TagResolver[0]));
    }

    /**
     * Integrates raw translation properties parsed strictly from externally registered storage allocations.
     *
     * @param path The operating system file target navigating specifically to the language properties.
     */
    public static void loadFile(@NotNull Path path) {
        LanguageLoader.loadFile(path, TRANSLATOR);
        PATH_FILES.add(path);
    }

    /**
     * Incorporates raw property details derived inherently from a specific compiled project resource payload.
     *
     * @param plugin       The compiled java module acting as root target for path acquisition algorithms.
     * @param resourcePath The internally nested pathway mapped cleanly against the desired asset array.
     */
    public static void loadResource(@NotNull Plugin plugin, @NotNull String resourcePath) {
        LanguageLoader.loadResource(plugin, resourcePath, TRANSLATOR);
        RESOURCE_FILES.computeIfAbsent(plugin, p -> new ArrayList<>()).add(resourcePath);
    }

    /**
     * Returns the master translation interface acting as fundamental repository controlling formatting operations.
     *
     * @return The translation structure mapped against active project variables determining overall strings.
     */
    public static CustomTranslator getSource() {
        return TRANSLATOR;
    }
}