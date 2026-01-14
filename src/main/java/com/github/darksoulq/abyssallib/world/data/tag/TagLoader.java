package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class TagLoader {
    private static final Path TAGS_FOLDER = new java.io.File(AbyssalLib.getInstance().getDataFolder(), "tags").toPath();
    private static final List<TagType<?, ?>> TAG_TYPES = new ArrayList<>();

    public static void loadTags() {
        if (!Files.exists(TAGS_FOLDER)) {
            try { Files.createDirectories(TAGS_FOLDER); }
            catch (IOException e) { e.printStackTrace(); }
        }

        for (TagType<?, ?> type : TAG_TYPES) {
            Path typeFolder = TAGS_FOLDER.resolve(type.folder);
            if (!Files.exists(typeFolder)) continue;
            scanAndRegister(typeFolder, type);
        }
        for (TagType<?, ?> type : TAG_TYPES) {
            Path typeFolder = TAGS_FOLDER.resolve(type.folder);
            if (!Files.exists(typeFolder)) continue;
            parseValues(typeFolder, type);
        }
    }

    private static <T, D> void scanAndRegister(Path folder, TagType<T, D> type) {
        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                .forEach(file -> {
                    Identifier tagId = getTagId(file, folder);
                    if (tagId != null && !Registries.TAGS.contains(tagId.toString())) {
                        Tag<T, D> tag = type.factory.apply(tagId);
                        Registries.TAGS.register(tagId.toString(), tag);
                    }
                });
        } catch (IOException e) {
            AbyssalLib.LOGGER.warning("Failed to scan tag folder " + folder + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, D> void parseValues(Path folder, TagType<T, D> type) {
        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                .forEach(file -> {
                    Identifier tagId = getTagId(file, folder);
                    if (tagId == null) return;

                    Tag<T, D> tag = (Tag<T, D>) Registries.TAGS.get(tagId.toString());
                    if (tag == null) return;

                    List<Object> rawList;
                    try (InputStream in = Files.newInputStream(file)) {
                        Object root = YamlOps.INSTANCE.parse(in);
                        if (!(root instanceof Map<?, ?> map)) return;
                        Object v = map.get("values");
                        if (!(v instanceof List<?> list)) return;
                        rawList = (List<Object>) list;
                    } catch (Exception e) {
                        AbyssalLib.LOGGER.warning("Failed to parse tag file " + file + ": " + e.getMessage());
                        return;
                    }

                    for (Object rawEntry : rawList) {
                        if (rawEntry instanceof String s && s.startsWith("#")) {
                            String refId = s.substring(1);
                            Tag<?, ?> rawIncluded = Registries.TAGS.get(refId);

                            if (rawIncluded == null) {
                                AbyssalLib.LOGGER.warning("Tag " + tagId + " references missing tag: " + refId);
                                continue;
                            }

                            try {
                                tag.include((Tag<T, D>) rawIncluded);
                            } catch (ClassCastException ex) {
                                AbyssalLib.LOGGER.warning("Tag " + tagId + " tried to include incompatible tag type " + refId);
                            }
                            continue;
                        }
                        try {
                            T value = type.codec.decode(YamlOps.INSTANCE, rawEntry);
                            tag.add(value);
                        } catch (Exception e) {
                            AbyssalLib.LOGGER.warning("Error decoding value in tag " + tagId + ": " + e.getMessage());
                        }
                    }
                });
        } catch (IOException e) {
            AbyssalLib.LOGGER.warning("Failed to parse tag values in " + folder + ": " + e.getMessage());
        }
    }

    private static Identifier getTagId(Path file, Path rootFolder) {
        Path relative = rootFolder.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping tag file " + file + ": Must be inside a namespace folder");
            return null;
        }
        String namespace = relative.getName(0).toString();

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) pathBuilder.append("/");
            pathBuilder.append(relative.getName(i).toString());
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) fullPath = fullPath.substring(0, lastDot);

        return Identifier.of(namespace, fullPath);
    }

    public static <T, D> void register(TagType<T, D> type) {
        if (TAG_TYPES.stream().anyMatch(t -> Objects.equals(t.folder, type.folder))) {
            AbyssalLib.LOGGER.warning("TagType folder conflict: " + type.folder);
            return;
        }
        TAG_TYPES.add(type);
    }

    public record TagType<T, D>(String folder, Codec<T> codec, Function<Identifier, Tag<T, D>> factory) {
        public static final TagType<ItemPredicate, ?> ITEM = new TagType<>(
            "items", ItemPredicate.CODEC, ItemTag::new
        );
        public static final TagType<String, ?> BLOCK = new TagType<>(
            "blocks", Codecs.STRING, BlockTag::new
        );
    }
}