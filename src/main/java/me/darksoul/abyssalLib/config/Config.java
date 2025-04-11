package me.darksoul.abyssalLib.config;

import com.google.gson.*;

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

    public static ConfigSpec register(String modId, String name, ConfigSpec spec) {
        String key = modId + ":" + name;
        CONFIGS.put(key, spec);
        load(key, spec);
        return spec;
    }

    private static void load(String key, ConfigSpec spec) {
        try {
            Files.createDirectories(CONFIG_DIR);
            Path path = CONFIG_DIR.resolve(key.replace(':', '-') + ".json");

            if (Files.exists(path)) {
                JsonObject json = JsonParser.parseReader(new FileReader(path.toFile())).getAsJsonObject();
                Map<String, JsonElement> flat = flatten(json, "");

                for (Map.Entry<String, JsonElement> entry : flat.entrySet()) {
                    Object def = spec.getAllDefaults().get(entry.getKey());
                    JsonElement elem = entry.getValue();

                    if (def instanceof List<?> defList && elem.isJsonArray()) {
                        List<Object> list = new ArrayList<>();
                        for (JsonElement element : elem.getAsJsonArray()) {
                            if (!element.isJsonPrimitive()) continue;
                            JsonPrimitive prim = element.getAsJsonPrimitive();
                            if (!defList.isEmpty()) {
                                Object typeHint = defList.getFirst();
                                if (typeHint instanceof Number && prim.isNumber()) list.add(prim.getAsNumber());
                                else if (typeHint instanceof Boolean && prim.isBoolean()) list.add(prim.getAsBoolean());
                                else if (typeHint instanceof String && prim.isString()) list.add(prim.getAsString());
                            } else {
                                if (prim.isString()) list.add(prim.getAsString());
                            }
                        }
                        spec.set(entry.getKey(), list);
                    } else if (def instanceof Number && elem.isJsonPrimitive()) {
                        spec.set(entry.getKey(), elem.getAsNumber());
                    } else if (def instanceof Boolean && elem.isJsonPrimitive()) {
                        spec.set(entry.getKey(), elem.getAsBoolean());
                    } else if (def instanceof String && elem.isJsonPrimitive()) {
                        spec.set(entry.getKey(), elem.getAsString());
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
        Path path = CONFIG_DIR.resolve(key.replace(':', '-') + ".json");

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

            if (value instanceof Number number) {
                current.addProperty(lastKey, number);
            } else if (value instanceof Boolean bool) {
                current.addProperty(lastKey, bool);
            } else if (value instanceof String str) {
                current.addProperty(lastKey, str);
            } else if (value instanceof List<?> list) {
                JsonArray array = new JsonArray();
                for (Object item : list) {
                    if (item instanceof Number n) array.add(n);
                    else if (item instanceof Boolean b) array.add(b);
                    else if (item instanceof String s) array.add(s);
                }
                current.add(lastKey, array);
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
