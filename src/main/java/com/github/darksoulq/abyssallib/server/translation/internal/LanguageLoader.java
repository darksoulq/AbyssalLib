package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * A utility class responsible for discovery and loading of language files.
 * <p>
 * This loader scans the plugin's {@code lang} folder for {@code .properties} files,
 * parses their filenames into {@link Locale} objects, and registers their content
 * with the provided {@link CustomTranslator}.
 */
public class LanguageLoader {
    /** The directory path where external language files are stored. */
    private static final Path LANG_FOLDER = AbyssalLib.getInstance().getDataFolder().toPath().resolve("lang");

    /**
     * Orchestrates the loading process for a translator.
     * <p>
     * If the language folder does not exist, it is created and populated with
     * the default English resource. The method then unregisters existing data
     * and performs a deep scan of the directory to load all valid properties files.
     *
     * @param translator The {@link CustomTranslator} instance to populate.
     */
    public static void load(CustomTranslator translator) {
        if (!Files.exists(LANG_FOLDER)) {
            try {
                Files.createDirectories(LANG_FOLDER);
                loadResource(AbyssalLib.getInstance(), "lang/en_us.properties", translator);
            } catch (IOException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to create lang folder: " + e.getMessage());
                return;
            }
        }

        translator.unregisterAll();

        try (Stream<Path> files = Files.walk(LANG_FOLDER)) {
            files.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".properties"))
                .forEach(path -> loadFile(path, translator));
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to walk lang folder: " + e.getMessage());
        }
    }

    /**
     * Loads a specific language file from the file system.
     *
     * @param path       The {@link Path} to the .properties file.
     * @param translator The {@link CustomTranslator} to receive the data.
     */
    public static void loadFile(Path path, CustomTranslator translator) {
        try (InputStream in = Files.newInputStream(path)) {
            loadStream(in, path.getFileName().toString(), translator);
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to load language file: " + path + " - " + e.getMessage());
        }
    }

    /**
     * Loads a language file embedded within a plugin's JAR as a resource.
     *
     * @param plugin       The {@link Plugin} instance containing the resource.
     * @param resourcePath The path to the resource (e.g., "lang/en_us.properties").
     * @param translator   The {@link CustomTranslator} to receive the data.
     */
    public static void loadResource(Plugin plugin, String resourcePath, CustomTranslator translator) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return;

            String fileName = resourcePath;
            if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            if (fileName.contains("\\")) fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);

            loadStream(in, fileName, translator);
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to load language resource: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Processes an input stream into a {@link ResourceBundle} and registers it.
     *
     * @param in         The {@link InputStream} of the property file.
     * @param fileName   The name of the file (used to derive the Locale).
     * @param translator The target {@link CustomTranslator}.
     * @throws IOException If the stream cannot be read or parsed.
     */
    private static void loadStream(InputStream in, String fileName, CustomTranslator translator) throws IOException {
        String code = fileName.replace(".properties", "");
        Locale locale = parseLocale(code);

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            ResourceBundle bundle = new PropertyResourceBundle(reader);
            translator.register(locale, bundle);
        }
    }

    /**
     * Parses a string code (e.g., "en_us" or "fr") into a {@link Locale} object.
     *
     * @param str The locale string code.
     * @return The corresponding {@link Locale} instance, or {@link Locale#US} as fallback.
     */
    private static Locale parseLocale(String str) {
        String[] parts = str.split("_");
        if (parts.length == 1) return Locale.of(parts[0]);
        if (parts.length == 2) return Locale.of(parts[0], parts[1]);
        if (parts.length >= 3) return Locale.of(parts[0], parts[1], parts[2]);
        return Locale.US;
    }
}