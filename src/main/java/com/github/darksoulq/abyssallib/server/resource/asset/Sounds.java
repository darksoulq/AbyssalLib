package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a collection of custom sounds in a resource pack.
 * Generates {@code assets/<namespace>/sounds.json} and includes all embedded .ogg files.
 */
@ApiStatus.Experimental
public class Sounds implements Asset {

    /**
     * Plugin used to access resources.
     */
    private final Plugin plugin;

    /**
     * Namespace for this group of sounds (e.g., plugin name).
     */
    private final String namespace;

    /**
     * Registered custom sounds mapped by their name (excluding extension).
     */
    private final Map<String, Sound> sounds = new LinkedHashMap<>();

    /**
     * Creates a sound manager for the given namespace.
     *
     * @param plugin    The plugin providing the sound files
     * @param namespace The namespace under which sounds will be registered
     */
    public Sounds(@NotNull Plugin plugin, @NotNull String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    /**
     * Loads a .ogg file from the plugin JAR at {@code resourcepack/<namespace>/sounds/<name>.ogg}
     * and registers it.
     *
     * @param name Name of the sound file (without extension)
     * @return The registered sound
     * @throws RuntimeException If the file is missing or unreadable
     */
    public @NotNull Sound sound(@NotNull String name) {
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/sounds/" + name + ".ogg")) {
            if (in == null) throw new FileNotFoundException(name);
            byte[] data = in.readAllBytes();
            return sound(name, data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to autoload sound: " + name, e);
        }
    }

    /**
     * Registers a custom sound using the provided raw .ogg data.
     *
     * @param name Name of the sound (no extension)
     * @param data Raw bytes of the .ogg file
     * @return The registered sound
     */
    public @NotNull Sound sound(@NotNull String name, byte @NotNull [] data) {
        Sound sound = new Sound(namespace, name, SoundCategory.MASTER, data);
        sounds.put(name, sound);
        return sound;
    }

    /**
     * Emits all registered sounds and the {@code sounds.json} metadata file to the resource pack output.
     *
     * @param output A map of path-to-bytes that the resource pack compiler will use
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> output) {
        JsonObject root = new JsonObject();
        for (Sound sound : sounds.values()) {
            sound.emit(output, root);
        }
        output.put("assets/" + namespace + "/sounds.json",
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(root)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Enum for supported sound categories used in both playback and JSON metadata.
     */
    public enum SoundCategory {
        MASTER, MUSIC, RECORDS, WEATHER, BLOCK, HOSTILE, NEUTRAL, PLAYER, AMBIENT, VOICE;

        /**
         * Converts this category to its Bukkit equivalent.
         *
         * @return Bukkit sound category
         */
        public org.bukkit.SoundCategory toBukkit() {
            return org.bukkit.SoundCategory.valueOf(this.name());
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Represents a single registered custom sound in the resource pack.
     */
    @ApiStatus.Experimental
    public static class Sound {

        /**
         * Namespace under which this sound is registered.
         */
        private final String namespace;

        /**
         * Name of the sound (without extension).
         */
        private final String name;

        /**
         * Sound category for playback and JSON metadata.
         */
        private final SoundCategory category;

        /**
         * Raw byte content of the .ogg sound file.
         */
        private final byte[] data;

        /**
         * Constructs a new custom sound.
         *
         * @param namespace The sound's namespace
         * @param name      The sound's name (no extension)
         * @param category  Playback category
         * @param data      Raw .ogg file bytes
         */
        public Sound(@NotNull String namespace, @NotNull String name, @NotNull SoundCategory category, byte @NotNull [] data) {
            this.namespace = namespace;
            this.name = name;
            this.category = category;
            this.data = data;
        }

        /**
         * Emits this sound to the resource pack output and adds its entry to {@code sounds.json}.
         *
         * @param files      Map of resource pack file outputs
         * @param soundsJson Root sounds.json object being built
         */
        public void emit(@NotNull Map<String, byte[]> files, @NotNull JsonObject soundsJson) {
            String key = namespace + "." + name;
            String assetPath = "assets/" + namespace + "/sounds/" + name + ".ogg";

            files.put(assetPath, data);

            JsonObject entry = new JsonObject();
            entry.addProperty("category", category.toString());

            JsonArray arr = new JsonArray();
            JsonObject fileObj = new JsonObject();
            fileObj.addProperty("name", namespace + ":sounds/" + name);
            fileObj.addProperty("stream", false);
            arr.add(fileObj);
            entry.add("sounds", arr);

            soundsJson.add(key, entry);
        }

        /**
         * Plays the sound at the given player's location with default volume and pitch.
         *
         * @param player Player to hear the sound
         */
        public void play(@NotNull Player player) {
            player.playSound(player.getLocation(), namespace + ":" + name, category.toBukkit(), 1f, 1f);
        }

        /**
         * Plays the sound at the given player's location with custom volume and pitch.
         *
         * @param player Player to hear the sound
         * @param volume Sound volume
         * @param pitch  Sound pitch
         */
        public void play(@NotNull Player player, float volume, float pitch) {
            player.playSound(player.getLocation(), namespace + ":" + name, category.toBukkit(), volume, pitch);
        }

        /**
         * Plays the sound at the given location with default volume and pitch.
         *
         * @param location World location
         */
        public void play(@NotNull Location location) {
            location.getWorld().playSound(location, namespace + ":" + name, category.toBukkit(), 1f, 1f);
        }

        /**
         * Plays the sound at the given location with custom volume and pitch.
         *
         * @param location World location
         * @param volume   Sound volume
         * @param pitch    Sound pitch
         */
        public void play(@NotNull Location location, float volume, float pitch) {
            location.getWorld().playSound(location, namespace + ":" + name, category.toBukkit(), volume, pitch);
        }

        /**
         * Returns the full namespaced ID for this sound.
         *
         * @return Sound ID in the format {@code namespace:name}
         */
        public @NotNull String id() {
            return namespace + ":" + name;
        }
    }
}
