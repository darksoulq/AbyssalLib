package com.github.darksoulq.abyssallib.server.data;

import com.github.darksoulq.abyssallib.AbyssalLib;
import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles the loading, compilation, and registration of embedded datapacks from within a plugin JAR.
 * <p>
 * Files located under {@code /data/} inside the plugin JAR are compiled into a ZIP file
 * located at {@code <plugin>/pack/datapack.zip} and registered using Paper's
 * {@link DatapackRegistrar}.
 * </p>
 * <p>
 * This class should be used during the {@code PluginBootstrap#bootstrap} phase.
 * </p>
 */
@ApiStatus.Experimental
public class Datapack {

    /**
     * The plugin instance that owns this datapack.
     */
    private final Plugin plugin;

    /**
     * The namespace or plugin ID associated with this datapack.
     */
    private final String pluginID;

    /**
     * The output path where the compiled datapack ZIP file is written.
     */
    private final Path outputFile;

    /**
     * Map of file paths to file contents that will be included in the final ZIP.
     * The map is sorted to ensure deterministic output order.
     */
    private final TreeMap<String, byte[]> files = new TreeMap<>();

    /**
     * Constructs a new {@code Datapack} instance associated with the specified plugin and mod ID.
     *
     * @param plugin the plugin that owns this datapack
     * @param modid  the namespace or mod ID for this datapack
     */
    public Datapack(@NotNull Plugin plugin, @NotNull String modid) {
        this.plugin = plugin;
        this.pluginID = modid;
        this.outputFile = plugin.getDataFolder().toPath().resolve("pack").resolve("datapack.zip");
    }

    /**
     * Compiles the datapack by extracting files from the plugin JAR's {@code /data/} directory,
     * generating the {@code pack.mcmeta}, and writing all contents into a ZIP file.
     *
     * @throws RuntimeException if an I/O error occurs during processing
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
     * Registers the datapack with Paper using {@link BootstrapContext}.
     * This method should be called during the {@code PluginBootstrap#bootstrap} phase.
     *
     * @param title the display title of the datapack in the UI
     */
    public void register(@NotNull Component title, BootstrapContext ctx) {
        compile();
        ctx.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            try {
                event.registrar().discoverPack(outputFile, pluginID, config -> {
                    config.title(title);
                    config.autoEnableOnServerStart(true);
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to register datapack: " + pluginID, e);
            }
        });
    }

    /**
     * Loads all files under {@code /data/} from the plugin's JAR and adds them to the internal file map.
     *
     * @throws IOException if the JAR file cannot be read
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
     * Reads all bytes from the given {@link InputStream}.
     *
     * @param in the input stream to read from
     * @return a byte array containing the entire contents of the stream
     * @throws IOException if an I/O error occurs
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
     * Generates the contents of the {@code pack.mcmeta} file.
     *
     * @return the JSON string for the {@code pack.mcmeta} file
     */
    private @NotNull String generatePackMeta() {
        return "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 40,\n" +
                "    \"description\": \"" + pluginID + " internal Datapack\"\n" +
                "  }\n" +
                "}";
    }

    /**
     * Adds a UTF-8 encoded text file to the datapack contents.
     *
     * @param path    the internal path of the file within the datapack
     * @param content the file content as a UTF-8 string
     */
    private void put(@NotNull String path, @NotNull String content) {
        files.put(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Adds a binary file to the datapack contents.
     *
     * @param path the internal path of the file within the datapack
     * @param data the file data as a byte array
     */
    private void put(@NotNull String path, byte[] data) {
        files.put(path, data);
    }

    /**
     * Gets the output path of the compiled datapack ZIP file.
     *
     * @return the path to the datapack ZIP file
     */
    public @NotNull Path getOutputFile() {
        return outputFile;
    }
}
