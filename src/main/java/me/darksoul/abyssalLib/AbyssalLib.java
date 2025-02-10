package me.darksoul.abyssalLib;

import org.bukkit.plugin.java.JavaPlugin;

public final class AbyssalLib extends JavaPlugin {
    private static AbyssalLib instance;

    public static AbyssalLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
