package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.server.translation.internal.LanguageLoader;
import com.github.darksoulq.abyssallib.server.util.HookConstants;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class FileSetup {

    public static void init(JavaPlugin plugin) {
        HookConstants.load();
        
        File tagFolder = new File(plugin.getDataFolder(), "tags");
        if (!new File(LanguageLoader.LANG_FOLDER.toFile(), "en_us.properties").exists()) {
            plugin.saveResource("lang/en_us.properties", false);
        }
        if (!tagFolder.exists()) {
            tagFolder.mkdir();
        }

        if (!new File(plugin.getDataFolder(), "permission/index.html").exists()) {
            plugin.saveResource("permission/index.html", true);
        }
        if (!new File(plugin.getDataFolder(), "permission/style.css").exists()) {
            plugin.saveResource("permission/style.css", true);
        }
        if (!new File(plugin.getDataFolder(), "permission/app.js").exists()) {
            plugin.saveResource("permission/app.js", true);
        }
    }
}