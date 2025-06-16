package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a language file (.lang/.json) for Minecraft resource packs.
 * Supports manual addition of translation keys or automatic loading from plugin resources.
 */
@ApiStatus.Experimental
public class Lang implements Asset {

    /**
     * Namespace this language file belongs to (e.g., plugin name).
     */
    private final String namespace;

    /**
     * Language identifier (e.g., en_us).
     */
    private final String lang;

    /**
     * Key-value translation pairs.
     */
    private final Map<String, String> values = new LinkedHashMap<>();

    /**
     * Creates an empty language file.
     *
     * @param namespace the namespace of the resource pack
     * @param lang      the language code (e.g., en_us)
     */
    public Lang(@NotNull String namespace, @NotNull String lang) {
        this.namespace = namespace;
        this.lang = lang;
    }

    /**
     * Loads the language file from the plugin JAR at path:
     * {@code resourcepack/{namespace}/lang/{lang}.json}.
     *
     * @param plugin    the plugin whose JAR is scanned
     * @param namespace the namespace of the resource pack
     * @param lang      the language code (e.g., en_us)
     */
    public Lang(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String lang) {
        this.namespace = namespace;
        this.lang = lang;
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/lang/" + lang + ".json")) {
            if (in != null) {
                JsonObject obj = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    values.put(e.getKey(), e.getValue().getAsString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load lang file", e);
        }
    }

    /**
     * Adds or updates a translation entry.
     *
     * @param key   the translation key (e.g., {@code item.example.name})
     * @param value the translated string (e.g., {@code Example Item})
     */
    public void put(@NotNull String key, @NotNull String value) {
        values.put(key, value);
    }

    /**
     * Emits this language file to the provided file map in JSON format.
     *
     * @param files the map to write generated files into
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonObject json = new JsonObject();
        values.forEach(json::addProperty);
        files.put("assets/" + namespace + "/lang/" + lang + ".json",
                json.toString().getBytes(StandardCharsets.UTF_8));
    }
}
