package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a single physical `.ogg` audio variant linked to a broader {@link SoundEvent}.
 * <p>
 * This class handles the specific logic and metadata configurations uniquely assigned
 * to an individual audio file, such as localized volume, weight probabilities, and 
 * RAM streaming directives.
 * </p>
 */
public class SoundEntry {

    /**
     * The namespace this entry operates under.
     */
    private final String namespace;

    /**
     * The path string representing this specific file relative to the sounds folder.
     */
    private final String fileName;

    /**
     * The raw byte array data extracted from the source .ogg file.
     */
    private final byte[] data;

    /**
     * A scalar adjusting the base volume strictly for this specific variant file.
     */
    private Float volume;

    /**
     * A scalar adjusting the base pitch strictly for this specific variant file.
     */
    private Float pitch;

    /**
     * The probability weight assigned to this variant. Higher numbers cause it to be played more frequently.
     */
    private Integer weight;

    /**
     * Determines whether the client should stream this audio file directly from disk rather than caching it in RAM.
     */
    private Boolean stream;

    /**
     * Dictates the maximum distance in blocks from which this specific variant can be heard.
     */
    private Integer attenuationDistance;

    /**
     * Instructs the client to forcibly load this audio file into RAM immediately when the resource pack is loaded.
     */
    private Boolean preload;

    /**
     * Determines the type of entry this represents (defaults to "file", but can be set to "event" to nest sounds).
     */
    private String type;

    /**
     * Constructs a new SoundEntry representing a physical audio variant.
     *
     * @param namespace The active namespace managing this asset.
     * @param fileName  The internal logical file path.
     * @param data      The physical byte array corresponding to the .ogg format file.
     */
    protected SoundEntry(@NotNull String namespace, @NotNull String fileName, byte[] data) {
        this.namespace = namespace;
        this.fileName = fileName;
        this.data = data;
    }

    /**
     * Sets the localized base volume for this specific variant.
     *
     * @param volume The volume scalar (default is 1.0).
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry volume(float volume) {
        this.volume = volume;
        return this;
    }

    /**
     * Sets the localized base pitch for this specific variant.
     *
     * @param pitch The pitch scalar (default is 1.0).
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Adjusts the RNG chance that this variant is selected when the parent event is triggered.
     *
     * @param weight The probabilistic weight integer (default is 1).
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry weight(int weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Sets whether this sound should be streamed dynamically from the hard drive.
     * <p>
     * This should always be set to true for long music tracks or dialogue to prevent RAM exhaustion.
     * </p>
     *
     * @param stream True to stream from disk, false to cache into RAM.
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry stream(boolean stream) {
        this.stream = stream;
        return this;
    }

    /**
     * Modifies the distance (in blocks) the sound can traverse before becoming fully attenuated.
     *
     * @param distance The falloff distance integer (default is 16).
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry attenuationDistance(int distance) {
        this.attenuationDistance = distance;
        return this;
    }

    /**
     * Forces the client to cache this sound directly into memory during the resource pack loading screen.
     * <p>
     * Useful for UI sounds that must trigger instantly without disk delay.
     * </p>
     *
     * @param preload True to forcibly load early.
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry preload(boolean preload) {
        this.preload = preload;
        return this;
    }

    /**
     * Sets the internal JSON entry type.
     *
     * @param type The type ("file" by default, or "event" to nest existing events).
     * @return This {@link SoundEntry} instance for chaining.
     */
    public @NotNull SoundEntry type(@Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * Copies the stored physical byte array data directly into the active resource pack map directory.
     *
     * @param files The global map of resource pack files currently being built.
     */
    protected void emitFile(@NotNull Map<String, byte[]> files) {
        files.put("assets/" + namespace + "/sounds/" + fileName + ".ogg", data);
    }

    /**
     * Serializes this specific file variant's localized configurations into the parent event's JSON array.
     *
     * @return A compiled {@link JsonObject} reflecting the variant modifiers.
     */
    protected @NotNull JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("name", namespace + ":" + fileName);
        
        if (volume != null && volume != 1.0f) o.addProperty("volume", volume);
        if (pitch != null && pitch != 1.0f) o.addProperty("pitch", pitch);
        if (weight != null && weight != 1) o.addProperty("weight", weight);
        if (stream != null && stream) o.addProperty("stream", true);
        if (attenuationDistance != null) o.addProperty("attenuation_distance", attenuationDistance);
        if (preload != null && preload) o.addProperty("preload", true);
        if (type != null) o.addProperty("type", type);
        
        return o;
    }
}