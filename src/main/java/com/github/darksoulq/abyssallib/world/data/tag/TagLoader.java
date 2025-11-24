package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TagLoader {
    private static final File TAGS_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "tags");

    public static void loadTags() {
        if (!TAGS_FOLDER.exists()) TAGS_FOLDER.mkdirs();
        loadKind(new File(TAGS_FOLDER, "items"), Type.ITEM);
        loadKind(new File(TAGS_FOLDER, "blocks"), Type.BLOCK);
    }

    private static void loadKind(File folder, Type type) {
        if (!folder.exists()) folder.mkdirs();
        if (!folder.isDirectory()) return;

        File[] namespaces = folder.listFiles(File::isDirectory);
        if (namespaces == null) return;

        for (File ns : namespaces) {
            File[] files = ns.listFiles((d, n) -> n.endsWith(".yml") || n.endsWith(".yaml"));
            if (files == null) continue;
            for (File file : files) {
                addValuesToTag(file, type);
            }
        }
    }

    private static void addValuesToTag(File file, Type type) {
        Tag<?> tag = Registries.TAGS.get(getTagId(file).toString());

        if (tag == null) {
            AbyssalLib.LOGGER.warning("Tag file " + file + " does not correspond to an existing tag, skipping.");
            return;
        }

        List<?> values;
        try (InputStream in = new FileInputStream(file)) {
            Object root = YamlOps.INSTANCE.parse(in);
            if (!(root instanceof Map<?, ?> map)) return;
            Object v = map.get("values");
            if (!(v instanceof List<?> list)) return;
            values = list;
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to parse tag file " + file + ": " + e.getMessage());
            return;
        }

        for (Object v : values) {
            if (!(v instanceof String s)) continue;
            boolean include = s.startsWith("#");
            String valueStr = include ? s.substring(1) : s;

            if (include) {
                Tag<?> included = Registries.TAGS.get(valueStr);
                if (included == null) {
                    AbyssalLib.LOGGER.warning("Referenced tag: " + valueStr + " in: "
                            + getTagId(file) + " does not exist, skipping");
                    continue;
                }
                if (type.condition.test(included)) {
                    AbyssalLib.LOGGER.warning("Referenced tag: " + valueStr + " is not "
                            + type.error +", skipping");
                }
            } else {
                tag.add(valueStr);
            }
        }
    }

    private static Identifier getTagId(File file) {
        File nsFolder = file.getParentFile();
        String namespace = nsFolder.getName();
        String path = file.getName().substring(0, file.getName().lastIndexOf('.'));
        return Identifier.of(namespace, path);
    }

    enum Type {
        ITEM(t -> t instanceof ItemTag, "an item tag"),
        BLOCK(t -> t instanceof BlockTag, "a block tag");

        public final Predicate<Tag<?>> condition;
        public final String error;
        Type(Predicate<Tag<?>> condition, String error) {
            this.condition = condition;
            this.error = error;
        }
    }
}
