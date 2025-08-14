package com.github.darksoulq.abyssallib.server.config;

import com.github.darksoulq.abyssallib.server.config.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.server.config.internal.format.ConfigWriter;
import com.github.darksoulq.abyssallib.server.config.internal.format.JsonConfigWriter;
import com.github.darksoulq.abyssallib.server.config.internal.format.YamlConfigWriter;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;

import java.lang.reflect.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public final class ConfigManager {
    private static final Map<Class<?>, Path> registered = new HashMap<>();
    private static final Map<Class<?>, ConfigType> types = new HashMap<>();
    private static final Map<Class<?>, ConfigWriter> writers = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void register(Class<?> clazz, ConfigType type) {
        if (!clazz.isAnnotationPresent(ConfigFile.class))
            throw new IllegalArgumentException("Missing @ConfigFile: " + clazz);

        ConfigFile cf = clazz.getAnnotation(ConfigFile.class);
        String name = cf.name().isEmpty() ? clazz.getSimpleName() : cf.name();
        Path dir = Paths.get("config", cf.id());
        if (!cf.subfolder().isEmpty()) dir = dir.resolve(cf.subfolder());
        try { Files.createDirectories(dir); } catch (IOException e) { throw new RuntimeException(e); }
        Path file = dir.resolve(name + (type == ConfigType.YAML ? ".yml" : ".json"));

        registered.put(clazz, file);
        types.put(clazz, type);
        writers.put(clazz, type == ConfigType.YAML ? new YamlConfigWriter() : new JsonConfigWriter());

        if (!Files.exists(file)) save(clazz);
        else reload(clazz);
    }

    public static void save(Class<?> clazz) {
        Path file = registered.get(clazz);
        if (file == null) throw new IllegalStateException("Not registered: " + clazz);

        validateClass(clazz);

        try {
            writers.get(clazz).writeConfig(clazz, file.toFile());
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @SuppressWarnings("unchecked")
    public static void reload(Class<?> clazz) {
        Path file = registered.get(clazz);
        if (file == null) throw new IllegalStateException("Not registered: " + clazz);

        try {
            ConfigType type = types.get(clazz);
            if (type == ConfigType.YAML) {
                Map<String,Object> data;
                try (Reader r = Files.newBufferedReader(file)) {
                    data = new org.yaml.snakeyaml.Yaml().load(r);
                }
                if (data != null) applyToClass(data, clazz);
            } else {
                // JSON with comments stripping
                List<String> lines = Files.readAllLines(file);
                StringBuilder json = new StringBuilder();
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.startsWith("//")) json.append(line).append("\n");
                }
                Map<String,Object> map = mapper.readValue(json.toString(), Map.class);
                applyToClass(map, clazz);
            }

            validateClass(clazz);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @SuppressWarnings("unchecked")
    private static void applyToClass(Map<String,Object> data, Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            ConfigProperty cp = f.getAnnotation(ConfigProperty.class);
            String key = cp != null && !cp.value().isEmpty() ? cp.value() : f.getName();
            Object val = data.get(key);
            if (val != null) {
                try {
                    f.setAccessible(true);
                    if (SerializerRegistry.has(f.getType()))
                        f.set(null, SerializerRegistry.get(f.getType()).deserialize(val));
                    else f.set(null, val);
                } catch (IllegalAccessException e) { throw new RuntimeException(e); }
            }
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (nested.isAnnotationPresent(Nest.class)) {
                Nest n = nested.getAnnotation(Nest.class);
                String key = n.value().isEmpty() ? nested.getSimpleName() : n.value();
                Object sub = data.get(key);
                if (sub instanceof Map) applyToClass((Map<String,Object>) sub, nested);
            }
        }
    }

    private static void validateClass(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            try {
                f.setAccessible(true);
                Object val = f.get(null);

                Option opt = f.getAnnotation(Option.class);
                if (opt != null && val != null) {
                    String strVal = String.valueOf(val);
                    boolean valid = Arrays.stream(opt.value()).anyMatch(o -> o.equalsIgnoreCase(strVal));
                    if (!valid) throw new IllegalStateException("Invalid value for " + f.getName() + ": " + strVal);
                }

                Range range = f.getAnnotation(Range.class);
                if (range != null && val instanceof Number) {
                    double num = ((Number) val).doubleValue();
                    if (num < range.min() || num > range.max())
                        throw new IllegalStateException("Value out of range for " + f.getName() + ": " + num);
                }

            } catch (IllegalAccessException e) { throw new RuntimeException(e); }
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (nested.isAnnotationPresent(Nest.class)) validateClass(nested);
        }
    }
}
