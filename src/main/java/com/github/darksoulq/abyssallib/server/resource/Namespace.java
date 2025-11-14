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
 * Represents a logical namespace inside a resource pack.
 * All emitted files are placed under {@code assets/<namespace>/}.
 */
public class Namespace {

    /** Owning plugin. Used to autoload embedded resource files from JAR. */
    private final Plugin plugin;

    /** The namespace name, e.g. {@code myplugin}. */
    private final String namespace;

    /** The parent resource pack. */
    private final ResourcePack pack;

    /** All assets registered under this namespace. */
    private final List<Asset> assets = new ArrayList<>();

    /**
     * Creates a new namespace under the given resource pack.
     *
     * @param namespace Namespace ID (e.g. {@code myplugin})
     * @param pack      Parent resource pack
     */
    public Namespace(@NotNull String namespace, @NotNull ResourcePack pack) {
        this.plugin = pack.getPlugin();
        this.namespace = namespace;
        this.pack = pack;
    }

    /**
     * Registers a texture from {@code resourcepack/<namespace>/textures/<path>.png}.
     *
     * @param path Texture path (e.g. {@code item/sword})
     * @return Registered texture asset
     */
    public @NotNull Texture texture(@NotNull String path) {
        return texture(path, null);
    }

    /**
     * Registers a texture asset.
     *
     * @param path Texture path (e.g. {@code item/sword})
     * @param data Texture bytes; if null, loads from plugin JAR
     * @return Registered texture asset
     */
    public @NotNull Texture texture(@NotNull String path, byte @Nullable [] data) {
        Texture t = (data == null)
                ? new Texture(plugin, namespace, path)
                : new Texture(namespace, path, data);
        assets.add(t);
        return t;
    }

    /**
     * Registers a .mcmeta file asset (e.g. animation metadata).
     *
     * @param path     File path (e.g. {@code item/sword})
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/textures/<path>.png.mcmeta}
     * @return Registered McMeta asset
     */
    public McMeta mcmeta(String path, boolean autoLoad) {
        McMeta m = new McMeta(plugin, namespace, path, autoLoad);
        assets.add(m);
        return m;
    }

    /**
     * Registers a waypoint style asset.
     *
     * @param name     Style name
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/waypoint_style/<name>.json}
     * @return Registered waypoint style asset
     */
    public WaypointStyle waypointStyle(String name, boolean autoLoad) {
        WaypointStyle w = autoLoad ? new WaypointStyle(plugin, namespace, name) : new WaypointStyle(namespace, name);
        assets.add(w);
        return w;
    }

    /**
     * Registers a post-processing effect asset.
     *
     * @param name     Effect name
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/post_effect/<name>.json}
     * @return Registered post effect asset
     */
    public PostEffect postEffect(String name, boolean autoLoad) {
        PostEffect p = autoLoad ? new PostEffect(plugin, namespace, name) : new PostEffect(namespace, name);
        assets.add(p);
        return p;
    }

    /**
     * Registers a font asset.
     *
     * @param name     Font name (e.g. {@code default})
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/font/<name>.json}
     * @return Registered font asset
     */
    public @NotNull Font font(@NotNull String name, boolean autoLoad) {
        Font f = autoLoad ? new Font(plugin, namespace, name) : new Font(namespace, name);
        assets.add(f);
        return f;
    }

    /**
     * Registers a model asset.
     *
     * @param name     Model name (without extension)
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/models/<name>.json}
     * @return Registered model asset
     */
    public @NotNull Model model(@NotNull String name, boolean autoLoad) {
        Model m = autoLoad ? new Model(plugin, namespace, name) : new Model(namespace, name);
        assets.add(m);
        return m;
    }

    /**
     * Registers a blockstate asset.
     *
     * @param name     Block name (e.g. {@code acacia_fence})
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/blockstates/<name>.json}
     * @return Registered blockstate asset
     */
    public BlockState blockstate(String name, boolean autoLoad) {
        BlockState b = autoLoad ? new BlockState(plugin, namespace, name) : new BlockState(namespace, name);
        assets.add(b);
        return b;
    }

    /**
     * Registers a language file asset.
     *
     * @param locale   Locale ID (e.g. {@code en_us})
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/lang/<locale>.json}
     * @return Registered lang asset
     */
    public @NotNull Lang lang(@NotNull String locale, boolean autoLoad) {
        Lang lang = autoLoad ? new Lang(plugin, namespace, locale) : new Lang(namespace, locale);
        assets.add(lang);
        return lang;
    }

    /**
     * Registers a new sound registry asset.
     *
     * @return Registered sounds asset
     */
    public @NotNull Sounds sounds() {
        Sounds s = new Sounds(plugin, namespace);
        assets.add(s);
        return s;
    }

    /**
     * Registers an item definition asset from the plugin JAR.
     *
     * @param name Item name (e.g. {@code custom_sword})
     * @return Registered item definition
     */
    public @NotNull ItemDefinition itemDefinition(@NotNull String name) {
        ItemDefinition def = new ItemDefinition(plugin, namespace, name);
        assets.add(def);
        return def;
    }

    /**
     * Registers a programmatic item definition asset.
     *
     * @param name                 Item name (e.g. {@code custom_sword})
     * @param selector             Item model selector
     * @return Registered item definition
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector) {
        return itemDefinition(name, selector, true, false);
    }
    /**
     * Registers a programmatic item definition asset.
     *
     * @param name                 Item name (e.g. {@code custom_sword})
     * @param selector             Item model selector
     * @param handAnimationOnSwap  Whether to play hand animation on item swap
     * @return Registered item definition
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, boolean handAnimationOnSwap) {
        return itemDefinition(name, selector, handAnimationOnSwap, false);
    }
    /**
     * Registers a programmatic item definition asset.
     *
     * @param name                 Item name (e.g. {@code custom_sword})
     * @param selector             Item model selector
     * @param handAnimationOnSwap  Whether to play hand animation on item swap
     * @param oversizedInGui Whether the model should be oversized or not
     * @return Registered item definition
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, boolean handAnimationOnSwap, boolean oversizedInGui) {
        ItemDefinition def = new ItemDefinition(namespace, name, selector, handAnimationOnSwap, oversizedInGui);
        assets.add(def);
        return def;
    }

    /**
     * Registers an equipment asset.
     *
     * @param path     Asset path (e.g. {@code myarmor})
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/equipment/<path>.json}
     * @return Registered equipment asset
     */
    public Equipment equipment(String path, boolean autoLoad) {
        Equipment e = autoLoad ? new Equipment(plugin, namespace, path) : new Equipment(namespace, path);
        assets.add(e);
        return e;
    }

    /**
     * Registers a generic JSON asset.
     *
     * @param path     Asset path relative to {@code <namespace>/} (no .json)
     * @param autoLoad If true, loads from {@code resourcepack/<namespace>/<path>.json}
     * @return Registered JSON asset
     */
    public JsonAsset json(String path, boolean autoLoad) {
        JsonAsset j = autoLoad ? new JsonAsset(plugin, namespace, path) : new JsonAsset(namespace, path);
        assets.add(j);
        return j;
    }

    /**
     * Emits all registered assets in this namespace into the output file map.
     *
     * @param files Output file map to write to
     */
    public void emit(@NotNull Map<String, byte[]> files) {
        for (Asset asset : assets) {
            asset.emit(files);
        }
    }

    /**
     * Constructs a fully qualified namespaced ID.
     *
     * @param path Path (e.g. {@code sword})
     * @return Namespaced ID (e.g. {@code myplugin:sword})
     */
    public @NotNull String id(@NotNull String path) {
        return namespace + ":" + path;
    }

    /** @return The plugin that owns this namespace */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /** @return This namespace's ID (mod ID) */
    public @NotNull String getNamespace() {
        return namespace;
    }

    /** @return The parent resource pack */
    public @NotNull ResourcePack getPack() {
        return pack;
    }
}
