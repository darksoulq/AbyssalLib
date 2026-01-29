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

public class LanguageLoader {
    private static final Path LANG_FOLDER = AbyssalLib.getInstance().getDataFolder().toPath().resolve("lang");

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

    public static void loadFile(Path path, CustomTranslator translator) {
        try (InputStream in = Files.newInputStream(path)) {
            loadStream(in, path.getFileName().toString(), translator);
        } catch (IOException e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to load language file: " + path + " - " + e.getMessage());
        }
    }

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

    private static void loadStream(InputStream in, String fileName, CustomTranslator translator) throws IOException {
        String code = fileName.replace(".properties", "");
        Locale locale = parseLocale(code);
        
        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            ResourceBundle bundle = new PropertyResourceBundle(reader);
            translator.register(locale, bundle);
        }
    }

    private static Locale parseLocale(String str) {
        String[] parts = str.split("_");
        if (parts.length == 1) return Locale.of(parts[0]);
        if (parts.length == 2) return Locale.of(parts[0], parts[1]);
        if (parts.length >= 3) return Locale.of(parts[0], parts[1], parts[2]);
        return Locale.US;
    }
}