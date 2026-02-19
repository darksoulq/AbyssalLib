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
 * <p>
 * All emitted files are placed under {@code assets/<namespace>/}. This class
 * provides factory methods for all supported asset types (textures, models, lang, etc.)
 * and maintains a list of registered assets for the final emission process.
 */
public class Namespace {

    /** The Bukkit {@link Plugin} that owns this namespace. */
    private final Plugin plugin;

    /** The string ID of the namespace (e.g., "myplugin"). */
    private final String namespace;

    /** The parent {@link ResourcePack} this namespace belongs to. */
    private final ResourcePack pack;

    /** The list of all {@link Asset} objects registered under this namespace. */
    private final List<Asset> assets = new ArrayList<>();

    /** An optional {@link Texture} used as a visual icon for this namespace in library menus. */
    private Texture icon = null;

    /**
     * Creates a new namespace.
     *
     * @param namespace The namespace string (ID).
     * @param pack      The parent resource pack.
     */
    public Namespace(@NotNull String namespace, @NotNull ResourcePack pack) {
        this.plugin = pack.getPlugin();
        this.namespace = namespace;
        this.pack = pack;
    }

    /**
     * Registers a texture and attempts to load it from the plugin's JAR resources.
     *
     * @param path The relative path to the texture (e.g., "item/sword").
     * @return The registered {@link Texture} asset.
     */
    public @NotNull Texture texture(@NotNull String path) {
        return texture(path, null);
    }

    /**
     * Registers a texture with optional raw byte data.
     *
     * @param path The relative path to the texture.
     * @param data The raw PNG bytes; if null, the system attempts to load from the JAR.
     * @return The registered {@link Texture} asset.
     */
    public @NotNull Texture texture(@NotNull String path, byte @Nullable [] data) {
        Texture t = data == null ? new Texture(plugin, namespace, path) : new Texture(namespace, path, data);
        assets.add(t);
        return t;
    }

    /**
     * Registers an .mcmeta file for texture animations or metadata.
     *
     * @param path     The path matching the texture file.
     * @param autoLoad Whether to automatically pull the file from the plugin JAR.
     * @return The registered {@link McMeta} asset.
     */
    public McMeta mcmeta(String path, boolean autoLoad) {
        McMeta m = autoLoad ? new McMeta(plugin, namespace, path) : new McMeta(namespace, path);
        assets.add(m);
        return m;
    }

    /**
     * Registers an .mcmeta file with provided data.
     *
     * @param path The file path.
     * @param data The raw JSON bytes for the metadata.
     * @return The registered {@link McMeta} asset.
     */
    public McMeta mcmeta(String path, byte[] data) {
        McMeta m = new McMeta(namespace, path, data);
        assets.add(m);
        return m;
    }

    /**
     * Registers a waypoints style asset (common in technical modding libraries).
     *
     * @param name     The style name.
     * @param autoLoad Load from JAR.
     * @return The registered {@link WaypointStyle} asset.
     */
    public WaypointStyle waypointStyle(String name, boolean autoLoad) {
        WaypointStyle w = autoLoad ? new WaypointStyle(plugin, namespace, name) : new WaypointStyle(namespace, name);
        assets.add(w);
        return w;
    }

    /**
     * Registers a waypoint style with provided data.
     *
     * @param name Style name.
     * @param data Raw JSON bytes.
     * @return The registered {@link WaypointStyle} asset.
     */
    public WaypointStyle waypointStyle(String name, byte[] data) {
        WaypointStyle w = new WaypointStyle(namespace, name, data);
        assets.add(w);
        return w;
    }

    /**
     * Registers a post-processing effect asset (Shaders).
     *
     * @param name     Effect name.
     * @param autoLoad Load from JAR.
     * @return The registered {@link PostEffect} asset.
     */
    public PostEffect postEffect(String name, boolean autoLoad) {
        PostEffect p = autoLoad ? new PostEffect(plugin, namespace, name) : new PostEffect(namespace, name);
        assets.add(p);
        return p;
    }

    /**
     * Registers a post-processing effect with provided data.
     *
     * @param name Effect name.
     * @param data Raw JSON bytes.
     * @return The registered {@link PostEffect} asset.
     */
    public PostEffect postEffect(String name, byte[] data) {
        PostEffect p = new PostEffect(namespace, name, data);
        assets.add(p);
        return p;
    }

    /**
     * Registers a font configuration asset.
     *
     * @param name     Font ID.
     * @param autoLoad Load from JAR.
     * @return The registered {@link Font} asset.
     */
    public @NotNull Font font(@NotNull String name, boolean autoLoad) {
        Font f = autoLoad ? new Font(plugin, namespace, name) : new Font(namespace, name);
        assets.add(f);
        return f;
    }

    /**
     * Registers a font configuration with provided data.
     *
     * @param name Font ID.
     * @param data Raw JSON bytes.
     * @return The registered {@link Font} asset.
     */
    public @NotNull Font font(@NotNull String name, byte[] data) {
        Font f = new Font(namespace, name, data);
        assets.add(f);
        return f;
    }

    /**
     * Registers a 3D or 2D item/block model asset.
     *
     * @param name     The name of the model.
     * @param autoLoad Load from JAR.
     * @return The registered {@link Model} asset.
     */
    public @NotNull Model model(@NotNull String name, boolean autoLoad) {
        Model m = autoLoad ? new Model(plugin, namespace, name) : new Model(namespace, name);
        assets.add(m);
        return m;
    }

    /**
     * Registers a model with provided raw data.
     *
     * @param name Model name.
     * @param data Raw JSON bytes.
     * @return The registered {@link Model} asset.
     */
    public @NotNull Model model(@NotNull String name, byte[] data) {
        Model m = new Model(namespace, name, data);
        assets.add(m);
        return m;
    }

    /**
     * Registers a blank model with specific texture resolution hints.
     *
     * @param name      Model name.
     * @param texWidth  Target texture width.
     * @param texHeight Target texture height.
     * @return The registered {@link Model} asset.
     */
    public @NotNull Model model(@NotNull String name, int texWidth, int texHeight) {
        Model m = new Model(namespace, name, texWidth, texHeight);
        assets.add(m);
        return m;
    }

    /**
     * Registers a blockstate configuration asset.
     *
     * @param name     Block ID.
     * @param autoLoad Load from JAR.
     * @return The registered {@link BlockState} asset.
     */
    public BlockState blockstate(String name, boolean autoLoad) {
        BlockState b = autoLoad ? new BlockState(plugin, namespace, name) : new BlockState(namespace, name);
        assets.add(b);
        return b;
    }

    /**
     * Registers a blockstate with provided data.
     *
     * @param name Block ID.
     * @param data Raw JSON bytes.
     * @return The registered {@link BlockState} asset.
     */
    public BlockState blockstate(String name, byte[] data) {
        BlockState b = new BlockState(namespace, name, data);
        assets.add(b);
        return b;
    }

    /**
     * Registers a translation language file (e.g., en_us.json).
     *
     * @param locale   The locale code.
     * @param autoLoad Load from JAR.
     * @return The registered {@link Lang} asset.
     */
    public @NotNull Lang lang(@NotNull String locale, boolean autoLoad) {
        Lang lang = autoLoad ? new Lang(plugin, namespace, locale) : new Lang(namespace, locale);
        assets.add(lang);
        return lang;
    }

    /**
     * Registers a language file with provided data.
     *
     * @param locale Locale code.
     * @param data   Raw JSON bytes.
     * @return The registered {@link Lang} asset.
     */
    public @NotNull Lang lang(@NotNull String locale, byte[] data) {
        Lang lang = new Lang(namespace, locale, data);
        assets.add(lang);
        return lang;
    }

    /**
     * Registers the sounds.json registry for this namespace.
     *
     * @return The registered {@link Sounds} asset.
     */
    public @NotNull Sounds sounds() {
        Sounds s = new Sounds(plugin, namespace);
        assets.add(s);
        return s;
    }

    /**
     * Registers an {@link ItemDefinition} asset from the plugin's JAR.
     *
     * @param name The item name.
     * @return The registered definition.
     */
    public @NotNull ItemDefinition itemDefinition(@NotNull String name) {
        ItemDefinition def = new ItemDefinition(plugin, namespace, name);
        assets.add(def);
        return def;
    }

    /**
     * Registers an {@link ItemDefinition} with provided data.
     *
     * @param name The item name.
     * @param data Raw JSON bytes.
     * @return The registered definition.
     */
    public @NotNull ItemDefinition itemDefinition(@NotNull String name, byte[] data) {
        ItemDefinition def = new ItemDefinition(namespace, name, data);
        assets.add(def);
        return def;
    }

    /**
     * Programmatically registers an item definition using a selector.
     *
     * @param name     Item name.
     * @param selector The {@link Selector} defining which model to show.
     * @return The registered definition.
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector) {
        return itemDefinition(name, selector, true, false, 1.0);
    }

    /**
     * Programmatically registers an item definition with toggleable swap animation.
     *
     * @param name                Item name.
     * @param selector            Model selector.
     * @param handAnimationOnSwap If true, plays the "swing" animation when swapping to this item.
     * @return The registered definition.
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, boolean handAnimationOnSwap) {
        return itemDefinition(name, selector, handAnimationOnSwap, false, 1.0);
    }

    /**
     * Programmatically registers an item definition with custom GUI animation scale.
     *
     * @param name               Item name.
     * @param selector           Model selector.
     * @param swapAnimationScale Scaling factor for the GUI movement on swap.
     * @return The registered definition.
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, double swapAnimationScale) {
        return itemDefinition(name, selector, true, false, swapAnimationScale);
    }

    /**
     * The master method for programmatic item definitions.
     *
     * @param name                Item name.
     * @param selector            Model selector.
     * @param handAnimationOnSwap Swing animation toggle.
     * @param oversizedInGui      Whether the item appears larger in inventories.
     * @param swapAnimationScale  Animation speed/scale.
     * @return The registered definition.
     */
    public ItemDefinition itemDefinition(@NotNull String name, @NotNull Selector selector, boolean handAnimationOnSwap, boolean oversizedInGui, double swapAnimationScale) {
        ItemDefinition def = new ItemDefinition(namespace, name, selector, handAnimationOnSwap, oversizedInGui, swapAnimationScale);
        assets.add(def);
        return def;
    }

    /**
     * Registers an equipment asset for armor or player-held items.
     *
     * @param path     Asset path.
     * @param autoLoad Load from JAR.
     * @return The registered {@link Equipment} asset.
     */
    public Equipment equipment(String path, boolean autoLoad) {
        Equipment e = autoLoad ? new Equipment(plugin, namespace, path) : new Equipment(namespace, path);
        assets.add(e);
        return e;
    }

    /**
     * Registers an equipment asset with provided data.
     *
     * @param path Asset path.
     * @param data Raw JSON bytes.
     * @return The registered {@link Equipment} asset.
     */
    public Equipment equipment(String path, byte[] data) {
        Equipment e = new Equipment(namespace, path, data);
        assets.add(e);
        return e;
    }

    /**
     * Registers a generic JSON file asset within the namespace.
     *
     * @param path     Path relative to assets/ns/ (excluding .json).
     * @param autoLoad Load from JAR.
     * @return The registered {@link JsonAsset}.
     */
    public JsonAsset json(String path, boolean autoLoad) {
        JsonAsset j = autoLoad ? new JsonAsset(plugin, namespace, path) : new JsonAsset(namespace, path);
        assets.add(j);
        return j;
    }

    /**
     * Sets the icon for this Namespace to show in menus using raw image data.
     *
     * @param data The PNG bytes.
     */
    public void icon(byte[] data) {
        icon = texture("item/icon", data);
    }

    /**
     * Autoloads the icon for this Namespace from {@code textures/item/icon.png}.
     */
    public void icon() {
        icon = texture("item/icon");
    }

    /**
     * Iterates through all registered assets and writes them into the provided map.
     *
     * @param files The map containing output file paths and byte data.
     */
    public void emit(@NotNull Map<String, byte[]> files) {
        for (Asset asset : assets) {
            asset.emit(files);
        }
    }

    /**
     * Formats a path into a namespaced ID (e.g., "myplugin:sword").
     *
     * @param path The local path string.
     * @return A namespaced ID.
     */
    public @NotNull String id(@NotNull String path) {
        return namespace + ":" + path;
    }

    /** @return The Bukkit {@link Plugin} associated with this namespace. */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /** @return The ID of this namespace. */
    public @NotNull String getNamespace() {
        return namespace;
    }

    /** @return The parent {@link ResourcePack}. */
    public @NotNull ResourcePack getPack() {
        return pack;
    }

    /** @return The {@link Texture} used as an icon for this namespace, or null. */
    public Texture getIcon() {
        return icon;
    }
}