package com.github.darksoulq.abyssallib.world.data.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.item.map.BaseMap;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * A utility class for loading and saving custom map metadata for plugins using {@link BaseMap}.
 *
 * <p>Maps are saved per-plugin inside a `maps/PluginName/` folder, with a simple `.json` file
 * containing the map's class name and Bukkit map ID. These are used to restore maps on startup.</p>
 *
 * <p>This class is intended to be used internally by {@link BaseMap} and should not be used directly
 * by plugin developers </p>
 */
public final class MapLoader {

    /**
     * The root directory for all map metadata files.
     * Structure: `plugins/AbyssalLib/maps/<PluginName>/*.json`
     */
    private static final File ROOT_DIR = new File(AbyssalLib.getInstance().getDataFolder(), "maps");

    /**
     * Loads all maps from every plugin's map data folder, using serialized class metadata.
     *
     * <p>This method is called once during server start. It uses reflection
     * to instantiate each {@link BaseMap} subclass with the matching plugin and map ID.</p>
     */
    public static void load() {
        if (!ROOT_DIR.exists()) return;

        for (File pluginDir : Objects.requireNonNull(ROOT_DIR.listFiles(File::isDirectory))) {
            loadMapsFromPlugin(pluginDir);
        }
    }

    /**
     * Loads all maps belonging to a specific plugin from its map folder.
     *
     * @param pluginDir The plugin-specific folder inside {@code ROOT_DIR}.
     */
    private static void loadMapsFromPlugin(File pluginDir) {
        File[] mapFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (mapFiles == null) return;

        for (File file : mapFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String className = reader.readLine();
                int mapId = Integer.parseInt(reader.readLine());

                Class<?> clazz = Class.forName(className);
                if (!BaseMap.class.isAssignableFrom(clazz)) continue;

                Constructor<?> constructor = clazz.getConstructor(Plugin.class, MapView.class);
                MapView mapView = Bukkit.getMap((short) mapId);
                if (mapView == null) continue;

                Plugin owningPlugin = Bukkit.getPluginManager().getPlugin(pluginDir.getName());
                if (owningPlugin == null) continue;

                constructor.newInstance(owningPlugin, mapView);

            } catch (Exception e) {
                AbyssalLib.getInstance().getLogger().warning("Failed to load map from: " + file.getName());
            }
        }
    }

    /**
     * Saves the class name and map ID for a {@link BaseMap} instance.
     *
     * <p>Called by {@link BaseMap} upon creation of a new map to persist its metadata.
     * The data is saved under the appropriate plugin's subfolder.</p>
     *
     * @param map    The map instance to save.
     * @param clazz  The class object of the map implementation.
     * @param mapId  The Bukkit map ID to store.
     */
    public static void saveMetadata(BaseMap map, Class<?> clazz, int mapId) {
        Plugin plugin = map.getPlugin();
        File pluginDir = new File(ROOT_DIR, plugin.getName());
        if (!pluginDir.exists()) pluginDir.mkdirs();

        File file = new File(pluginDir, clazz.getSimpleName() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(clazz.getName());
            writer.newLine();
            writer.write(String.valueOf(mapId));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save metadata for map: " + clazz.getSimpleName());
            e.printStackTrace();
        }
    }
}
