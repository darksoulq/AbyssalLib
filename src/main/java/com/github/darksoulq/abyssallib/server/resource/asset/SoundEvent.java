package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a logical sound event within Minecraft (e.g., "entity.zombie.ambient").
 * <p>
 * A SoundEvent acts as the central trigger point that the client listens for. It does
 * not contain audio itself, but instead houses one or more {@link SoundEntry} variants
 * which point to physical .ogg files on the disk.
 * </p>
 */
public class SoundEvent {

    /**
     * The plugin instance used to resolve internal .ogg file streams dynamically.
     */
    private final Plugin plugin;

    /**
     * The namespace this event operates under.
     */
    private final String namespace;

    /**
     * The unique name identifier of the event.
     */
    private final String eventName;

    /**
     * The in-game audio slider category this sound is bound to (defaults to "master").
     */
    private String category = "master";

    /**
     * The localized subtitle text displayed in the bottom right corner when subtitles are enabled.
     */
    private String subtitle;

    /**
     * If true, this event completely replaces any existing Vanilla sound event sharing the same name.
     */
    private boolean replace = false;

    /**
     * A list encompassing all specific .ogg file variants associated with this overarching event.
     */
    private final List<SoundEntry> entries = new ArrayList<>();

    /**
     * Constructs a new SoundEvent representation.
     *
     * @param plugin    The plugin context for file resolution.
     * @param namespace The namespace this event is registered under.
     * @param eventName The key identifier for this event.
     */
    public SoundEvent(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String eventName) {
        this.plugin = plugin;
        this.namespace = namespace;
        this.eventName = eventName;
    }

    /**
     * Adds a physical .ogg file variant to this sound event by attempting to read it from
     * the plugin's internal resources.
     *
     * @param fileName The path of the .ogg file relative to "resourcepack/{namespace}/sounds/".
     * @return The newly generated {@link SoundEntry} to allow for localized variant modifications.
     * @throws RuntimeException If the specified resource path cannot be found or read.
     */
    public @NotNull SoundEntry addVariant(@NotNull String fileName) {
        String path = "resourcepack/" + namespace + "/sounds/" + fileName + ".ogg";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new RuntimeException("Sound file not found: " + path);
            return addVariant(fileName, in.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sound variant: " + fileName, e);
        }
    }

    /**
     * Adds a physical .ogg file variant to this sound event using raw byte data.
     *
     * @param fileName The path string where the file should be placed.
     * @param data     The raw .ogg audio byte array data.
     * @return The newly generated {@link SoundEntry} to allow for localized variant modifications.
     */
    public @NotNull SoundEntry addVariant(@NotNull String fileName, byte[] data) {
        SoundEntry entry = new SoundEntry(namespace, fileName, data);
        entries.add(entry);
        return entry;
    }

    /**
     * Assigns the master sound category governing this event's volume controls.
     *
     * @param category The Bukkit {@link SoundCategory} to apply.
     * @return This {@link SoundEvent} instance for chaining.
     */
    public @NotNull SoundEvent category(@NotNull SoundCategory category) {
        this.category = category.name().toLowerCase();
        return this;
    }

    /**
     * Assigns the translation key or raw text to display when the client has subtitles enabled.
     *
     * @param subtitle The subtitle string.
     * @return This {@link SoundEvent} instance for chaining.
     */
    public @NotNull SoundEvent subtitle(@Nullable String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * Determines whether this custom event should completely overwrite a vanilla event of the same name.
     *
     * @param replace True to replace existing vanilla sounds, false to append variants to them.
     * @return This {@link SoundEvent} instance for chaining.
     */
    public @NotNull SoundEvent replace(boolean replace) {
        this.replace = replace;
        return this;
    }

    /**
     * Plays this sound event for a specific player at their current location with default volume and pitch.
     *
     * @param player The target player.
     */
    public void play(@NotNull Player player) {
        play(player, 1f, 1f);
    }

    /**
     * Plays this sound event for a specific player at their current location with customized volume and pitch.
     *
     * @param player The target player.
     * @param volume The volume scalar.
     * @param pitch  The pitch scalar.
     */
    public void play(@NotNull Player player, float volume, float pitch) {
        player.playSound(player.getLocation(), id(), SoundCategory.valueOf(category.toUpperCase()), volume, pitch);
    }

    /**
     * Plays this sound event at a specific world location for all nearby players with default volume and pitch.
     *
     * @param location The target origin location.
     */
    public void play(@NotNull Location location) {
        play(location, 1f, 1f);
    }

    /**
     * Plays this sound event at a specific world location for all nearby players with customized volume and pitch.
     *
     * @param location The target origin location.
     * @param volume   The volume scalar.
     * @param pitch    The pitch scalar.
     */
    public void play(@NotNull Location location, float volume, float pitch) {
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, id(), SoundCategory.valueOf(category.toUpperCase()), volume, pitch);
        }
    }

    /**
     * Retrieves the fully qualified namespaced ID of this sound event used by Minecraft clients.
     *
     * @return The formatted namespaced ID (e.g., "namespace:event_name").
     */
    public @NotNull String id() {
        return namespace + ":" + eventName;
    }

    /**
     * Iterates through all registered variants and delegates the physical file byte writing process.
     *
     * @param files The global map of resource pack files.
     */
    public void emitFiles(@NotNull Map<String, byte[]> files) {
        for (SoundEntry entry : entries) {
            entry.emitFile(files);
        }
    }

    /**
     * Serializes this event and all its encapsulated entries into the standard sounds.json format.
     *
     * @return A compiled {@link JsonObject} representing the sound event logic.
     */
    @NotNull
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        if (category != null) obj.addProperty("category", category);
        if (subtitle != null) obj.addProperty("subtitle", subtitle);
        if (replace) obj.addProperty("replace", true);

        JsonArray soundsArr = new JsonArray();
        for (SoundEntry e : entries) {
            soundsArr.add(e.toJson());
        }
        obj.add("sounds", soundsArr);
        
        return obj;
    }
}