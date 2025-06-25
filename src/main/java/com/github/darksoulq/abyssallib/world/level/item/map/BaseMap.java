package com.github.darksoulq.abyssallib.world.level.item.map;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.level.data.internal.MapLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a base class for dynamically rendered custom maps in Minecraft.
 * Subclasses define rendering behavior via the {@link #render(MapCanvas, Player)} method.
 * Handles renderer setup, viewer registration, and map metadata persistence.
 */
public abstract class BaseMap {

    /** The owning plugin instance. */
    private final Plugin plugin;

    /** The underlying Bukkit map view object. */
    protected final MapView mapView;

    /**
     * Constructs a new {@code BaseMap} using a provided {@link MapView}.
     * Registers the renderer and saves metadata for reloading later.
     *
     * @param plugin   The plugin owning this map.
     * @param mapView  The map view this map instance should render on.
     */
    public BaseMap(Plugin plugin, MapView mapView) {
        this.plugin = plugin;
        this.mapView = mapView;

        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
                BaseMap.this.render(canvas, player);
            }
        });

        AbyssalLib.EVENT_BUS.register(this);
        saveMetadata();
    }

    /**
     * Creates a new {@link ItemStack} of type {@code FILLED_MAP} bound to this map view.
     *
     * @return A new map item bound to this map.
     */
    public ItemStack createMapItem(Component name, List<Component> lore) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(mapView);
        if (name != null) {
            meta.itemName(name);
        }
        if (lore != null) {
            meta.lore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Returns the plugin that owns this map.
     *
     * @return The owning {@link Plugin}.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Persists metadata (class name and map ID) for reloading this map instance on server restart.
     */
    protected void saveMetadata() {
        MapLoader.saveMetadata(this, this.getClass(), mapView.getId());
    }

    /**
     * Subclasses implement this method to draw custom content onto the map.
     * This is invoked automatically for each player viewing the map.
     *
     * @param canvas The canvas to draw onto.
     * @param player The player currently viewing the map.
     */
    protected abstract void render(MapCanvas canvas, Player player);
}
