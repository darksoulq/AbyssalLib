package com.github.darksoulq.abyssallib.server.resource;

import com.github.darksoulq.abyssallib.server.resource.asset.*;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A logical asset container for a given namespace inside a resource pack.
 * All files will be emitted under {@code assets/<namespace>/...}.
 */
public class Namespace {

    /** Owning plugin. Used for autoloading resources from JAR. */
    private final Plugin plugin;

    /** The namespace used in paths like {@code assets/<namespace>/...}. */
    private final String namespace;

    /** The resource pack this namespace belongs to. */
    private final ResourcePack pack;

    /** All registered assets under this namespace. */
    private final List<Asset> assets = new ArrayList<>();

    /**
     * Creates a new namespace under a specific pack.
     *
     * @param namespace The namespace
     * @param pack      The parent resource pack
     */
    public Namespace(@NotNull String namespace, @NotNull ResourcePack pack) {
        this.plugin = pack.getPlugin();
        this.namespace = namespace;
        this.pack = pack;
    }

    /**
     * Creates a texture asset with autoload from plugin JAR.
     *
     * @param path Asset path (e.g. {@code item/sword})
     * @return Texture asset
     */
    public @NotNull Texture texture(@NotNull String path) {
        return texture(path, null);
    }

    /**
     * Creates a texture asset.
     *
     * @param path Asset path (e.g. {@code item/sword})
     * @param data Optional texture bytes. If {@code null}, tries to load from plugin JAR.
     * @return Texture asset
     */
    public @NotNull Texture texture(@NotNull String path, byte @Nullable [] data) {
        Texture t = (data == null)
                ? new Texture(plugin, namespace, path)
                : new Texture(namespace, path, data);
        assets.add(t);
        return t;
    }

    /**
     * Creates a font asset.
     *
     * @param name     Font name (e.g. {@code default})
     * @param autoLoad If true, loads {@code resourcepack/font/<name>} from plugin JAR
     * @return Font asset
     */
    public @NotNull Font font(@NotNull String name, boolean autoLoad) {
        Font f = autoLoad
                ? new Font(plugin, namespace, name)
                : new Font(namespace, name);
        assets.add(f);
        return f;
    }

    /**
     * Creates a model asset.
     *
     * @param name     Model name (without extension)
     * @param autoLoad If true, loads model from {@code resourcepack/models/<name>} in plugin JAR
     * @return Model asset
     */
    public @NotNull Model model(@NotNull String name, boolean autoLoad) {
        Model m = autoLoad
                ? new Model(plugin, namespace, name)
                : new Model(namespace, name);
        assets.add(m);
        return m;
    }

    /**
     * Creates a language file asset.
     *
     * @param locale   Locale code (e.g. {@code en_us})
     * @param autoLoad If true, loads from {@code resourcepack/lang/<locale>} in plugin JAR
     * @return Lang asset
     */
    public @NotNull Lang lang(@NotNull String locale, boolean autoLoad) {
        Lang lang = autoLoad
                ? new Lang(plugin, namespace, locale)
                : new Lang(namespace, locale);
        assets.add(lang);
        return lang;
    }

    /**
     * Creates a new sound registry asset. No autoloading!
     *
     * @return Sounds asset
     */
    public @NotNull Sounds sounds() {
        Sounds s = new Sounds(plugin, namespace);
        assets.add(s);
        return s;
    }

    /**
     * Creates an item definition asset.
     *
     * @param name The item name (e.g. {@code custom_sword})
     * @return ItemDefinition asset
     */
    public @NotNull ItemDefinition itemDefinition(@NotNull String name) {
        ItemDefinition def = new ItemDefinition(plugin, namespace, name);
        assets.add(def);
        return def;
    }

    /**
     * Creates an item definition asset.
     *
     * @param name The item name (e.g. {@code custom_sword})
     * @param selector The model provider, can be nested.
     * @param handAnimationOnSwap whether swapping item should trigger hand animation
     * @return ItemDefinition asset
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, boolean handAnimationOnSwap) {
        ItemDefinition def = new ItemDefinition(namespace, name, selector, handAnimationOnSwap);
        assets.add(def);
        return def;
    }

    /**
     * Emits all asset files under this namespace into the given map.
     *
     * @param files File map to write to
     */
    public void emit(@NotNull Map<String, byte[]> files) {
        for (Asset asset : assets) {
            asset.emit(files);
        }
    }

    /**
     * Builds a namespaced ID for something.
     *
     * @param path Path (e.g. {@code sword})
     * @return Fully qualified ID (e.g. {@code myplugin:sword})
     */
    public @NotNull String id(@NotNull String path) {
        return namespace + ":" + path;
    }

    /**
     * @return Owning plugin
     */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /**
     * @return Namespace (modid)
     */
    public @NotNull String getNamespace() {
        return namespace;
    }

    /**
     * @return Owning resource pack
     */
    public @NotNull ResourcePack getPack() {
        return pack;
    }
}
