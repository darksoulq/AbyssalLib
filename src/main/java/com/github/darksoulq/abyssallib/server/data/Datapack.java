package com.github.darksoulq.abyssallib.server.data;

import com.github.darksoulq.abyssallib.AbyssalLib;
import io.papermc.paper.datapack.DatapackRegistrar;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads datapack files from {@code /data/} within the plugin JAR and compiles them into a ZIP
 * written to {@code <plugin>/pack/datapack.zip}. To be registered in the {@code PluginLifecycleEvent.Bootstrap}.
 */
@ApiStatus.Experimental
public class Datapack {

    /**
     * Owning plugin instance.
     */
    private final Plugin plugin;

    /**
     * The mod ID / namespace for the datapack (e.g. {@code mymod}).
     */
    private final String modid;

    /**
     * Output path to compiled ZIP file.
     */
    private final Path outputFile;

    /**
     * Files included in the final datapack zip. Keys are relative paths.
     */
    private final TreeMap<String, byte[]> files = new TreeMap<>();

    /**
     * Constructs a new Datapack tied to the specified plugin and modid.
     *
     * @param plugin the plugin owning this datapack
     * @param modid  the mod identifier used in descriptions
     */
    public Datapack(@NotNull Plugin plugin, @NotNull String modid) {
        this.plugin = plugin;
        this.modid = modid;
        this.outputFile = plugin.getDataFolder().toPath().resolve("pack").resolve("datapack.zip");
    }

    /**
     * Compiles the datapack by loading files from the JAR's {@code /data} directory
     * and writing them into a ZIP file. Also generates a default {@code pack.mcmeta}.
     */
    public void compile() {
        files.clear();
        try {
            loadFromJar();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load /data folder from plugin JAR", e);
        }

        put("pack.mcmeta", generatePackMeta());

        try {
            Files.createDirectories(outputFile.getParent());
            try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(outputFile))) {
                for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                    zip.putNextEntry(new ZipEntry(entry.getKey()));
                    zip.write(entry.getValue());
                    zip.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write datapack ZIP: " + outputFile, e);
        }
    }

    /**
     * Registers this datapack via Paper's {@link DatapackRegistrar}.
     * Must be called during the {@code PluginBootstrap#bootstrap} phase.
     *
     * @param title the title shown to the player in the datapack list
     */
    public void register(@NotNull Component title) {
        compile();
        AbyssalLib.DATAPACK_REGISTRAR.register(outputFile, modid, title);
    }

    /**
     * Reads all files in the JAR under {@code /data/} and stores them in the internal file map.
     */
    private void loadFromJar() throws IOException {
        Path jarPath = Paths.get(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!entry.isDirectory() && name.startsWith("data/")) {
                    try (InputStream in = new BufferedInputStream(jar.getInputStream(entry))) {
                        files.put(name, readAllBytes(in));
                    }
                }
            }
        }
    }

    /**
     * Reads an entire input stream into a byte array.
     *
     * @param in the input stream
     * @return all bytes from the stream
     */
    private byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int read;
        while ((read = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
        }
        return buffer.toByteArray();
    }

    /**
     * Generates the default {@code pack.mcmeta} JSON contents.
     *
     * @return the pack.mcmeta file as UTF-8 string
     */
    private @NotNull String generatePackMeta() {
        return "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 40,\n" +
                "    \"description\": \"" + modid + " internal Datapack\"\n" +
                "  }\n" +
                "}";
    }

    /**
     * Inserts a UTF-8 encoded text file into the final pack.
     *
     * @param path    the file path inside the pack
     * @param content the text content
     */
    private void put(@NotNull String path, @NotNull String content) {
        files.put(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Inserts a binary file into the final pack.
     *
     * @param path the file path inside the pack
     * @param data the file contents
     */
    private void put(@NotNull String path, byte[] data) {
        files.put(path, data);
    }

    /**
     * Returns the final output path of the datapack ZIP file.
     *
     * @return the output path
     */
    public @NotNull Path getOutputFile() {
        return outputFile;
    }

    /**
     * Internal-only helper used by AbyssalLib to register datapacks via Paper.
     */
    @ApiStatus.Internal
    public static class Registrar {
        private final DatapackRegistrar registrar;

        public Registrar(@NotNull DatapackRegistrar registrar) {
            this.registrar = registrar;
        }

        public void register(@NotNull Path path, @NotNull String id, @NotNull Component title) {
            try {
                registrar.discoverPack(path, id, config -> {
                    config.title(title);
                    config.autoEnableOnServerStart(true);
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to register datapack: " + id, e);
            }
        }
    }
}
