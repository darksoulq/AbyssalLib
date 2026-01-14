package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ItemPredicateLoader {
    private static final Path PREDICATES_FOLDER = new java.io.File(AbyssalLib.getInstance().getDataFolder(), "predicates").toPath();

    public static void loadPredicates() {
        if (!Files.exists(PREDICATES_FOLDER)) {
            try {
                Files.createDirectories(PREDICATES_FOLDER);
            } catch (IOException e) {
                AbyssalLib.LOGGER.severe("Failed to create predicates folder: " + e.getMessage());
                return;
            }
        }

        try (Stream<Path> stream = Files.walk(PREDICATES_FOLDER)) {
            stream.filter(Files::isRegularFile)
                .filter(path -> {
                    String name = path.getFileName().toString().toLowerCase();
                    return name.endsWith(".yml") || name.endsWith(".yaml");
                })
                .forEach(ItemPredicateLoader::loadSingle);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to walk predicates folder: " + e.getMessage());
        }
    }

    private static void loadSingle(Path path) {
        Identifier id = getPredicateId(path);
        if (id == null) return;

        Try.of(() -> {
                try (InputStream in = Files.newInputStream(path)) {
                    Object root = YamlOps.INSTANCE.parse(in);
                    return ItemPredicate.CODEC.decode(YamlOps.INSTANCE, root);
                }
            })
            .onSuccess(predicate -> Registries.PREDICATES.register(id.toString(), predicate))
            .onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load predicate " + id + " from " + path + ": " + e.getMessage()));
    }

    private static Identifier getPredicateId(Path file) {
        Path relative = PREDICATES_FOLDER.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping file " + file + ": Must be inside a namespace folder (predicates/<namespace>/<name>.yml)");
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
        if (lastDot > 0) {
            fullPath = fullPath.substring(0, lastDot);
        }
        return Identifier.of(namespace, fullPath);
    }
}