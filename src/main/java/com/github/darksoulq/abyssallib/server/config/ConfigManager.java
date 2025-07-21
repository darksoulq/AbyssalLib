package com.github.darksoulq.abyssallib.server.config;

import com.github.darksoulq.abyssallib.server.config.annotation.ConfigFile;
import com.github.darksoulq.abyssallib.server.config.annotation.Options;
import com.github.darksoulq.abyssallib.server.config.annotation.Range;
import com.github.darksoulq.abyssallib.server.config.internal.ReflectUtil;
import com.github.darksoulq.abyssallib.server.config.internal.format.ConfigFormat;
import com.github.darksoulq.abyssallib.server.config.internal.format.Json5Format;
import com.github.darksoulq.abyssallib.server.config.internal.format.YamlFormat;
import com.github.darksoulq.abyssallib.server.config.serializer.BuiltinSerializers;
import com.github.darksoulq.abyssallib.server.config.serializer.ConfigSerializer;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public class ConfigManager {
    static {
        SerializerRegistry.register(UUID.class, new BuiltinSerializers.UUIDSerializer());
        SerializerRegistry.register(ItemStack.class, new BuiltinSerializers.ItemStackSerializer());
    }

    private static final Map<Class<?>, File> files = new HashMap<>();
    private static final Map<Class<?>, ConfigFormat> formats = new HashMap<>();

    public static <C> void register(Class<C> cls, ConfigType type) {
        try {
            ConfigFile cf = cls.getAnnotation(ConfigFile.class);
            if (cf == null) throw new IllegalArgumentException("Missing @ConfigFile");

            File dir = new File("config/" + cf.pluginId() +
                    (cf.folder().isEmpty() ? "" : "/" + cf.folder()));
            dir.mkdirs();

            String ext = (type == ConfigType.YAML ? ".yml" : ".json5");
            File f = new File(dir, cls.getSimpleName() + ext);
            files.put(cls, f);

            ConfigFormat fmt = (type == ConfigType.YAML ? new YamlFormat() : new Json5Format());
            formats.put(cls, fmt);

            List<String> keys = new ArrayList<>();

            List<Field> fields = new ArrayList<>();

            List<String[]> comments = new ArrayList<>();

            ReflectUtil.collectFields(cls, "", keys, fields, comments);

            Map<String, Object> data = f.exists()
                    ? fmt.parse(Files.readString(f.toPath()))
                    : new LinkedHashMap<>();

            boolean changed = false;

            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Field field = fields.get(i);
                Object defaultVal = field.get(null);
                if (!data.containsKey(key)) {
                    data.put(key, serializeField(field, defaultVal));
                    changed = true;
                } else {
                    Object des = deserializeField(field, data.get(key));
                    validateField(field, des);
                    field.set(null, des);
                }
            }

            if (changed || !f.exists()) save(cls);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <C> void save(Class<C> cls) {
        try {
            File f = files.get(cls);
            ConfigFormat fmt = formats.get(cls);

            List<String> keys = new ArrayList<>();
            List<Field> fields = new ArrayList<>();
            List<String[]> comments = new ArrayList<>();
            ReflectUtil.collectFields(cls, "", keys, fields, comments);

            Map<String, Object> data = new LinkedHashMap<>();
            Map<String, String[]> commentMap = new LinkedHashMap<>();

            for (int i = 0; i < keys.size(); i++) {
                data.put(keys.get(i), serializeField(fields.get(i), fields.get(i).get(null)));
                if (comments.get(i) != null)
                    commentMap.put(keys.get(i), comments.get(i));
            }

            try (FileWriter w = new FileWriter(f)) {
                w.write(fmt.dump(data, commentMap));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <C> void reload(Class<C> cls) {
        try {
            File f = files.get(cls);
            if (!f.exists()) return;
            ConfigFormat fmt = formats.get(cls);
            List<String> keys = new ArrayList<>();
            List<Field> fields = new ArrayList<>();
            ReflectUtil.collectFields(cls, "", keys, fields, new ArrayList<>());

            Map<String, Object> data = fmt.parse(Files.readString(f.toPath()));
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                if (!data.containsKey(key)) continue;
                Object des = deserializeField(fields.get(i), data.get(key));
                validateField(fields.get(i), des);
                fields.get(i).set(null, des);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object serializeField(Field f, Object v) throws Exception {
        ConfigSerializer ser = SerializerRegistry.get(f.getType());
        return (ser != null ? ser.serialize(v) : v);
    }

    @SuppressWarnings("unchecked")
    private static Object deserializeField(Field f, Object raw) throws Exception {
        ConfigSerializer ser = SerializerRegistry.get(f.getType());
        return (ser != null ? ser.deserialize(raw, f) : raw);
    }

    private static void validateField(Field f, Object v) {
        Range r = f.getAnnotation(Range.class);
        if (r != null && v instanceof Number) {
            double d = ((Number) v).doubleValue();
            if (d < r.min() || d > r.max())
                throw new IllegalArgumentException(f.getName() + " out of range");
        }
        Options o = f.getAnnotation(Options.class);
        if (o != null) {
            boolean ok = Arrays.stream(o.options()).anyMatch(x -> x.equals(v.toString()));
            if (!ok) throw new IllegalArgumentException(f.getName() + " invalid option");
        }
    }
}
