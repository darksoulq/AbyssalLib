package me.darksoul.abyssalLib.config;

import com.google.gson.*;
import me.darksoul.abyssalLib.util.ResourceLocation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Paths.get("config");

    private static final Map<String, ConfigSpec> CONFIGS = new HashMap<>();

    public static ConfigSpec register(String modId, ConfigSpec spec) {
        CONFIGS.put(modId, spec);
        load(modId, spec);
        return spec;
    }

    private static void load(String key, ConfigSpec spec) {
        try {
            Files.createDirectories(CONFIG_DIR);
            Path path = CONFIG_DIR.resolve(key + ".json");

            if (Files.exists(path)) {
                JsonObject json = JsonParser.parseReader(new FileReader(path.toFile())).getAsJsonObject();
                Map<String, JsonElement> flat = flatten(json, "");
                for (Map.Entry<String, JsonElement> entry : flat.entrySet()) {
                    Object parsed = ConfigParser.parseValue(entry.getValue());
                    if (parsed != null) {
                        spec.set(entry.getKey(), parsed);
                    }
                }
            } else {
                save(key, spec);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + key, e);
        }
    }

    public static ConfigSpec get(String modid) {
        return CONFIGS.get(modid);
    }

    public static void reloadAll() {
        CONFIGS.forEach(Config::load);
    }

    public static void saveAll() {
        CONFIGS.forEach(Config::save);
    }

    private static void save(String key, ConfigSpec spec) {
        Path path = CONFIG_DIR.resolve(key + ".json");

        JsonObject root = new JsonObject();

        for (Map.Entry<String, Object> entry : spec.getAllValues().entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            JsonObject current = root;

            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                if (!current.has(part) || !current.get(part).isJsonObject()) {
                    current.add(part, new JsonObject());
                }
                current = current.getAsJsonObject(part);
            }

            String lastKey = parts[parts.length - 1];
            Object value = entry.getValue();
            JsonElement serialized = ConfigParser.serializeValue(value);

            if (serialized != null) {
                current.add(lastKey, serialized);
            }
        }

        try (Writer writer = new FileWriter(path.toFile())) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config: " + key, e);
        }
    }

    private static Map<String, JsonElement> flatten(JsonObject obj, String prefix) {
        Map<String, JsonElement> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            JsonElement val = entry.getValue();

            if (val.isJsonObject()) {
                result.putAll(flatten(val.getAsJsonObject(), key));
            } else {
                result.put(key, val);
            }
        }
        return result;
    }

    public static Set<String> getAllModIDs() {
        return CONFIGS.keySet();
    }
}
