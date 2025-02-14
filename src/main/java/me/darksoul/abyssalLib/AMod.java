package me.darksoul.abyssalLib;

import me.darksoul.abyssalLib.item.AItem;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AMod {
    private static final List<AMod> _mods = new ArrayList<>();
    private final List<String> _resources = new ArrayList<>();
    private final Plugin plugin;

    public AMod(Plugin plugin) {
        this.plugin = plugin;
        loadResources(plugin);
        setRegistries();
        _mods.add(this);
    }

    public abstract void setRegistries();
    public void register(RegistryType type, AItem item) {
        AItem.getItemsRegistry().put(item.getItem().getItemMeta().getItemModel(), item);
    }

    public Plugin getPlugin() {
        return plugin;
    }
    public List<String> getResources() {
        return _resources;
    }

    private void loadResources(Plugin plugin) {
        List<String> files = FileUtils.getFilePathList(plugin, "assets/");
        _resources.addAll(files);
    }

    public static List<AMod> getMods() {
        return _mods;
    }

    enum RegistryType {
        ITEM,
        BLOCK
    }
}
