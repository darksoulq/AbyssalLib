package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Acts as the centralized registry for custom sound events within a specific namespace.
 * <p>
 * This class implements the {@link Asset} interface and is responsible for managing
 * multiple {@link SoundEvent} instances, eventually compiling them into a single
 * {@code sounds.json} file during resource pack generation.
 * </p>
 */
public class Sounds implements Asset {

    /**
     * A pre-configured Gson instance optimized for pretty-printing JSON outputs
     * without escaping standard HTML characters, ensuring a clean sounds.json file.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * The overarching namespace to which all registered sound events in this instance belong.
     */
    private final String namespace;

    /**
     * The Bukkit plugin instance utilized for resolving internal resource files during generation.
     */
    private final Plugin plugin;

    /**
     * A chronologically ordered map linking unique event names to their corresponding {@link SoundEvent} configurations.
     */
    private final Map<String, SoundEvent> events = new LinkedHashMap<>();

    /**
     * Constructs a new Sound registry for a designated namespace.
     *
     * @param plugin    The plugin providing the foundational resource context.
     * @param namespace The namespace prefix used for all JSON definitions and file structures.
     */
    public Sounds(@NotNull Plugin plugin, @NotNull String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    public Sounds(@NotNull Plugin plugin, @NotNull String namespace, byte[] data) {
        this.plugin = plugin;
        this.namespace = namespace;
        parseJson(new String(data, StandardCharsets.UTF_8));
    }

    private void parseJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            String eventName = entry.getKey();
            JsonObject eventObj = entry.getValue().getAsJsonObject();
            SoundEvent event = event(eventName);

            if (eventObj.has("category")) event.category(SoundCategory.valueOf(eventObj.get("category").getAsString().toUpperCase()));
            if (eventObj.has("subtitle")) event.subtitle(eventObj.get("subtitle").getAsString());
            if (eventObj.has("replace")) event.replace(eventObj.get("replace").getAsBoolean());

            if (eventObj.has("sounds")) {
                JsonArray soundsArr = eventObj.getAsJsonArray("sounds");
                for (JsonElement soundEl : soundsArr) {
                    if (soundEl.isJsonPrimitive()) {
                        String name = soundEl.getAsString().replace(namespace + ":", "");
                        event.addVariant(name);
                    } else if (soundEl.isJsonObject()) {
                        JsonObject soundObj = soundEl.getAsJsonObject();
                        String name = soundObj.get("name").getAsString().replace(namespace + ":", "");
                        SoundEntry sEntry = event.addVariant(name);

                        if (soundObj.has("volume")) sEntry.volume(soundObj.get("volume").getAsFloat());
                        if (soundObj.has("pitch")) sEntry.pitch(soundObj.get("pitch").getAsFloat());
                        if (soundObj.has("weight")) sEntry.weight(soundObj.get("weight").getAsInt());
                        if (soundObj.has("stream")) sEntry.stream(soundObj.get("stream").getAsBoolean());
                        if (soundObj.has("attenuation_distance")) sEntry.attenuationDistance(soundObj.get("attenuation_distance").getAsInt());
                        if (soundObj.has("preload")) sEntry.preload(soundObj.get("preload").getAsBoolean());
                        if (soundObj.has("type")) sEntry.type(soundObj.get("type").getAsString());
                    }
                }
            }
        }
    }

    /**
     * Creates or retrieves a {@link SoundEvent} linked to the specified event name.
     * <p>
     * Note: The event name represents the trigger identifier (e.g., "entity.custom_zombie.ambient"),
     * not the actual name of the physical .ogg file.
     * </p>
     *
     * @param eventName The registry name for this sound event.
     * @return The resulting {@link SoundEvent} instance for method chaining and variant registration.
     */
    public @NotNull SoundEvent event(@NotNull String eventName) {
        return events.computeIfAbsent(eventName, name -> new SoundEvent(plugin, namespace, name));
    }

    /**
     * Compiles all registered sound events into the standard {@code sounds.json} structure
     * and exports the physical .ogg files into the final resource pack byte map.
     *
     * @param files The global map of resource pack files where paths act as keys and byte arrays as data.
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonObject root = new JsonObject();

        for (Map.Entry<String, SoundEvent> entry : events.entrySet()) {
            entry.getValue().emitFiles(files);
            root.add(entry.getKey(), entry.getValue().toJson());
        }

        files.put("assets/" + namespace + "/sounds.json", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}